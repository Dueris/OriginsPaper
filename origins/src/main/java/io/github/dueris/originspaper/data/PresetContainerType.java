package io.github.dueris.originspaper.data;

import io.github.dueris.originspaper.util.TextAlignment;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.MenuConstructor;
import org.jetbrains.annotations.NotNull;

public record PresetContainerType(int columns, int rows, @NotNull Factory factory) implements ContainerType {

	@Override
	public TextAlignment titleAlignment() {
		return TextAlignment.NONE;
	}

	@Override
	public MenuConstructor create(Container inventory) {
		return factory().create(inventory, columns(), rows());
	}

	@FunctionalInterface
	public interface Factory {
		MenuConstructor create(Container inventory, int columns, int rows);
	}

}
