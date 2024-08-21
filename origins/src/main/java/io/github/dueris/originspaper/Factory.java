package io.github.dueris.originspaper;

import io.github.dueris.calio.parser.SerializableData;
import net.minecraft.resources.ResourceLocation;

public interface Factory {

	ResourceLocation getSerializerId();

	SerializableData getSerializableData();
}
