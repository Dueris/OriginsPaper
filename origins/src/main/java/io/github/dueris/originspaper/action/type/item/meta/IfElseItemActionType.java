package io.github.dueris.originspaper.action.type.item.meta;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.ItemAction;
import io.github.dueris.originspaper.action.context.ItemActionContext;
import io.github.dueris.originspaper.action.type.ItemActionType;
import io.github.dueris.originspaper.action.type.ItemActionTypes;
import io.github.dueris.originspaper.action.type.meta.IfElseMetaActionType;
import io.github.dueris.originspaper.condition.ItemCondition;
import io.github.dueris.originspaper.condition.context.ItemConditionContext;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class IfElseItemActionType extends ItemActionType implements IfElseMetaActionType<ItemActionContext, ItemConditionContext, ItemAction, ItemCondition> {

	private final ItemCondition condition;

	private final ItemAction ifAction;
	private final Optional<ItemAction> elseAction;

	public IfElseItemActionType(ItemCondition condition, ItemAction ifAction, Optional<ItemAction> elseAction) {
		this.condition = condition;
		this.ifAction = ifAction;
		this.elseAction = elseAction;
	}

	@Override
	protected void execute(Level world, SlotAccess stackReference) {
		executeAction(new ItemActionContext(world, stackReference));
	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return ItemActionTypes.IF_ELSE;
	}

	@Override
	public ItemCondition condition() {
		return condition;
	}

	@Override
	public ItemAction ifAction() {
		return ifAction;
	}

	@Override
	public Optional<ItemAction> elseAction() {
		return elseAction;
	}

}
