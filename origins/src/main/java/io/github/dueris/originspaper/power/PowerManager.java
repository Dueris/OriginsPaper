package io.github.dueris.originspaper.power;

import com.google.gson.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.dueris.calio.CraftCalio;
import io.github.dueris.calio.data.IdentifiableMultiJsonDataLoader;
import io.github.dueris.calio.data.MultiJsonDataContainer;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.power.type.PowerType;
import io.github.dueris.originspaper.power.type.PowerTypes;
import io.github.dueris.originspaper.util.fabric.IdentifiableResourceReloadListener;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

public class PowerManager extends IdentifiableMultiJsonDataLoader implements IdentifiableResourceReloadListener {

	public static final Set<ResourceLocation> DEPENDENCIES = new HashSet<>();
	public static final ResourceLocation ID = OriginsPaper.apoliIdentifier("powers");
	private static final Gson GSON = new GsonBuilder()
		.disableHtmlEscaping()
		.setPrettyPrinting()
		.create();
	private static final Set<String> FIELDS_TO_IGNORE = Set.of(
		"condition",
		"loading_priority",
		"fabric:load_conditions"
	);
	private static final Map<ResourceLocation, Integer> LOADING_PRIORITIES = new HashMap<>();
	private static final Object2ObjectOpenHashMap<ResourceLocation, Power> POWERS_BY_ID = new Object2ObjectOpenHashMap<>();
	public static final Codec<ResourceLocation> VALIDATING_CODEC = ResourceLocation.CODEC.comapFlatMap(id -> contains(id) ? DataResult.success(id) : DataResult.error(() -> "Couldn't get power from ID \"" + id + "\", as it wasn't registered!"), Function.identity());
	public static final Codec<Power> DISPATCH_CODEC = ResourceLocation.CODEC.comapFlatMap(
		PowerManager::getResult,
		Power::getId
	);
	public static final StreamCodec<ByteBuf, Power> DISPATCH_PACKET_CODEC = ResourceLocation.STREAM_CODEC.map(
		PowerManager::get,
		Power::getId
	);
	private static final ObjectOpenHashSet<ResourceLocation> DISABLED_POWERS = new ObjectOpenHashSet<>();
	private static boolean building;
	private static boolean validated;

	public PowerManager() {
		super(GSON, "powers", PackType.SERVER_DATA);
	}

	public static void updateData(@NotNull Entity entity, boolean initialize) {

		RegistryOps<JsonElement> jsonOps = entity.registryAccess().createSerializationContext(JsonOps.INSTANCE);
		PowerHolderComponent component = PowerHolderComponent.KEY.getNullable(entity);

		if (component == null) {
			return;
		}

		boolean mismatch = false;
		for (Power oldPower : component.getPowers(true)) {

			ResourceLocation powerId = oldPower.getId();

			if (!contains(powerId)) {

				OriginsPaper.LOGGER.error("Removed unregistered power \"{}\" from entity {}!", powerId, entity.getName().getString());
				mismatch = true;

				for (ResourceLocation sourceId : component.getSources(oldPower)) {
					component.removePower(oldPower, sourceId);
				}

				continue;

			}

			Power newPower = get(powerId);
			PowerType oldPowerType = component.getPowerType(oldPower);

			JsonElement oldPowerJson = Power.CODEC.encodeStart(jsonOps, oldPower).getOrThrow(JsonParseException::new);
			JsonElement newPowerJson = Power.CODEC.encodeStart(jsonOps, newPower).getOrThrow(JsonParseException::new);

			if (oldPowerJson.equals(newPowerJson)) {
				continue;
			}

			OriginsPaper.LOGGER.warn("Mismatched data fields of power \"{}\" from entity {}! Updating...", powerId, entity.getName().getString());
			mismatch = true;

			for (ResourceLocation source : component.getSources(oldPower)) {
				component.removePower(oldPower, source);
				component.addPower(newPower, source);
			}

			PowerType newPowerType = component.getPowerType(newPower);
			if (oldPowerType.getClass().isAssignableFrom(newPowerType.getClass())) {
				//  Transfer the data of the old power to the new power if the old power is an instance of the new power
				OriginsPaper.LOGGER.info("Successfully transferred old data of power \"{}\"!", powerId);
				newPowerType.fromTag(oldPowerType.toTag());
			} else {
				//  Output a warning that the data of the old power couldn't be transferred to the new power. This usually
				//  occurs if the power no longer uses the same power type as it used to
				OriginsPaper.LOGGER.warn("Couldn't transfer old data of power \"{}\", as it's using a different power type!", powerId);
			}

		}

		if (initialize || mismatch) {

			if (mismatch) {
				OriginsPaper.LOGGER.info("Finished updating power data of entity {}.", entity.getName().getString());
			}

		}

	}

	private static Power register(ResourceLocation id, Power power) {

		if (!building) {
			return power;
		} else if (POWERS_BY_ID.containsKey(id)) {
			throw new IllegalArgumentException("Tried to register duplicate power with ID \"" + id + "\"");
		} else {

			DISABLED_POWERS.remove(id);
			POWERS_BY_ID.put(id, power);

			return power;

		}

	}

	private static Power update(ResourceLocation id, Power power) {

		if (!building) {
			return power;
		} else {

			if (remove(id) instanceof MultiplePower removedMultiplePower) {
				removedMultiplePower.getSubPowers()
					.stream()
					.map(Power::getId)
					.forEach(PowerManager::remove);
			}

			return register(id, power);

		}

	}

	private static @Nullable Power remove(ResourceLocation id) {

		if (building) {
			return POWERS_BY_ID.remove(id);
		} else {
			return null;
		}

	}

	public static void disable(ResourceLocation id) {
		remove(id);
		DISABLED_POWERS.add(id);
	}

	/**
	 * Validates all registered powers.
	 */
	public static void validate() {

		if (POWERS_BY_ID.isEmpty()) {
			return;
		}

		OriginsPaper.LOGGER.info("Validating {} powers...", size());

		Iterator<Map.Entry<ResourceLocation, Power>> powerTypeIterator = POWERS_BY_ID.entrySet().iterator();
		validated = true;

		while (powerTypeIterator.hasNext()) {

			Map.Entry<ResourceLocation, Power> powerTypeEntry = powerTypeIterator.next();

			ResourceLocation id = powerTypeEntry.getKey();
			Power power = powerTypeEntry.getValue();

			try {
				power.validate();
			} catch (Exception e) {

				StringBuilder errorBuilder = new StringBuilder("There was a problem validating ");
				powerTypeIterator.remove();

				if (power instanceof SubPower subPowerType) {
					errorBuilder
						.append("sub-power \"")
						.append(subPowerType.getSubName())
						.append("\" in power \"")
						.append(subPowerType.getSuperPowerId())
						.append("\"");
				} else {
					errorBuilder
						.append("power \"")
						.append(id)
						.append("\"");
				}

				OriginsPaper.LOGGER.error(errorBuilder
					.append(" (removing): ")
					.append(e.getMessage()));

			}

		}

	}

	private static void startBuilding() {

		if (building) {
			return;
		}

		building = true;
		validated = false;

		POWERS_BY_ID.clear();
		DISABLED_POWERS.clear();

		LOADING_PRIORITIES.clear();

	}

	private static void endBuilding() {
		building = false;
	}

	public static DataResult<Power> getResult(ResourceLocation id) {

		try {
			return DataResult.success(get(id));
		} catch (Exception e) {
			return DataResult.error(e::getMessage);
		}

	}

	public static Optional<Power> getOptional(ResourceLocation id) {
		return Optional.ofNullable(POWERS_BY_ID.get(id));
	}

	public static Power get(ResourceLocation id) {
		return getOptional(id).orElseThrow(() -> new IllegalArgumentException("Couldn't get power from ID \"" + id + "\", as it wasn't registered!"));
	}

	public static @NotNull Set<Map.Entry<ResourceLocation, Power>> entrySet() {
		return new ObjectArraySet<>(POWERS_BY_ID.entrySet());
	}

	public static @NotNull Collection<Power> values() {
		return new ObjectArrayList<>(POWERS_BY_ID.values());
	}

	public static Stream<ResourceLocation> streamIds() {
		return new ObjectArraySet<>(POWERS_BY_ID.keySet()).stream();
	}

	public static void forEach(BiConsumer<ResourceLocation, Power> biConsumer) {
		POWERS_BY_ID.forEach(biConsumer);
	}

	public static boolean isDisabled(ResourceLocation id) {
		return DISABLED_POWERS.contains(id);
	}

	public static boolean contains(ResourceLocation id) {
		return POWERS_BY_ID.containsKey(id);
	}

	public static Set<ResourceLocation> keySet() {
		return new ObjectOpenHashSet<>(POWERS_BY_ID.keySet());
	}

	public static boolean contains(@NotNull Power power) {
		return contains(power.getId());
	}

	public static int size() {
		return POWERS_BY_ID.size();
	}

	public static boolean shouldIgnoreField(@NotNull String field) {
		return field.isEmpty()
			|| field.startsWith("$")
			|| FIELDS_TO_IGNORE.contains(field)
			|| Power.DATA.containsField(field);
	}

	@Override
	protected void apply(MultiJsonDataContainer prepared, ResourceManager manager, ProfilerFiller profiler) {

		OriginsPaper.LOGGER.info("Reading powers from data packs...");

		startBuilding();
		RegistryAccess dynamicRegistries = CraftCalio.getDynamicRegistries().orElse(null);

		if (dynamicRegistries == null) {

			OriginsPaper.LOGGER.error("Can't read powers from data packs without access to dynamic registries!");
			endBuilding();

			return;

		}

		prepared.forEach((packName, id, jsonElement) -> {

			try {

				SerializableData.CURRENT_NAMESPACE = id.getNamespace();
				SerializableData.CURRENT_PATH = id.getPath();

				if (jsonElement instanceof JsonObject jsonObject) {
					this.readMultipleOrNormalPower(dynamicRegistries, packName, id, jsonObject);
				} else {
					throw new JsonSyntaxException("Not a JSON object: " + jsonElement);
				}

			} catch (Exception e) {
				OriginsPaper.LOGGER.error("There was a problem reading power \"{}\" from data pack [{}]: {}", id, packName, e.getMessage());
			}

		});

		SerializableData.CURRENT_NAMESPACE = null;
		SerializableData.CURRENT_PATH = null;

		endBuilding();
		OriginsPaper.LOGGER.info("Finished reading powers from data packs. Registry contains {} powers.", size());

		validate();

		if (validated) {
			OriginsPaper.LOGGER.info("Finished validating powers from data packs. Registry contains {} powers.", size());
		}

	}

	@Override
	public void onReject(String packName, ResourceLocation resourceId) {

		if (!contains(resourceId)) {
			disable(resourceId);
		}

	}

	@Override
	public ResourceLocation getFabricId() {
		return ID;
	}

	@Override
	public Collection<ResourceLocation> getFabricDependencies() {
		return DEPENDENCIES;
	}

	private void readMultipleOrNormalPower(HolderLookup.@NotNull Provider wrapperLookup, String packName, @NotNull ResourceLocation powerId, @NotNull JsonObject powerJson) {

		powerJson.addProperty("id", powerId.toString());

		Power basePower = Power.CODEC.parse(wrapperLookup.createSerializationContext(JsonOps.INSTANCE), powerJson).getOrThrow(JsonParseException::new);

		if (basePower.isMultiple()) {

			Power supposedMultiplePower = this.readPower(packName, new MultiplePower(basePower), powerJson);
			Set<ResourceLocation> subPowerIds = new ObjectLinkedOpenHashSet<>();

			powerJson.asMap().forEach((key, jsonElement) -> {

				if (shouldIgnoreField(key)) {
					return;
				}

				try {

					if (!ResourceLocation.isValidPath(key)) {
						throw new ResourceLocationException("Non [a-z0-9/._-] character in sub-power name \"" + key + "\"!");
					} else if (jsonElement instanceof JsonObject subPowerJson) {

						ResourceLocation subPowerId = powerId.withSuffix("_" + key);

						if (this.readSubPower(wrapperLookup, packName, powerId, subPowerId, key, subPowerJson)) {
							subPowerIds.add(subPowerId);
						}

					} else {
						throw new JsonSyntaxException("Not a JSON object: " + jsonElement);
					}

				} catch (Exception e) {
					OriginsPaper.LOGGER.error("There was a problem reading sub-power \"{}\" in power \"{}\" from data pack [{}]: {}", key, powerId, packName, e.getMessage());
				}

			});

			if (supposedMultiplePower instanceof MultiplePower multiplePower) {
				multiplePower.setSubPowerIds(subPowerIds);
			} else if (isDisabled(powerId)) {
				subPowerIds.forEach(PowerManager::disable);
			}

		} else {
			this.readPower(packName, basePower, powerJson);
		}

	}

	private boolean readSubPower(HolderLookup.@NotNull Provider wrapperLookup, String packName, ResourceLocation superPowerId, @NotNull ResourceLocation subPowerId, String name, @NotNull JsonObject subPowerJson) {
		subPowerJson.addProperty("id", subPowerId.toString());
		Power basePower = Power.CODEC.parse(wrapperLookup.createSerializationContext(JsonOps.INSTANCE), subPowerJson).getOrThrow(JsonParseException::new);

		SubPower subPower = switch (this.readPower(packName, new SubPower(superPowerId, name, basePower), subPowerJson)) {
			case SubPower selfSubPower -> selfSubPower;
			case Power power -> new SubPower(superPowerId, name, power);
			case null -> null;
		};

		if (subPower != null && subPower.isMultiple()) {
			throw new IllegalStateException("Using the '" + PowerTypes.MULTIPLE.getSerializerId() + "' power type in sub-powers is not allowed!");
		} else {
			return subPower != null;
		}

	}

	@Nullable
	private <P extends Power> Power readPower(String packName, @NotNull P power, JsonObject powerJson) {

		ResourceLocation powerId = power.getId();

		int priority = GsonHelper.getAsInt(powerJson, "loading_priority", 0);
		int previousPriority = LOADING_PRIORITIES.computeIfAbsent(powerId, k -> priority);

		if (!contains(powerId)) {
			return this.finishReadingPower(PowerManager::register, powerId, power, powerJson, priority);
		} else if (previousPriority < priority) {

			StringBuilder overrideMessage = new StringBuilder("Overriding ");
			if (power instanceof SubPower subPower) {
				overrideMessage
					.append("sub-power \"")
					.append(subPower.getSubName())
					.append("\" of power \"")
					.append(subPower.getSuperPowerId())
					.append("\"");
			} else {
				overrideMessage.append("power \"")
					.append(power.getId())
					.append("\"");
			}

			OriginsPaper.LOGGER.warn(overrideMessage
				.append(" (with a previous loading priority of ")
				.append(previousPriority)
				.append(") with the same power that has a higher loading priority of ")
				.append(priority)
				.append(" from data pack [")
				.append(packName)
				.append("]!"));

			return this.finishReadingPower(PowerManager::update, powerId, power, powerJson, priority);

		} else {

			StringBuilder overrideHint = new StringBuilder("Ignoring ");
			if (power instanceof SubPower subPower) {
				overrideHint
					.append("sub-power \"")
					.append(subPower.getSubName())
					.append("\" of power \"")
					.append(subPower.getSuperPowerId())
					.append("\"");
			} else {
				overrideHint.append("power \"")
					.append(power.getId())
					.append("\"");
			}

			OriginsPaper.LOGGER.warn(overrideHint
				.append(" from data pack [")
				.append(packName)
				.append("]. Its loading priority must be higher than ")
				.append(previousPriority)
				.append(" in order to override the same power added by a previous data pack!"));

			return power.isSubPower()
				? get(powerId)
				: null;

		}

	}

	private <P extends Power> P finishReadingPower(@NotNull BiFunction<ResourceLocation, Power, Power> powerTypeProcessor, ResourceLocation powerId, P power, JsonObject jsonObject, int priority) {

		powerTypeProcessor.apply(powerId, power);
		LOADING_PRIORITIES.put(powerId, priority);

		return power;

	}

}

