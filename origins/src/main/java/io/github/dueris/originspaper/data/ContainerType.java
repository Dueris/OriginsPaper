package io.github.dueris.originspaper.data;

import com.google.common.base.Preconditions;
import io.github.dueris.originspaper.util.TextAlignment;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.MenuConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record ContainerType(TextAlignment titleAlignment, int columns, int rows, Optional<Factory> factory) {

	public ContainerType {
		Preconditions.checkArgument(columns > 0, "Container type must have at least 1 column!");
		Preconditions.checkArgument(rows > 0, "Container type must have at least 1 row!");
	}

	public MenuConstructor create(Container inventory) {
		return factory()
			.map(factory -> factory.create(inventory, columns(), rows()))
			.orElseThrow(() -> new IllegalStateException("Dynamic screen handler for dynamic container types aren't implemented yet!"));
	}

	public int size() {
		return columns() * rows();
	}

	public static @NotNull ContainerType preset(int columns, int rows, @NotNull Factory factory) {
		return new ContainerType(TextAlignment.NONE, columns, rows, Optional.of(factory));
	}

	@FunctionalInterface
	public interface Factory {
		MenuConstructor create(Container inventory, int columns, int rows);
	}

}
