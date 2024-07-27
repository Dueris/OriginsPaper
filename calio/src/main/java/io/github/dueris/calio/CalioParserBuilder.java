package io.github.dueris.calio;

import io.github.dueris.calio.data.AccessorKey;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public class CalioParserBuilder {
	protected static final AbstractObjectSet<AccessorKey<?>> accessorKeys = new ObjectOpenHashSet<>();
	private final CraftCalio calio;

	public CalioParserBuilder(CraftCalio calio) {
		this.calio = calio;
	}

	public CalioParserBuilder withAccessor(AccessorKey<?> key) {
		accessorKeys.add(key);
		return this;
	}

	public CraftCalio build() {
		return calio;
	}
}
