package io.github.dueris.originspaper.origin;

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
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.power.PowerManager;
import io.github.dueris.originspaper.util.fabric.IdentifiableResourceReloadListener;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.Util;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class OriginManager extends IdentifiableMultiJsonDataLoader implements IdentifiableResourceReloadListener {

	public static final Set<ResourceLocation> DEPENDENCIES = Util.make(new HashSet<>(), set -> set.add(PowerManager.ID));
	public static final ResourceLocation ID = OriginsPaper.originIdentifier("origins");

	private static final Object2ObjectOpenHashMap<ResourceLocation, Origin> ORIGINS_BY_ID = new Object2ObjectOpenHashMap<>();
	private static final ObjectOpenHashSet<ResourceLocation> DISABLED_ORIGINS = new ObjectOpenHashSet<>();

	private static final Object2ObjectOpenHashMap<ResourceLocation, Integer> LOADING_PRIORITIES = new Object2ObjectOpenHashMap<>();
	private static final Gson GSON = new GsonBuilder()
		.disableHtmlEscaping()
		.setPrettyPrinting()
		.create();

	public OriginManager() {
		super(GSON, "origins", PackType.SERVER_DATA);
	}

	public static Set<Map.Entry<ResourceLocation, Origin>> entrySet() {
		return new ObjectOpenHashSet<>(ORIGINS_BY_ID.object2ObjectEntrySet());
	}

	public static Set<ResourceLocation> keySet() {
		return new ObjectOpenHashSet<>(ORIGINS_BY_ID.keySet());
	}

	public static Collection<Origin> values() {
		return new ObjectOpenHashSet<>(ORIGINS_BY_ID.values());
	}

	public static DataResult<Origin> getResult(ResourceLocation id) {
		return contains(id)
			? DataResult.success(ORIGINS_BY_ID.get(id))
			: DataResult.error(() -> "Could not get origin from ID \"" + id + "\", as it was not registered!");
	}

	public static Optional<Origin> getOptional(ResourceLocation id) {
		return getResult(id).result();
	}

	@Nullable
	public static Origin getNullable(ResourceLocation id) {
		return ORIGINS_BY_ID.get(id);
	}

	public static Origin get(ResourceLocation id) {
		return getResult(id).getOrThrow();
	}

	public static boolean contains(Origin origin) {
		return contains(origin.getId());
	}

	public static boolean contains(ResourceLocation id) {
		return ORIGINS_BY_ID.containsKey(id);
	}

	public static int size() {
		return ORIGINS_BY_ID.size();
	}

	private static void startBuilding() {

		LOADING_PRIORITIES.clear();

		ORIGINS_BY_ID.clear();
		DISABLED_ORIGINS.clear();

	}

	private static void endBuilding() {

		LOADING_PRIORITIES.clear();
		ORIGINS_BY_ID.put(Origin.EMPTY.getId(), Origin.EMPTY);

		ORIGINS_BY_ID.trim();
		DISABLED_ORIGINS.trim();

	}

	private static Origin register(ResourceLocation id, Origin origin) {

		if (contains(id)) {
			throw new IllegalArgumentException("Tried to register duplicate origin with ID \"" + id + "\"!");
		} else {

			DISABLED_ORIGINS.remove(id);
			ORIGINS_BY_ID.put(id, origin);

			return origin;

		}

	}

	private static Origin remove(ResourceLocation id) {
		return ORIGINS_BY_ID.remove(id);
	}

	private static Origin update(ResourceLocation id, Origin origin) {
		remove(id);
		return register(id, origin);
	}

	public static boolean isDisabled(ResourceLocation id) {
		return DISABLED_ORIGINS.contains(id);
	}

	public static void disable(ResourceLocation id) {
		remove(id);
		DISABLED_ORIGINS.add(id);
	}

	@Override
	protected void apply(MultiJsonDataContainer prepared, ResourceManager manager, ProfilerFiller profiler) {

		OriginsPaper.LOGGER.info("Reading origins from data packs...");

		RegistryAccess dynamicRegistries = CraftCalio.getDynamicRegistries().orElse(null);
		startBuilding();

		if (dynamicRegistries == null) {

			OriginsPaper.LOGGER.error("Can't read origins from data packs without access to dynamic registries!");
			endBuilding();

			return;

		}

		AtomicBoolean hasConfigChanged = new AtomicBoolean(false);
		prepared.forEach((packName, id, jsonElement) -> {

			try {

				SerializableData.CURRENT_NAMESPACE = id.getNamespace();
				SerializableData.CURRENT_PATH = id.getPath();

				if (!(jsonElement instanceof JsonObject jsonObject)) {
					throw new JsonSyntaxException("Not a JSON object: " + jsonElement);
				}

				jsonObject.addProperty("id", id.toString());
				Origin origin = Origin.DATA_TYPE.read(dynamicRegistries.createSerializationContext(JsonOps.INSTANCE), jsonObject).getOrThrow();

				int prevLoadingPriority = LOADING_PRIORITIES.getOrDefault(id, 0);
				int currLoadingPriority = GsonHelper.getAsInt(jsonObject, "loading_priority", 0);

				if (!contains(id)) {

					origin.validate();

					register(id, origin);
					LOADING_PRIORITIES.put(id, currLoadingPriority);

				} else if (prevLoadingPriority < currLoadingPriority) {

					OriginsPaper.LOGGER.warn("Overriding origin \"{}\" (with prev. loading priority of {}) with a higher loading priority of {} from data pack [{}]!", id, prevLoadingPriority, currLoadingPriority, packName);
					origin.validate();

					update(id, origin);
					LOADING_PRIORITIES.put(id, currLoadingPriority);

				}

				origin = get(id);
				// TODO - Dueris
				// hasConfigChanged.set(hasConfigChanged.get() | Origins.config.addToConfig(origin));

				/* if (Origins.config.isOriginDisabled(id)) {
					disable(id);
				} */

			} catch (Exception e) {
				OriginsPaper.LOGGER.error("There was a problem reading origin \"{}\": {}", id, e.getMessage());
			}

		});

		SerializableData.CURRENT_NAMESPACE = null;
		SerializableData.CURRENT_PATH = null;

		OriginsPaper.LOGGER.info("Finished reading origins from data packs. Registry contains {} origins.", size());
		endBuilding();

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
