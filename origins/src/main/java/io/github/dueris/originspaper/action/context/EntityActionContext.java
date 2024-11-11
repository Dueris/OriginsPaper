package io.github.dueris.originspaper.action.context;

import io.github.dueris.originspaper.condition.context.EntityConditionContext;
import io.github.dueris.originspaper.util.context.TypeActionContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public record EntityActionContext(Entity entity) implements TypeActionContext<EntityConditionContext> {

	@Override
	public EntityConditionContext forCondition() {
		return new EntityConditionContext(entity());
	}

	public Level world() {
		return entity().level();
	}

	public Vec3 pos() {
		return entity().position();
	}

}
