package io.github.dueris.originspaper.origin;

import com.google.gson.*;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.dueris.calio.CraftCalio;
import io.github.dueris.calio.data.IdentifiableMultiJsonDataLoader;
import io.github.dueris.calio.data.MultiJsonDataContainer;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.component.OriginComponent;
import io.github.dueris.originspaper.screen.ChooseOriginScreen;
import io.github.dueris.originspaper.util.PrioritizedEntry;
import io.github.dueris.originspaper.util.fabric.IdentifiableResourceReloadListener;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.Util;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class OriginLayerManager extends IdentifiableMultiJsonDataLoader implements IdentifiableResourceReloadListener {

	public static final Set<ResourceLocation> DEPENDENCIES = Util.make(new ObjectOpenHashSet<>(), set -> set.add(OriginManager.ID));
	public static final ResourceLocation ID = OriginsPaper.originIdentifier("origin_layers");

	private static final Object2ObjectOpenHashMap<ResourceLocation, OriginLayer> LAYERS_BY_ID = new Object2ObjectOpenHashMap<>();

	private static final Map<ResourceLocation, Integer> LOADING_PRIORITIES = new HashMap<>();
	private static final Gson GSON = new GsonBuilder()
		.disableHtmlEscaping()
		.setPrettyPrinting()
		.create();

	public OriginLayerManager() {
		super(GSON, "origin_layers", PackType.SERVER_DATA);
	}

	private static OriginLayer merge(OriginLayer oldLayer, OriginLayer newLayer) {

		if (newLayer.shouldReplace()) {
			return newLayer;
		} else {

			Set<OriginLayer.ConditionedOrigin> origins = new ObjectLinkedOpenHashSet<>(oldLayer.getConditionedOrigins());
			Set<ResourceLocation> originsExcludedFromRandom = new ObjectLinkedOpenHashSet<>(oldLayer.getOriginsExcludedFromRandom());

			if (newLayer.shouldReplaceOrigins()) {
				origins.clear();
			}

			if (newLayer.shouldReplaceExcludedOriginsFromRandom()) {
				originsExcludedFromRandom.clear();
			}

			origins.addAll(newLayer.getConditionedOrigins());
			originsExcludedFromRandom.addAll(newLayer.getOriginsExcludedFromRandom());

			return new OriginLayer(
				oldLayer.getId(),
				oldLayer.getOrder(),
				origins,
				newLayer.shouldReplaceOrigins(),
				newLayer.shouldReplace(),
				oldLayer.isEnabled(),
				oldLayer.getName(),
				oldLayer.getGuiTitle(),
				oldLayer.getMissingName(),
				oldLayer.getMissingDescription(),
				oldLayer.isRandomAllowed(),
				oldLayer.isUnchoosableRandomAllowed(),
				originsExcludedFromRandom,
				newLayer.shouldReplaceExcludedOriginsFromRandom(),
				oldLayer.getDefaultOrigin(),
				oldLayer.shouldAutoChoose(),
				oldLayer.isHidden()
			);

		}

	}

	public static DataResult<OriginLayer> getResult(ResourceLocation id) {
		return LAYERS_BY_ID.containsKey(id)
			? DataResult.success(LAYERS_BY_ID.get(id))
			: DataResult.error(() -> "Could not get layer from id '" + id.toString() + "', as it doesn't exist!");
	}

	public static Optional<OriginLayer> getOptional(ResourceLocation id) {
		return getResult(id).result();
	}

	@Nullable
	public static OriginLayer getNullable(ResourceLocation id) {
		return LAYERS_BY_ID.get(id);
	}

	public static OriginLayer get(ResourceLocation id) {
		return getResult(id).getOrThrow();
	}

	public static Set<Map.Entry<ResourceLocation, OriginLayer>> entrySet() {
		return new ObjectOpenHashSet<>(LAYERS_BY_ID.entrySet());
	}

	public static Set<ResourceLocation> keySet() {
		return new ObjectOpenHashSet<>(LAYERS_BY_ID.keySet());
	}

	public static Collection<OriginLayer> values() {
		return new ObjectOpenHashSet<>(LAYERS_BY_ID.values());
	}

	public static boolean contains(OriginLayer layer) {
		return contains(layer.getId());
	}

	public static boolean contains(ResourceLocation id) {
		return LAYERS_BY_ID.containsKey(id);
	}

	public static int getOriginOptionCount(Player playerEntity) {
		return getOriginOptionCount(playerEntity, (layer, component) -> !component.hasOrigin(layer));
	}

	public static int getOriginOptionCount(Player playerEntity, BiPredicate<OriginLayer, OriginComponent> condition) {
		return values()
			.stream()
			.filter(ol -> ol.isEnabled() && OriginComponent.ORIGIN.maybeGet(playerEntity).map(oc -> condition.test(ol, oc)).orElse(false))
			.flatMapToInt(ol -> IntStream.of(ol.getOriginOptionCount(playerEntity)))
			.sum();
	}

	public static int size() {
		return LAYERS_BY_ID.size();
	}

	private static void startBuilding() {
		LOADING_PRIORITIES.clear();
		LAYERS_BY_ID.clear();
	}

	private static void endBuilding() {
		LOADING_PRIORITIES.clear();
		LAYERS_BY_ID.trim();
	}

	private void updateData(ServerPlayer player, boolean init) {

		RegistryOps<JsonElement> jsonOps = player.registryAccess().createSerializationContext(JsonOps.INSTANCE);
		OriginComponent component = OriginComponent.ORIGIN.get(player);

		int mismatches = 0;

		for (Map.Entry<OriginLayer, Origin> entry : component.getOrigins().entrySet()) {

			OriginLayer oldLayer = entry.getKey();
			OriginLayer newLayer = OriginLayerManager.getNullable(oldLayer.getId());

			Origin oldOrigin = entry.getValue();
			Origin newOrigin = OriginManager.getNullable(oldOrigin.getId());

			if (oldOrigin != Origin.EMPTY) {

				if (newLayer == null) {
					OriginsPaper.LOGGER.error("Removed unregistered origin layer \"{}\" from player {}!", oldLayer.getId(), player.getName().getString());
					component.removeLayer(oldLayer);
				} else if (!newLayer.contains(oldOrigin) || newOrigin == null) {
					OriginsPaper.LOGGER.error("Removed unregistered origin \"{}\" from origin layer \"{}\" from player {}!", oldOrigin.getId(), oldLayer.getId(), player.getName().getString());
					component.setOrigin(newLayer, Origin.EMPTY);
				} else {

					JsonElement oldOriginJson = Origin.DATA_TYPE.write(jsonOps, oldOrigin).getOrThrow(JsonParseException::new);
					JsonElement newOriginJson = Origin.DATA_TYPE.write(jsonOps, newOrigin).getOrThrow(JsonParseException::new);

					if (oldOriginJson.equals(newOriginJson)) {
						continue;
					}

					OriginsPaper.LOGGER.warn("Origin \"{}\" from player {} has mismatched data fields! Updating...", oldOrigin.getId(), player.getName().getString());
					mismatches++;

					component.setOrigin(newLayer, newOrigin);

				}

			}

		}

		if (mismatches > 0) {
			OriginsPaper.LOGGER.info("Finished updating {} origins with mismatched data fields from player {}!", mismatches, player.getName().getString());
		}

		if (component.hasAllOrigins()) {
			component.sync();
		} else {

			component.checkAutoChoosingLayers(player, true);

			if (init) {

				if (component.hasAllOrigins()) {
					OriginComponent.onChosen(player, false);
				} else if (player.isRealPlayer) {
					component.selectingOrigin(true);
					new ChooseOriginScreen(player);
				}

			}

			component.sync();

		}

	}

	@Override
	protected void apply(MultiJsonDataContainer prepared, ResourceManager manager, ProfilerFiller profiler) {

		OriginsPaper.LOGGER.info("Reading origin layers from data packs...");

		RegistryAccess dynamicRegistries = CraftCalio.getDynamicRegistries().orElse(null);
		startBuilding();

		if (dynamicRegistries == null) {

			OriginsPaper.LOGGER.error("Can't read origin layers from data packs without access to dynamic registries!");
			endBuilding();

			return;

		}

		Map<ResourceLocation, List<PrioritizedEntry<OriginLayer>>> loadedLayers = new HashMap<>();
		OriginsPaper.LOGGER.info("Reading origin layers from data packs...");

		prepared.forEach((packName, id, jsonElement) -> {

			try {

				SerializableData.CURRENT_NAMESPACE = id.getNamespace();
				SerializableData.CURRENT_PATH = id.getPath();

				if (!(jsonElement instanceof JsonObject jsonObject)) {
					throw new JsonSyntaxException("Not a JSON object: " + jsonElement);
				}

				jsonObject.addProperty("id", id.toString());

				OriginLayer layer = OriginLayer.DATA_TYPE.read(dynamicRegistries.createSerializationContext(JsonOps.INSTANCE), jsonObject).getOrThrow();
				int currLoadingPriority = GsonHelper.getAsInt(jsonObject, "loading_priority", 0);

				PrioritizedEntry<OriginLayer> entry = new PrioritizedEntry<>(layer, currLoadingPriority);
				int prevLoadingPriority = LOADING_PRIORITIES.getOrDefault(id, Integer.MIN_VALUE);

				if (layer.shouldReplace() && currLoadingPriority <= prevLoadingPriority) {
					OriginsPaper.LOGGER.warn("Ignoring origin layer \"{}\" with 'replace' set to true from data pack [{}]. Its loading priority ({}) must be higher than {} to replace the origin layer!", id, packName, currLoadingPriority, prevLoadingPriority);
				} else {

					if (layer.shouldReplace()) {
						OriginsPaper.LOGGER.info("Origin layer \"{}\" has been replaced by data pack [{}]!", id, packName);
					}

					List<String> invalidOrigins = layer.getConditionedOrigins()
						.stream()
						.map(OriginLayer.ConditionedOrigin::origins)
						.flatMap(Collection::stream)
						.filter(Predicate.not(OriginManager::contains))
						.map(ResourceLocation::toString)
						.toList();

					if (!invalidOrigins.isEmpty()) {
						OriginsPaper.LOGGER.error("Origin layer \"{}\" contained {} invalid origin(s): {}", id, invalidOrigins.size(), String.join(", ", invalidOrigins));
					}

					loadedLayers.computeIfAbsent(id, k -> new LinkedList<>()).add(entry);
					LOADING_PRIORITIES.put(id, currLoadingPriority);

				}

			} catch (Exception e) {
				OriginsPaper.LOGGER.error("There was a problem reading origin layer \"{}\": {}", id, e.getMessage());
			}

		});

		SerializableData.CURRENT_NAMESPACE = null;
		SerializableData.CURRENT_PATH = null;

		OriginsPaper.LOGGER.info("Finished reading {} origin layers. Merging similar origin layers...", loadedLayers.size());
		loadedLayers.forEach((id, entries) -> {

			AtomicReference<OriginLayer> currentLayer = new AtomicReference<>();
			entries.sort(Comparator.comparing(PrioritizedEntry::priority));

			for (PrioritizedEntry<OriginLayer> entry : entries) {

				if (currentLayer.get() == null) {
					currentLayer.set(entry.value());
				} else {
					currentLayer.accumulateAndGet(entry.value(), OriginLayerManager::merge);
				}

			}

			LAYERS_BY_ID.put(id, currentLayer.get());

		});

		endBuilding();
		OriginsPaper.LOGGER.info("Finished merging similar origin layers. Registry contains {} origin layers.", size());

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
