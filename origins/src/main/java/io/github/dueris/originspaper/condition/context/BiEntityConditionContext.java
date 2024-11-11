package io.github.dueris.originspaper.condition.context;

import io.github.dueris.originspaper.util.context.TypeConditionContext;
import net.minecraft.world.entity.Entity;

public record BiEntityConditionContext(Entity actor, Entity target) implements TypeConditionContext {

}
