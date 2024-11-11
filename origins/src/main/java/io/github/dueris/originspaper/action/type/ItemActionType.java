package io.github.dueris.originspaper.action.type;

import io.github.dueris.originspaper.action.ItemAction;
import io.github.dueris.originspaper.action.context.ItemActionContext;
import io.github.dueris.originspaper.power.type.ModifyEnchantmentLevelPowerType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class ItemActionType extends AbstractActionType<ItemActionContext, ItemAction> {

	@Override
	public void accept(ItemActionContext context) {

		SlotAccess stackReference = context.stackReference();
		if (stackReference == SlotAccess.NULL) {
			//	Skip empty stack references since they're immutable and don't have anything useful
			return;
		}

		//	Replace the stack of the stack reference with a "workable" empty stack if the said stack is
		//	an instance of ItemStack#EMPTY
		if (stackReference.get() == ItemStack.EMPTY) {
			stackReference.set(new ItemStack((Void) null));
		}

		//	Execute the action of this type
		execute(context.world(), context.stackReference());

		//	Restore the ItemStack#EMPTY stack of the stack reference afterward
		if (!ModifyEnchantmentLevelPowerType.isWorkableEmptyStack(stackReference) && stackReference.get().isEmpty()) {
			stackReference.set(ItemStack.EMPTY);
		}

	}

	@Override
	public ItemAction createAction() {
		return new ItemAction(this);
	}

	protected abstract void execute(Level world, SlotAccess stackReference);

}
