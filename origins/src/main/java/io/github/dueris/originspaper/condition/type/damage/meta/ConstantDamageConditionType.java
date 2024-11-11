package io.github.dueris.originspaper.condition.type.damage.meta;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.DamageConditionType;
import io.github.dueris.originspaper.condition.type.DamageConditionTypes;
import io.github.dueris.originspaper.condition.type.meta.ConstantMetaConditionType;
import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.NotNull;

public class ConstantDamageConditionType extends DamageConditionType implements ConstantMetaConditionType {

	private final boolean value;

	public ConstantDamageConditionType(boolean value) {
		this.value = value;
	}

	@Override
	public boolean test(DamageSource source, float amount) {
		return value();
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return DamageConditionTypes.CONSTANT;
	}

	@Override
	public boolean value() {
		return value;
	}

}
