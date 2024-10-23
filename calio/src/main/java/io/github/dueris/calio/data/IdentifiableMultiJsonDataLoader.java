package io.github.dueris.calio.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.parsers.json.JsonFormat;
import org.quiltmc.parsers.json.JsonReader;
import org.quiltmc.parsers.json.gson.GsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class IdentifiableMultiJsonDataLoader extends ExtendedSinglePreparationResourceReloader<MultiJsonDataContainer> implements IExtendedJsonDataLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger(IdentifiableMultiJsonDataLoader.class);
	@Nullable
	protected final PackType resourceType;
	protected final String directoryName;
	private final Gson gson;

	public IdentifiableMultiJsonDataLoader(Gson gson, String directoryName) {
		this(gson, directoryName, null);
	}

	public IdentifiableMultiJsonDataLoader(Gson gson, String directoryName, @Nullable PackType resourceType) {
		this.gson = gson;
		this.directoryName = directoryName;
		this.resourceType = resourceType;
	}

	@Override
	protected MultiJsonDataContainer prepare(@NotNull ResourceManager manager, ProfilerFiller profiler) {

		MultiJsonDataContainer prepared = new MultiJsonDataContainer();
		manager.listResourceStacks(directoryName, this::hasValidFormat).forEach((fileId, resources) -> {

			ResourceLocation resourceId = this.trim(fileId, directoryName);
			String fileExtension = "." + FilenameUtils.getExtension(fileId.getPath());

			JsonFormat jsonFormat = this.getValidFormats().get(fileExtension);
			resources.forEach(resource -> {

				String packName = resource.sourcePackId();
				try (Reader resourceReader = resource.openAsReader()) {

					GsonReader gsonReader = new GsonReader(JsonReader.create(resourceReader, jsonFormat));
					JsonElement jsonElement = gson.fromJson(gsonReader, JsonElement.class);

					if (jsonElement == null) {
						throw new JsonParseException("JSON cannot be empty!");
					} else {
						prepared
							.computeIfAbsent(resourceId, k -> new LinkedHashSet<>())
							.add(MultiJsonDataContainer.entry(packName, jsonElement));
					}

				} catch (Exception e) {
					this.onError(packName, resourceId, fileExtension, e);
				}

			});

		});

		return prepared;

	}

	@Override
	protected void preApply(@NotNull MultiJsonDataContainer prepared, ResourceManager manager, ProfilerFiller profiler) {

		var preparedIterator = prepared.entrySet().iterator();
		while (preparedIterator.hasNext()) {

			var preparedData = preparedIterator.next();
			Set<MultiJsonDataContainer.Entry> resourceEntries = preparedData.getValue();

			if (resourceEntries.isEmpty()) {
				preparedIterator.remove();
			}

		}

	}

	@Override
	public void onError(String packName, @NotNull ResourceLocation resourceId, String fileExtension, Exception exception) {
		String filePath = packName + "/" + (resourceType != null ? resourceType.getDirectory() : "...") + "/" + resourceId.getNamespace() + "/" + directoryName + "/" + resourceId.getPath() + fileExtension;
		LOGGER.error("Couldn't parse data file \"{}\" from \"{}\"", resourceId, filePath, exception);
	}

}

