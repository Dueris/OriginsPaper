package io.github.dueris.originspaper.global;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.dueris.calio.CraftCalio;
import io.github.dueris.calio.data.IdentifiableMultiJsonDataLoader;
import io.github.dueris.calio.data.MultiJsonDataContainer;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.util.TagLike;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerReference;
import io.github.dueris.originspaper.util.PrioritizedEntry;
import io.github.dueris.originspaper.util.fabric.IdentifiableResourceReloadListener;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.Util;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class GlobalPowerSetManager extends IdentifiableMultiJsonDataLoader implements IdentifiableResourceReloadListener {

	public static final Set<ResourceLocation> DEPENDENCIES = Util.make(new HashSet<>(), set -> set.add(OriginsPaper.apoliIdentifier("powers")));
	public static final ResourceLocation ID = OriginsPaper.apoliIdentifier("global_powers");

	private static final Object2ObjectOpenHashMap<ResourceLocation, GlobalPowerSet> SETS_BY_ID = new Object2ObjectOpenHashMap<>();
	private static final ObjectOpenHashSet<ResourceLocation> DISABLED_SETS = new ObjectOpenHashSet<>();

	private static final Map<ResourceLocation, Integer> LOADING_PRIORITIES = new HashMap<>();
	private static final Gson GSON = new GsonBuilder()
		.disableHtmlEscaping()
		.setPrettyPrinting()
		.create();

	public GlobalPowerSetManager() {
		super(GSON, "global_powers", PackType.SERVER_DATA);
	}

	private static GlobalPowerSet merge(RegistryAccess dynamicRegistries, GlobalPowerSet oldSet, GlobalPowerSet newSet) {

		TagLike.Builder<EntityType<?>> oldBuilder = new TagLike.Builder<>(Registries.ENTITY_TYPE);
		TagLike.Builder<EntityType<?>> newBuilder = new TagLike.Builder<>(Registries.ENTITY_TYPE);

		oldSet.getEntityTypes().map(TagLike::entries).ifPresent(oldBuilder::addAll);
		newSet.getEntityTypes().map(TagLike::entries).ifPresent(newBuilder::addAll);

		Set<PowerReference> powerReferences = new ObjectLinkedOpenHashSet<>(oldSet.getPowerReferences());
		int order = oldSet.getOrder();

		if (newSet.shouldReplace()) {

			oldBuilder.clear();
			powerReferences.clear();

			order = newSet.getOrder();

		}

		oldBuilder.addAll(newBuilder);
		powerReferences.addAll(newSet.getPowerReferences());

		Optional<TagLike<EntityType<?>>> entityTypes = oldSet.getEntityTypes().isPresent() || newSet.getEntityTypes().isPresent()
			? Optional.of(oldBuilder.build(dynamicRegistries.lookupOrThrow(Registries.ENTITY_TYPE)))
			: Optional.empty();

		return new GlobalPowerSet(
			entityTypes,
			powerReferences,
			newSet.shouldReplace(),
			order
		);

	}

	public static DataResult<GlobalPowerSet> getResult(ResourceLocation id) {
		return contains(id)
			? DataResult.success(SETS_BY_ID.get(id))
			: DataResult.error(() -> "Couldn't get global power set from ID \"" + id + "\", as it wasn't registered!");
	}

	public static Optional<GlobalPowerSet> getOptional(ResourceLocation id) {
		return getResult(id).result();
	}

	@Nullable
	public static GlobalPowerSet getNullable(ResourceLocation id) {
		return SETS_BY_ID.get(id);
	}

	public static GlobalPowerSet get(ResourceLocation id) {
		return getResult(id).getOrThrow();
	}

	public static Set<Map.Entry<ResourceLocation, GlobalPowerSet>> entrySet() {
		return new ObjectOpenHashSet<>(SETS_BY_ID.entrySet());
	}

	public static Set<ResourceLocation> keySet() {
		return new ObjectOpenHashSet<>(SETS_BY_ID.keySet());
	}

	public static Collection<GlobalPowerSet> values() {
		return new ObjectOpenHashSet<>(SETS_BY_ID.values());
	}

	public static boolean isDisabled(ResourceLocation id) {
		return DISABLED_SETS.contains(id);
	}

	public static boolean contains(ResourceLocation id) {
		return SETS_BY_ID.containsKey(id);
	}

	public static int size() {
		return SETS_BY_ID.size();
	}

	private static GlobalPowerSet remove(ResourceLocation id) {
		return SETS_BY_ID.remove(id);
	}

	public static void disable(ResourceLocation id) {
		remove(id);
		DISABLED_SETS.add(id);
	}

	private static void startBuilding() {

		LOADING_PRIORITIES.clear();

		SETS_BY_ID.clear();
		DISABLED_SETS.clear();

	}

	private static void endBuilding() {

		LOADING_PRIORITIES.clear();

		SETS_BY_ID.trim();
		DISABLED_SETS.trim();

	}

	@Override
	protected void apply(MultiJsonDataContainer prepared, ResourceManager manager, ProfilerFiller profiler) {
		OriginsPaper.LOGGER.info("Reading global power sets from data packs...");

		RegistryAccess dynamicRegistries = CraftCalio.getDynamicRegistries().orElse(null);
		startBuilding();

		if (dynamicRegistries == null) {

			OriginsPaper.LOGGER.error("Can't read global power sets from data packs without access to dynamic registries!");
			endBuilding();

			return;

		}

		Map<ResourceLocation, List<PrioritizedEntry<GlobalPowerSet>>> loadedGlobalPowerSets = new Object2ObjectLinkedOpenHashMap<>();
		prepared.forEach((packName, id, jsonElement) -> {

			try {

				SerializableData.CURRENT_NAMESPACE = id.getNamespace();
				SerializableData.CURRENT_PATH = id.getPath();

				if (!(jsonElement instanceof JsonObject jsonObject)) {
					throw new JsonSyntaxException("Not a JSON object: " + jsonElement);
				}

				GlobalPowerSet globalPowerSet = GlobalPowerSet.DATA_TYPE.read(dynamicRegistries.createSerializationContext(JsonOps.INSTANCE), jsonObject).getOrThrow();
				int currLoadingPriority = GsonHelper.getAsInt(jsonObject, "loading_priority", 0);

				PrioritizedEntry<GlobalPowerSet> entry = new PrioritizedEntry<>(globalPowerSet, currLoadingPriority);
				int prevLoadingPriority = LOADING_PRIORITIES.getOrDefault(id, Integer.MIN_VALUE);

				if (globalPowerSet.shouldReplace() && currLoadingPriority <= prevLoadingPriority) {
					OriginsPaper.LOGGER.warn("Ignoring global power set \"{}\" with 'replace' set to true from data pack [{}]. Its loading priority ({}) must be higher than {} to replace the global power set!", id, packName, currLoadingPriority, prevLoadingPriority);
				} else {

					if (globalPowerSet.shouldReplace()) {
						OriginsPaper.LOGGER.info("Global power set \"{}\" has been replaced by data pack \"{}\"!", id, packName);
					}

					List<String> invalidPowers = globalPowerSet.validate()
						.stream()
						.map(Power::getId)
						.map(ResourceLocation::toString)
						.toList();

					if (!invalidPowers.isEmpty()) {
						OriginsPaper.LOGGER.error("Global power set \"{}\" contained {} invalid power(s): {}", id, invalidPowers.size(), String.join(", ", invalidPowers));
					}

					loadedGlobalPowerSets.computeIfAbsent(id, k -> new LinkedList<>()).add(entry);
					DISABLED_SETS.remove(id);

					LOADING_PRIORITIES.put(id, currLoadingPriority);

				}

			} catch (Exception e) {
				OriginsPaper.LOGGER.error("There was a problem reading global power set \"{}\" (skipping): {}", id, e.getMessage());
			}

		});

		SerializableData.CURRENT_NAMESPACE = null;
		SerializableData.CURRENT_PATH = null;

		OriginsPaper.LOGGER.info("Finished reading global power sets from data packs. Merging similar global power sets...");

		loadedGlobalPowerSets.forEach((id, entries) -> {

			AtomicReference<GlobalPowerSet> currentSet = new AtomicReference<>();
			entries.sort(Comparator.comparing(PrioritizedEntry::priority));

			for (PrioritizedEntry<GlobalPowerSet> entry : entries) {

				if (currentSet.get() == null) {
					currentSet.set(entry.value());
				} else {
					currentSet.accumulateAndGet(entry.value(), (oldSet, newSet) -> merge(dynamicRegistries, oldSet, newSet));
				}

			}

			SETS_BY_ID.put(id, currentSet.get());

		});

		endBuilding();
		OriginsPaper.LOGGER.info("Finished merging similar global power sets. Registry contains {} global power sets.", size());

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

}
