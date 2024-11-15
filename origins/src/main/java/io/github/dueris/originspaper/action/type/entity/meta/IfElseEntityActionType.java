package io.github.dueris.originspaper.action.type.entity.meta;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.EntityAction;
import io.github.dueris.originspaper.action.context.EntityActionContext;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.action.type.meta.IfElseMetaActionType;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.condition.context.EntityConditionContext;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class IfElseEntityActionType extends EntityActionType implements IfElseMetaActionType<EntityActionContext, EntityConditionContext, EntityAction, EntityCondition> {

	private final EntityCondition condition;

	private final EntityAction ifAction;
	private final Optional<EntityAction> elseAction;

	public IfElseEntityActionType(EntityCondition condition, EntityAction ifAction, Optional<EntityAction> elseAction) {
		this.condition = condition;
		this.ifAction = ifAction;
		this.elseAction = elseAction;
	}

	@Override
	protected void execute(Entity entity) {
		executeAction(new EntityActionContext(entity));
	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return EntityActionTypes.IF_ELSE;
	}

	@Override
	public EntityCondition condition() {
		return condition;
	}

	@Override
	public EntityAction ifAction() {
		return ifAction;
	}

	@Override
	public Optional<EntityAction> elseAction() {
		return elseAction;
	}

}
