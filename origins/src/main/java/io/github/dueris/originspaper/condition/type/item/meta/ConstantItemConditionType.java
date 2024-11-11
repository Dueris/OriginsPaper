package io.github.dueris.originspaper.condition.type.item.meta;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.ItemConditionType;
import io.github.dueris.originspaper.condition.type.ItemConditionTypes;
import io.github.dueris.originspaper.condition.type.meta.ConstantMetaConditionType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ConstantItemConditionType extends ItemConditionType implements ConstantMetaConditionType {

	private final boolean value;

	public ConstantItemConditionType(boolean value) {
		this.value = value;
	}

	@Override
	public boolean test(Level world, ItemStack stack) {
		return value();
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return ItemConditionTypes.CONSTANT;
	}

	@Override
	public boolean value() {
		return value;
	}

}
