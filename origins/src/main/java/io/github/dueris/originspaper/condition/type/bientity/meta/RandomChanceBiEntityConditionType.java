package io.github.dueris.originspaper.condition.type.bientity.meta;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.BiEntityConditionType;
import io.github.dueris.originspaper.condition.type.BiEntityConditionTypes;
import io.github.dueris.originspaper.condition.type.meta.RandomChanceMetaConditionType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class RandomChanceBiEntityConditionType extends BiEntityConditionType implements RandomChanceMetaConditionType {

	private final float chance;

	public RandomChanceBiEntityConditionType(float chance) {
		this.chance = chance;
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return BiEntityConditionTypes.RANDOM_CHANCE;
	}

	@Override
	public boolean test(Entity actor, Entity target) {
		return testCondition();
	}

	@Override
	public float chance() {
		return chance;
	}

}
