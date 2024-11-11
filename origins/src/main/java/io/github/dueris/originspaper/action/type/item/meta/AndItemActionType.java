package io.github.dueris.originspaper.action.type.item.meta;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.ItemAction;
import io.github.dueris.originspaper.action.context.ItemActionContext;
import io.github.dueris.originspaper.action.type.ItemActionType;
import io.github.dueris.originspaper.action.type.ItemActionTypes;
import io.github.dueris.originspaper.action.type.meta.AndMetaActionType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AndItemActionType extends ItemActionType implements AndMetaActionType<ItemActionContext, ItemAction> {

	private final List<ItemAction> actions;

	public AndItemActionType(List<ItemAction> actions) {
		this.actions = actions;
	}

	@Override
	protected void execute(Level world, SlotAccess stackReference) {
		executeActions(new ItemActionContext(world, stackReference));
	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return ItemActionTypes.AND;
	}

	@Override
	public List<ItemAction> actions() {
		return actions;
	}

}
