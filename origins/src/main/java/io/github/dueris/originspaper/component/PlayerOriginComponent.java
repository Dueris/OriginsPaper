package io.github.dueris.originspaper.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.origin.Origin;
import io.github.dueris.originspaper.origin.OriginLayer;
import io.github.dueris.originspaper.origin.OriginLayerManager;
import io.github.dueris.originspaper.origin.OriginManager;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerManager;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class PlayerOriginComponent implements OriginComponent {

	private final Map<OriginLayer, Origin> origins = new ConcurrentHashMap<>();
	private final Player player;

	private boolean selectingOrigin = false;
	private boolean hadOriginBefore = false;

	private int invulnerabilityTicks = 0;

	public PlayerOriginComponent(Player player) {
		this.player = player;
	}

	@Override
	public boolean hasSelectionInvulnerability() {
		return invulnerabilityTicks > 0;
	}

	@Override
	public boolean isSelectingOrigin() {
		return selectingOrigin;
	}

	@Override
	public void selectingOrigin(boolean selectingOrigin) {

		this.selectingOrigin = selectingOrigin;

		if (selectingOrigin && !player.level().isClientSide) {
			invulnerabilityTicks = 60;
		}

	}

	@Override
	public boolean hasAllOrigins() {
		return OriginLayerManager.values()
			.stream()
			.allMatch(layer -> !layer.isEnabled()
				|| (layer.getOrigins().isEmpty() || layer.getOriginOptionCount(player) == 0)
				|| hasOrigin(layer));
	}

	@Override
	public Map<OriginLayer, Origin> getOrigins() {
		return origins;
	}

	@Override
	public boolean hasOrigin(OriginLayer layer) {
		return origins.containsKey(layer)
			&& origins.get(layer) != Origin.EMPTY;
	}

	@Override
	public Origin getOrigin(OriginLayer layer) {
		return origins.get(layer);
	}

	@Override
	public boolean hadOriginBefore() {
		return hadOriginBefore;
	}

	@Override
	public void removeLayer(OriginLayer layer) {

		Origin oldOrigin = getOrigin(layer);
		if (oldOrigin != null) {
			PowerHolderComponent.KEY.get(player).removeAllPowersFromSource(oldOrigin.getId());
		}

		origins.remove(layer);

	}

	@Override
	public void setOrigin(OriginLayer layer, Origin origin) {

		Origin oldOrigin = getOrigin(layer);
		if (origin == oldOrigin) {
			return;
		}

		PowerHolderComponent powerComponent = PowerHolderComponent.KEY.get(player);
		RegistryOps<JsonElement> jsonOps = player.registryAccess().createSerializationContext(JsonOps.INSTANCE);

		if (oldOrigin != null) {

			JsonElement oldOriginJson = Origin.DATA_TYPE.write(jsonOps, oldOrigin).getOrThrow(JsonParseException::new);
			JsonElement originJson = Origin.DATA_TYPE.write(jsonOps, origin).getOrThrow(JsonParseException::new);

			if (!oldOrigin.getId().equals(origin.getId())) {
				PowerHolderComponent.revokeAllPowersFromSource(player, oldOrigin.getId(), true);
			} else if (!oldOriginJson.equals(originJson)) {
				revokeRemovedPowers(origin, powerComponent);
			}

		}

		grantPowersFromOrigin(origin);
		this.origins.put(layer, origin);

		if (this.hasAllOrigins()) {
			this.hadOriginBefore = true;
		}

		if (player instanceof ServerPlayer spe) {
			// ChoseOriginCriterion.INSTANCE.trigger(spe, origin); // TODO - Dueris
		}

	}

	private void grantPowersFromOrigin(@NotNull Origin origin) {
		PowerHolderComponent.grantPowers(this.player, Map.of(origin.getId(), origin.getPowers()), true);
	}

	private void revokeRemovedPowers(@NotNull Origin origin, @NotNull PowerHolderComponent powerComponent) {

		ResourceLocation sourceId = origin.getId();
		List<Power> powers = powerComponent.getPowersFromSource(sourceId)
			.stream()
			.filter(Predicate.not(origin::hasPower))
			.toList();

		boolean revoked = powers
			.stream()
			.map(pt -> powerComponent.removePower(pt, sourceId))
			.reduce(false, Boolean::logicalOr);

		if (revoked) {
			// PowerHolderComponent.PacketHandlers.REVOKE_POWERS.sync(player, Map.of(sourceId, powers)); // Nothing, we are server-sided only.
		}

	}

	@Override
	public void serverTick() {
		if (!selectingOrigin && invulnerabilityTicks > 0) {
			invulnerabilityTicks--;
		}
	}

	@Override
	public void readFromNbt(@NotNull CompoundTag compoundTag, HolderLookup.Provider wrapperLookup) {

		if (player == null) {
			OriginsPaper.LOGGER.error("Player was null in PlayerOriginComponent#fromTag! This is not supposed to happen D:");
			return;
		}

		PowerHolderComponent powerComponent = PowerHolderComponent.KEY.get(player);
		origins.clear();

		//  Migrate origin data from old versions
		if (compoundTag.contains("Origin")) {
			try {

				OriginLayer defaultOriginLayer = OriginLayerManager.get(OriginsPaper.originIdentifier("origin"));
				origins.put(defaultOriginLayer, OriginManager.get(ResourceLocation.parse(compoundTag.getString("Origin"))));

			} catch (Exception ignored) {
				OriginsPaper.LOGGER.warn("Player {} had old origin which could not be migrated: {}", player.getName().getString(), compoundTag.getString("Origin"));
			}
		} else {

			ListTag originLayersNbt = compoundTag.getList("OriginLayers", Tag.TAG_COMPOUND);
			for (int i = 0; i < originLayersNbt.size(); i++) {

				CompoundTag originLayerNbt = originLayersNbt.getCompound(i);
				try {

					ResourceLocation layerId = ResourceLocation.parse(originLayerNbt.getString("Layer"));
					ResourceLocation originId = ResourceLocation.parse(originLayerNbt.getString("Origin"));

					OriginLayer layer = OriginLayerManager.get(layerId);
					Origin origin = OriginManager.get(originId);

					origins.put(layer, origin);

					if (layer.contains(origin) || origin.isSpecial()) {
						continue;
					}

					OriginsPaper.LOGGER.warn("Origin \"{}\" is not in origin layer \"{}\" and is not considered special, but was found on player {}!", originId, layerId, player.getName().getString());

					powerComponent.removeAllPowersFromSource(originId);
					origins.put(layer, Origin.EMPTY);

				} catch (Exception e) {
					OriginsPaper.LOGGER.error("There was a problem trying to read origin NBT data of player {}: {}", player.getName().getString(), e.getMessage());
				}

			}

		}

		selectingOrigin = compoundTag.getBoolean("SelectingOrigin");
		hadOriginBefore = compoundTag.getBoolean("HadOriginBefore");

		if (player.level().isClientSide) {
			return;
		}

		for (Origin origin : origins.values()) {
			//  Grant powers only if the player doesn't have them yet from the specific Origin source.
			//  Needed in case the origin was set before the update to Apoli happened.
			grantPowersFromOrigin(origin);
		}

		for (Origin origin : origins.values()) {
			revokeRemovedPowers(origin, powerComponent);
		}

		//  Compatibility with old worlds. Load power data from Origins' NBT, whereas in new versions, power data is
		//  stored in Apoli's NBT
		if (!compoundTag.contains("Powers")) {
			return;
		}

		ListTag legacyPowersNbt = compoundTag.getList("Powers", Tag.TAG_COMPOUND);
		for (int i = 0; i < legacyPowersNbt.size(); i++) {

			CompoundTag legacyPowerNbt = legacyPowersNbt.getCompound(i);
			String legacyPowerString = legacyPowerNbt.getString("Type");

			try {

				ResourceLocation legacyPowerId = ResourceLocation.parse(legacyPowerString);
				Power legacyPower = PowerManager.get(legacyPowerId);

				if (!powerComponent.hasPower(legacyPower)) {
					continue;
				}

				try {
					Tag legacyPowerData = legacyPowerNbt.get("Data");
					powerComponent.getPowerType(legacyPower).fromTag(legacyPowerData);
				} catch (ClassCastException e) {
					//  Occurs when the power was overridden by a data pack since last world load
					//  where the overridden power now uses different data classes
					OriginsPaper.LOGGER.warn("Data type of power \"{}\" changed, skipping data for that power on entity {}", legacyPowerId, player.getName().getString());
				}


			} catch (IllegalArgumentException e) {
				OriginsPaper.LOGGER.warn("Power data of unregistered power \"{}\" found on player {}, skipping...", legacyPowerString, player.getName().getString());
			}

		}

	}

	@Override
	public void writeToNbt(@NotNull CompoundTag compoundTag, HolderLookup.Provider wrapperLookup) {

		ListTag originLayersNbt = new ListTag();
		origins.forEach((layer, origin) -> {

			CompoundTag originLayerNbt = new CompoundTag();

			originLayerNbt.putString("Layer", layer.getId().toString());
			originLayerNbt.putString("Origin", origin.getId().toString());

			originLayersNbt.add(originLayerNbt);

		});

		compoundTag.put("OriginLayers", originLayersNbt);
		compoundTag.putBoolean("SelectingOrigin", selectingOrigin);
		compoundTag.putBoolean("HadOriginBefore", hadOriginBefore);

	}

	@Override
	public void sync() {
		// Nothing, we are server-sided only.
	}

}
