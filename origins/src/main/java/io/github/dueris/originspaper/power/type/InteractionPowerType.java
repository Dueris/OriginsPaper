package io.github.dueris.originspaper.power.type;

import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.util.InventoryUtil;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.SlotRanges;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.EnumSet;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class InteractionPowerType extends PowerType {

	protected final Consumer<Tuple<Level, SlotAccess>> heldItemAction;
	protected final ItemStack itemResult;
	protected final Consumer<Tuple<Level, SlotAccess>> resultItemAction;
	private final EnumSet<InteractionHand> hands;
	private final InteractionResult actionResult;
	private final Predicate<Tuple<Level, ItemStack>> itemCondition;

	public InteractionPowerType(Power power, LivingEntity entity, EnumSet<InteractionHand> hands, InteractionResult actionResult, Predicate<Tuple<Level, ItemStack>> itemCondition, Consumer<Tuple<Level, SlotAccess>> heldItemAction, ItemStack itemResult, Consumer<Tuple<Level, SlotAccess>> resultItemAction) {
		super(power, entity);
		this.hands = hands;
		this.actionResult = actionResult;
		this.itemCondition = itemCondition;
		this.heldItemAction = heldItemAction;
		this.itemResult = itemResult;
		this.resultItemAction = resultItemAction;
	}

	public boolean shouldExecute(InteractionHand hand, ItemStack heldStack) {
		if (!doesApplyToHand(hand)) {
			return false;
		}
		return doesApplyToItem(heldStack);
	}

	public boolean doesApplyToHand(InteractionHand hand) {
		return hands.contains(hand);
	}

	public boolean doesApplyToItem(ItemStack heldStack) {
		return itemCondition == null || itemCondition.test(new Tuple<>(entity.level(), heldStack));
	}

	public InteractionResult getActionResult() {
		return actionResult;
	}

	protected void performActorItemStuff(InteractionPowerType power, Player actor, InteractionHand hand) {

		SlotAccess heldStack = actor.getSlot(hand == InteractionHand.OFF_HAND ? SlotRanges.nameToIds("weapon.offhand").slots().getFirst() : SlotRanges.nameToIds("weapon.mainhand").slots().getFirst());

		if (power.heldItemAction != null) {
			heldItemAction.accept(new Tuple<>(actor.level(), heldStack));
		}

		if (power.itemResult != null) {
			heldStack.set(power.itemResult);
		}

		SlotAccess resultingStack = InventoryUtil.createStackReference(power.itemResult == null ? heldStack.get() : power.itemResult.copy());
		boolean modified = power.itemResult != null;

		if (power.resultItemAction != null) {
			resultItemAction.accept(new Tuple<>(actor.level(), heldStack));
			modified = true;
		}

		if (modified) {
			if (heldStack.get().isEmpty()) {
				actor.setItemInHand(hand, resultingStack.get());
			} else {
				actor.getInventory().placeItemBackInInventory(resultingStack.get());
			}
		}

	}

}

