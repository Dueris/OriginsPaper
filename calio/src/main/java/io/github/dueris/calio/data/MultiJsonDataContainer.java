package io.github.dueris.calio.data;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Set;

public class MultiJsonDataContainer extends LinkedHashMap<ResourceLocation, Set<MultiJsonDataContainer.Entry>> {

	public static MultiJsonDataContainer.@NotNull Entry entry(String source, JsonElement jsonData) {
		return new MultiJsonDataContainer.Entry(source, jsonData);
	}

	public void forEach(Processor processor) {
		this.forEach((id, entries) -> entries.forEach(entry -> processor.process(entry.source(), id, entry.jsonData())));
	}

	@FunctionalInterface
	public interface Processor {
		void process(String packName, ResourceLocation id, JsonElement jsonElement);
	}

	public record Entry(String source, JsonElement jsonData) {

	}

}

