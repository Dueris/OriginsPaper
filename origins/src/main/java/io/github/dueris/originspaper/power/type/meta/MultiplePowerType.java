package io.github.dueris.originspaper.power.type.meta;

import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.power.type.PowerType;
import io.github.dueris.originspaper.power.type.PowerTypes;
import org.jetbrains.annotations.NotNull;

public final class MultiplePowerType extends PowerType {

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.MULTIPLE;
	}

}
