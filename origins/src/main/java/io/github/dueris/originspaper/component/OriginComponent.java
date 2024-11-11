package io.github.dueris.originspaper.component;

import io.github.dueris.originspaper.origin.Origin;
import io.github.dueris.originspaper.origin.OriginLayer;
import io.github.dueris.originspaper.origin.OriginLayerManager;
import io.github.dueris.originspaper.origin.OriginManager;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.type.ModifyPlayerSpawnPowerType;
import io.github.dueris.originspaper.power.type.PowerType;
import io.github.dueris.originspaper.util.ProvidableComponentKey;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public interface OriginComponent {
	ProvidableComponentKey<OriginComponent, Player> ORIGIN = new ProvidableComponentKey<>(PlayerOriginComponent::new);

	static void onChosen(Player player, boolean hadOriginBefore) {

		if (!hadOriginBefore) {
			PowerHolderComponent.getPowerTypes(player, ModifyPlayerSpawnPowerType.class)
				.stream()
				.max(Comparator.comparing(ModifyPlayerSpawnPowerType::getPriority))
				.ifPresent(ModifyPlayerSpawnPowerType::teleportToModifiedSpawn);
		}

//		PowerHolderComponent.withPowerTypes(player, OriginsCallbackPowerType.class, p -> true, p -> p.onChosen(hadOriginBefore)); // TODO

	}

	static void partialOnChosen(Player player, boolean hadOriginBefore, @NotNull Origin origin) {

		PowerHolderComponent powerHolder = PowerHolderComponent.KEY.get(player);

		for (Power power : powerHolder.getPowersFromSource(origin.getId())) {

			PowerType powerType = powerHolder.getPowerType(power);

			if (powerType instanceof ModifyPlayerSpawnPowerType mps && !hadOriginBefore) {
				mps.teleportToModifiedSpawn();
			} /* else if (powerType instanceof OriginsCallbackPowerType ocp) {
				ocp.onChosen(hadOriginBefore);
			} */ // TODO

		}

	}

	Map<OriginLayer, Origin> getOrigins();

	Origin getOrigin(OriginLayer layer);

	boolean hasSelectionInvulnerability();

	boolean isSelectingOrigin();

	boolean hasOrigin(OriginLayer layer);

	boolean hasAllOrigins();

	boolean hadOriginBefore();

	void selectingOrigin(boolean selectingOrigin);

	void removeLayer(OriginLayer layer);

	void setOrigin(OriginLayer layer, Origin origin);

	void sync();

	default boolean checkAutoChoosingLayers(Player player, boolean includeDefaults) {

		List<OriginLayer> layers = new ArrayList<>();
		boolean choseOneAutomatically = false;

		OriginLayerManager.values()
			.stream()
			.filter(OriginLayer::isEnabled)
			.forEach(layers::add);

		Collections.sort(layers);
		for (OriginLayer layer : layers) {

			if (!layer.isEnabled() || hasOrigin(layer)) {
				continue;
			}

			if (includeDefaults && layer.hasDefaultOrigin()) {

				setOrigin(layer, OriginManager.get(layer.getDefaultOrigin()));
				choseOneAutomatically = true;

			} else if (layer.getOriginOptionCount(player) == 1 && layer.shouldAutoChoose()) {

				List<Origin> origins = layer.getOrigins(player)
					.stream()
					.map(OriginManager::get)
					.filter(Origin::isChoosable)
					.toList();

				if (!origins.isEmpty()) {

					setOrigin(layer, origins.getFirst());
					choseOneAutomatically = true;

				} else if (layer.isRandomAllowed() && !layer.getRandomOrigins(player).isEmpty()) {

					List<ResourceLocation> randomOriginIds = layer.getRandomOrigins(player);
					int randomOriginIndex = player.getRandom().nextInt(randomOriginIds.size());

					setOrigin(layer, OriginManager.get(randomOriginIds.get(randomOriginIndex)));
					choseOneAutomatically = true;

				}

			}

		}

		return choseOneAutomatically;

	}

	void serverTick();

	void readFromNbt(CompoundTag tag, HolderLookup.Provider lookup);

	void writeToNbt(CompoundTag compoundTag, HolderLookup.Provider lookup);
}
