package io.github.dueris.originspaper.condition.context;

import io.github.dueris.originspaper.util.context.TypeConditionContext;
import net.minecraft.world.damagesource.DamageSource;

public record DamageConditionContext(DamageSource source, float amount) implements TypeConditionContext {

}
