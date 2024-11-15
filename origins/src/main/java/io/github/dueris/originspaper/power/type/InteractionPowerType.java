package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.action.ItemAction;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.condition.ItemCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.util.InventoryUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;
import java.util.Optional;
import java.util.function.BiFunction;

public abstract class InteractionPowerType extends PowerType {

	protected final Optional<ItemAction> heldItemAction;
	protected final Optional<ItemCondition> heldItemCondition;

	protected final Optional<ItemAction> resultItemAction;
	protected final Optional<ItemStack> resultStack;

	protected final EnumSet<InteractionHand> hands;
	protected final InteractionResult actionResult;

	public InteractionPowerType(Optional<ItemAction> heldItemAction, Optional<ItemCondition> heldItemCondition, Optional<ItemAction> resultItemAction, Optional<ItemStack> resultStack, EnumSet<InteractionHand> hands, InteractionResult actionResult, Optional<EntityCondition> condition) {
		super(condition);
		this.hands = hands;
		this.actionResult = actionResult;
		this.heldItemCondition = heldItemCondition;
		this.heldItemAction = heldItemAction;
		this.resultStack = resultStack;
		this.resultItemAction = resultItemAction;
	}

	public InteractionPowerType(Optional<ItemAction> heldItemAction, Optional<ItemCondition> heldItemCondition, Optional<ItemStack> resultStack, Optional<ItemAction> resultItemAction, EnumSet<InteractionHand> hands, InteractionResult actionResult) {
		this(heldItemAction, heldItemCondition, resultItemAction, resultStack, hands, actionResult, Optional.empty());
	}

	protected static SlotAccess getHeldStackReference(Player player, InteractionHand hand) {

		Inventory playerInventory = player.getInventory();
		int selectedSlot = playerInventory.selected;

		if (hand == InteractionHand.MAIN_HAND && Inventory.isHotbarSlot(selectedSlot)) {
			return SlotAccess.forContainer(playerInventory, selectedSlot);
		} else if (hand == InteractionHand.OFF_HAND) {
			return SlotAccess.of(playerInventory.offhand::getFirst, stack -> playerInventory.offhand.set(0, stack));
		} else {
			return SlotAccess.NULL;
		}

	}

	public static <T extends InteractionPowerType> TypedDataObjectFactory<T> createConditionedDataFactory(SerializableData serializableData, FromData<T> fromData, BiFunction<T, SerializableData, SerializableData.Instance> toData) {
		return PowerType.createConditionedDataFactory(
			serializableData
				.add("held_item_action", ItemAction.DATA_TYPE.optional(), Optional.empty())
				.add("item_condition", ItemCondition.DATA_TYPE.optional(), Optional.empty())
				.addFunctionedDefault("held_item_condition", ItemCondition.DATA_TYPE.optional(), data -> data.get("item_condition"))
				.add("result_item_action", ItemAction.DATA_TYPE.optional(), Optional.empty())
				.add("result_stack", SerializableDataTypes.ITEM_STACK.optional(), Optional.empty())
				.add("hands", SerializableDataTypes.HAND_SET, EnumSet.allOf(InteractionHand.class))
				.add("action_result", SerializableDataTypes.ACTION_RESULT, InteractionResult.SUCCESS),
			(data, condition) -> fromData.apply(
				data,
				data.get("held_item_action"),
				data.get("held_item_condition"),
				data.get("result_item_action"),
				data.get("result_stack"),
				data.get("hands"),
				data.get("action_result"),
				condition
			),
			(t, _serializableData) -> toData.apply(t, _serializableData)
				.set("held_item_action", t.heldItemAction)
				.set("held_item_condition", t.heldItemCondition)
				.set("result_item_action", t.resultItemAction)
				.set("result_stack", t.resultStack)
				.set("hands", t.hands)
				.set("action_result", t.actionResult)
		);
	}

	public boolean shouldExecute(InteractionHand hand, ItemStack heldStack) {
		return doesApplyToHand(hand)
			&& doesApplyToItem(heldStack);
	}

	public boolean doesApplyToHand(InteractionHand hand) {
		return hands.contains(hand);
	}

	public boolean doesApplyToItem(ItemStack heldStack) {
		return heldItemCondition
			.map(condition -> condition.test(getHolder().level(), heldStack))
			.orElse(true);
	}

	public InteractionResult getActionResult() {
		return actionResult;
	}

	protected void performActorItemStuff(Player actor, InteractionHand hand) {

		SlotAccess heldStackReference = getHeldStackReference(actor, hand);
		heldItemAction.ifPresent(action -> action.execute(actor.level(), heldStackReference));

		ItemStack resultStack = this.resultStack.isPresent()
			? this.resultStack.get().copy()
			: heldStackReference.get().copy();

		SlotAccess resultStackReference = InventoryUtil.createStackReference(resultStack);
		boolean modified = this.resultStack.isPresent() || resultItemAction.isPresent();

		resultItemAction.ifPresent(action -> action.execute(actor.level(), resultStackReference));

		if (modified) {

			if (heldStackReference.get().isEmpty()) {
				actor.setItemInHand(hand, resultStackReference.get());
			} else {
				actor.getInventory().placeItemBackInInventory(resultStackReference.get());
			}

		}

	}

	@FunctionalInterface
	public interface FromData<T extends InteractionPowerType> {
		T apply(SerializableData.Instance data, Optional<ItemAction> heldItemAction, Optional<ItemCondition> heldItemCondition, Optional<ItemAction> resultItemAction, Optional<ItemStack> resultStack, EnumSet<InteractionHand> hands, InteractionResult actionResult, Optional<EntityCondition> condition);
	}

}
