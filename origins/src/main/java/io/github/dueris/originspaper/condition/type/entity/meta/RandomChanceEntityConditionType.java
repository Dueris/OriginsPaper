package io.github.dueris.originspaper.condition.type.entity.meta;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import io.github.dueris.originspaper.condition.type.meta.RandomChanceMetaConditionType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class RandomChanceEntityConditionType extends EntityConditionType implements RandomChanceMetaConditionType {

	private final float chance;

	public RandomChanceEntityConditionType(float chance) {
		this.chance = chance;
	}

	@Override
	public boolean test(Entity entity) {
		return testCondition();
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return EntityConditionTypes.RANDOM_CHANCE;
	}

	@Override
	public float chance() {
		return chance;
	}

}
