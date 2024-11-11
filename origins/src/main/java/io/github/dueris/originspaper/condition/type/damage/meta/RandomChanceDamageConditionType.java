package io.github.dueris.originspaper.condition.type.damage.meta;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.DamageConditionType;
import io.github.dueris.originspaper.condition.type.DamageConditionTypes;
import io.github.dueris.originspaper.condition.type.meta.RandomChanceMetaConditionType;
import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.NotNull;

public class RandomChanceDamageConditionType extends DamageConditionType implements RandomChanceMetaConditionType {

	private final float chance;

	public RandomChanceDamageConditionType(float chance) {
		this.chance = chance;
	}

	@Override
	public boolean test(DamageSource source, float amount) {
		return testCondition();
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return DamageConditionTypes.RANDOM_CHANCE;
	}

	@Override
	public float chance() {
		return chance;
	}

}
