package io.github.dueris.originspaper.action.type.item.meta;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.ItemAction;
import io.github.dueris.originspaper.action.context.ItemActionContext;
import io.github.dueris.originspaper.action.type.ItemActionType;
import io.github.dueris.originspaper.action.type.ItemActionTypes;
import io.github.dueris.originspaper.action.type.meta.DelayMetaActionType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class DelayItemActionType extends ItemActionType implements DelayMetaActionType<ItemActionContext, ItemAction> {

	private final ItemAction action;
	private final int ticks;

	public DelayItemActionType(ItemAction action, int ticks) {
		this.action = action;
		this.ticks = ticks;
	}

	@Override
	protected void execute(Level world, SlotAccess stackReference) {
		executeAction(new ItemActionContext(world, stackReference));
	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return ItemActionTypes.DELAY;
	}

	@Override
	public ItemAction action() {
		return action;
	}

	@Override
	public int ticks() {
		return ticks;
	}

}
