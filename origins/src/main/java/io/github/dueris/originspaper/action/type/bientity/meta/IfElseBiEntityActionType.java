package io.github.dueris.originspaper.action.type.bientity.meta;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.BiEntityAction;
import io.github.dueris.originspaper.action.context.BiEntityActionContext;
import io.github.dueris.originspaper.action.type.BiEntityActionType;
import io.github.dueris.originspaper.action.type.BiEntityActionTypes;
import io.github.dueris.originspaper.action.type.meta.IfElseMetaActionType;
import io.github.dueris.originspaper.condition.BiEntityCondition;
import io.github.dueris.originspaper.condition.context.BiEntityConditionContext;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class IfElseBiEntityActionType extends BiEntityActionType implements IfElseMetaActionType<BiEntityActionContext, BiEntityConditionContext, BiEntityAction, BiEntityCondition> {

	private final BiEntityCondition condition;

	private final BiEntityAction ifAction;
	private final Optional<BiEntityAction> elseAction;

	public IfElseBiEntityActionType(BiEntityCondition condition, BiEntityAction ifAction, Optional<BiEntityAction> elseAction) {
		this.condition = condition;
		this.ifAction = ifAction;
		this.elseAction = elseAction;
	}

	@Override
	protected void execute(Entity actor, Entity target) {
		executeAction(new BiEntityActionContext(actor, target));
	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return BiEntityActionTypes.IF_ELSE;
	}

	@Override
	public BiEntityCondition condition() {
		return condition;
	}

	@Override
	public BiEntityAction ifAction() {
		return ifAction;
	}

	@Override
	public Optional<BiEntityAction> elseAction() {
		return elseAction;
	}

}
