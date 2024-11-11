package io.github.dueris.originspaper.condition.context;

import io.github.dueris.originspaper.util.context.TypeConditionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public record EntityConditionContext(Entity entity) implements TypeConditionContext {

	public Level world() {
		return entity().level();
	}

	public BlockPos blockPos() {
		return entity().blockPosition();
	}

}
