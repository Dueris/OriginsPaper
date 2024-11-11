package io.github.dueris.originspaper.power.type.meta;

import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.power.type.PowerType;
import io.github.dueris.originspaper.power.type.PowerTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class DummyPowerType extends PowerType {

	public DummyPowerType(Optional<EntityCondition> condition) {
		super(condition);
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.DUMMY;
	}

}
