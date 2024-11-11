package io.github.dueris.originspaper.condition.context;

import io.github.dueris.originspaper.util.context.TypeConditionContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record ItemConditionContext(Level world, ItemStack stack) implements TypeConditionContext {

}
