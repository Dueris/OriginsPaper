package io.github.dueris.calio;

import io.github.dueris.calio.data.DataBuildDirective;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public class CalioParserBuilder {
	protected static final AbstractObjectSet<DataBuildDirective<?>> DATA_BUILD_DIRECTIVES = new ObjectOpenHashSet<>();
	private final CraftCalio calio;

	public CalioParserBuilder(CraftCalio calio) {
		this.calio = calio;
	}

	public CalioParserBuilder withAccessor(DataBuildDirective<?> key) {
		DATA_BUILD_DIRECTIVES.add(key);
		return this;
	}

	public CraftCalio build() {
		return calio;
	}
}
