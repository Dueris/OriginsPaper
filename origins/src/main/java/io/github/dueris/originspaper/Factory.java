package io.github.dueris.originspaper;

import io.github.dueris.calio.data.SerializableData;
import net.minecraft.resources.ResourceLocation;

public interface Factory {

	ResourceLocation getSerializerId();

	SerializableData getSerializableData();
}
