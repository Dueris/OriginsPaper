package io.github.dueris.originspaper.action.type.item.meta;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.ItemAction;
import io.github.dueris.originspaper.action.context.ItemActionContext;
import io.github.dueris.originspaper.action.type.ItemActionType;
import io.github.dueris.originspaper.action.type.ItemActionTypes;
import io.github.dueris.originspaper.action.type.meta.ChanceMetaActionType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ChanceItemActionType extends ItemActionType implements ChanceMetaActionType<ItemActionContext, ItemAction> {

	private final ItemAction successAction;
	private final Optional<ItemAction> failAction;

	private final float chance;

	public ChanceItemActionType(ItemAction successAction, Optional<ItemAction> failAction, float chance) {
		this.successAction = successAction;
		this.failAction = failAction;
		this.chance = chance;
	}

	@Override
	protected void execute(Level world, SlotAccess stackReference) {
		executeAction(new ItemActionContext(world, stackReference));
	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return ItemActionTypes.CHANCE;
	}

	@Override
	public ItemAction successAction() {
		return successAction;
	}

	@Override
	public Optional<ItemAction> failAction() {
		return failAction;
	}

	@Override
	public float chance() {
		return chance;
	}

}
