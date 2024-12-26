package io.github.dueris.originspaper.data;

import io.github.dueris.originspaper.util.TextAlignment;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.MenuConstructor;
import org.jetbrains.annotations.Range;

public interface ContainerType {

	TextAlignment titleAlignment();

	MenuConstructor create(Container inventory);

	default int size() {
		return columns() * rows();
	}

	@Range(from = 1, to = Integer.MAX_VALUE)
	int columns();

	@Range(from = 1, to = Integer.MAX_VALUE)
	int rows();

}