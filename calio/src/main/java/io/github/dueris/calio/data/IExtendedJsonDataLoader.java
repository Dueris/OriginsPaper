package io.github.dueris.calio.data;

import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.io.FilenameUtils;
import org.quiltmc.parsers.json.JsonFormat;

import java.util.HashMap;
import java.util.Map;

public interface IExtendedJsonDataLoader {

	Map<String, JsonFormat> DEFAULT_VALID_FORMATS = Util.make(new HashMap<>(), map -> {
		map.put(".json", JsonFormat.JSON);
		map.put(".json5", JsonFormat.JSON5);
		map.put(".jsonc", JsonFormat.JSONC);
	});

	/**
	 * <p>Called when a JSON data currently being prepared has been rejected due to its resource conditions not being fulfilled.</p>
	 *
	 * @param packName   the name of the data/resource pack the JSON is from
	 * @param resourceId the ID (without the file extension suffix and directory) of the JSON data
	 */
	default void onReject(String packName, ResourceLocation resourceId) {

	}

	/**
	 * <p>Called when an error occurs upon preparing a JSON data.</p>
	 *
	 * @param packName      the name of the data/resource pack the JSON is from
	 * @param resourceId    the ID (<b>without</b> the file extension suffix and directory) of the JSON data
	 * @param fileExtension the file extension of the JSON
	 * @param exception     the {@link Exception} thrown when preparing the JSON data
	 */
	default void onError(String packName, ResourceLocation resourceId, String fileExtension, Exception exception) {

	}

	/**
	 * @return a {@link Map} of supported file extensions ({@link String}) and its associated JSON formats ({@link JsonFormat})
	 */
	default Map<String, JsonFormat> getValidFormats() {
		return DEFAULT_VALID_FORMATS;
	}

	default ResourceLocation trim(ResourceLocation fileId, String directoryName) {
		String path = FilenameUtils.removeExtension(fileId.getPath()).substring(directoryName.length() + 1);
		return ResourceLocation.fromNamespaceAndPath(fileId.getNamespace(), path);
	}

	default boolean hasValidFormat(ResourceLocation fileId) {
		return this.getValidFormats().keySet()
			.stream()
			.anyMatch(suffix -> fileId.getPath().endsWith(suffix));
	}

}

