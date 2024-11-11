package io.github.dueris.originspaper.condition.type.item;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.ItemConditionType;
import io.github.dueris.originspaper.condition.type.ItemConditionTypes;
import io.github.dueris.originspaper.power.type.EdibleItemPowerType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class FoodItemConditionType extends ItemConditionType {

    @Override
    public boolean test(Level world, ItemStack stack) {
        return EdibleItemPowerType.get(stack).isPresent()
            || stack.has(DataComponents.FOOD);
    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return ItemConditionTypes.FOOD;
    }

}
