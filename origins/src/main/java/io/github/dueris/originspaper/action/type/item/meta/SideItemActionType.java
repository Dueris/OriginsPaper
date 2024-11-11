package io.github.dueris.originspaper.action.type.item.meta;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.ItemAction;
import io.github.dueris.originspaper.action.context.ItemActionContext;
import io.github.dueris.originspaper.action.type.ItemActionType;
import io.github.dueris.originspaper.action.type.ItemActionTypes;
import io.github.dueris.originspaper.action.type.meta.SideMetaActionType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class SideItemActionType extends ItemActionType implements SideMetaActionType<ItemActionContext, ItemAction> {

	private final ItemAction action;
	private final Side side;

	public SideItemActionType(ItemAction action, Side side) {
		this.action = action;
		this.side = side;
	}

	@Override
	protected void execute(Level world, SlotAccess stackReference) {
		executeAction(new ItemActionContext(world, stackReference));
	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return ItemActionTypes.SIDE;
	}

	@Override
	public ItemAction action() {
		return action;
	}

	@Override
	public Side side() {
		return side;
	}

}
