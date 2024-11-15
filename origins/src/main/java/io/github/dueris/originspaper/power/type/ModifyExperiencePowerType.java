package io.github.dueris.originspaper.power.type;

import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.util.modifier.Modifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class ModifyExperiencePowerType extends ValueModifyingPowerType {

	public ModifyExperiencePowerType(List<Modifier> modifiers, Optional<EntityCondition> condition) {
		super(modifiers, condition);
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.MODIFY_EXPERIENCE;
	}

}
