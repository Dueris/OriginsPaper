package io.github.dueris.originspaper.condition.type.item.meta;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.ItemConditionType;
import io.github.dueris.originspaper.condition.type.ItemConditionTypes;
import io.github.dueris.originspaper.condition.type.meta.RandomChanceMetaConditionType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class RandomChanceItemConditionType extends ItemConditionType implements RandomChanceMetaConditionType {

	private final float chance;

	public RandomChanceItemConditionType(float chance) {
		this.chance = chance;
	}

	@Override
	public boolean test(Level world, ItemStack stack) {
		return testCondition();
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return ItemConditionTypes.RANDOM_CHANCE;
	}

	@Override
	public float chance() {
		return chance;
	}

}
