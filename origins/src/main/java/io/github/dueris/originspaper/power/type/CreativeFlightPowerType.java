package io.github.dueris.originspaper.power.type;

import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.power.PowerConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CreativeFlightPowerType extends PowerType {

	public CreativeFlightPowerType(Optional<EntityCondition> condition) {
		super(condition);
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.CREATIVE_FLIGHT;
	}
}
