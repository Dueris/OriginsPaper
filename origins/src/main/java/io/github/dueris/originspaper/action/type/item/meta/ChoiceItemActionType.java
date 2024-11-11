package io.github.dueris.originspaper.action.type.item.meta;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.ItemAction;
import io.github.dueris.originspaper.action.context.ItemActionContext;
import io.github.dueris.originspaper.action.type.ItemActionType;
import io.github.dueris.originspaper.action.type.ItemActionTypes;
import io.github.dueris.originspaper.action.type.meta.ChoiceMetaActionType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.behavior.ShufflingList;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ChoiceItemActionType extends ItemActionType implements ChoiceMetaActionType<ItemActionContext, ItemAction> {

	private final ShufflingList<ItemAction> actions;

	public ChoiceItemActionType(ShufflingList<ItemAction> actions) {
		this.actions = actions;
	}

	@Override
	protected void execute(Level world, SlotAccess stackReference) {
		executeActions(new ItemActionContext(world, stackReference));
	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return ItemActionTypes.CHOICE;
	}

	@Override
	public ShufflingList<ItemAction> actions() {
		return actions;
	}

}
