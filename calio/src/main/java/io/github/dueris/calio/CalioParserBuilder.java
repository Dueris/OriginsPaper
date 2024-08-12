package io.github.dueris.calio;

import io.github.dueris.calio.data.AccessorKey;
import io.github.dueris.calio.parser.ParsingStrategy;
import io.github.dueris.calio.registry.RegistryKey;
import io.github.dueris.calio.test.ModMeta;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class CalioParserBuilder {
	protected static final AbstractObjectSet<AccessorKey<?>> accessorKeys = new ObjectOpenHashSet<>();
	private final CraftCalio calio;

	public CalioParserBuilder(CraftCalio calio) {
		this.calio = calio;
		accessorKeys.add(
			new AccessorKey<>(List.of("calio"), "test", ModMeta.class, 0, ParsingStrategy.DEFAULT,
				new RegistryKey<>(ModMeta.class, ResourceLocation.fromNamespaceAndPath("calio", "test")))
		);
	}

	public CalioParserBuilder withAccessor(AccessorKey<?> key) {
		accessorKeys.add(key);
		return this;
	}

	public CraftCalio build() {
		return calio;
	}
}
