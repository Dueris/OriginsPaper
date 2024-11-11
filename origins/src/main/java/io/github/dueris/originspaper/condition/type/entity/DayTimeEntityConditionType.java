package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import io.github.dueris.originspaper.util.Comparison;
import org.jetbrains.annotations.NotNull;

public class DayTimeEntityConditionType extends TimeOfDayEntityConditionType {

	public DayTimeEntityConditionType() {
		super(Comparison.LESS_THAN, 13000);
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return EntityConditionTypes.DAY_TIME;
	}

}
