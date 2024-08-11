package me.dueris.originspaper;

import io.github.dueris.calio.parser.InstanceDefiner;
import net.minecraft.resources.ResourceLocation;

public interface Factory {

	ResourceLocation getSerializerId();

	InstanceDefiner getSerializableData();
}
