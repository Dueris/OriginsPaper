package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import io.github.dueris.originspaper.util.InventoryUtil;
import io.github.dueris.originspaper.util.PriorityPhase;
import io.github.dueris.originspaper.util.StackClickPhase;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.EnumSet;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ItemOnItemPowerType extends PowerType implements Prioritized<ItemOnItemPowerType> {

	private final Predicate<Tuple<Level, ItemStack>> usingItemCondition;
	private final Predicate<Tuple<Level, ItemStack>> onItemCondition;

	private final Consumer<Tuple<Level, SlotAccess>> usingItemAction;
	private final Consumer<Tuple<Level, SlotAccess>> onItemAction;
	private final Consumer<Tuple<Level, SlotAccess>> resultItemAction;
	private final Consumer<Entity> entityAction;

	private final EnumSet<ClickAction> clickTypes;
	private final EnumSet<StackClickPhase> clickPhases;

	private final ItemStack resultStack;

	private final int resultFromOnStack;
	private final int priority;

	public ItemOnItemPowerType(Power power, LivingEntity entity, EnumSet<ClickAction> clickTypes, EnumSet<StackClickPhase> clickPhases, Consumer<Entity> entityAction, Consumer<Tuple<Level, SlotAccess>> usingItemAction, Consumer<Tuple<Level, SlotAccess>> onItemAction, Consumer<Tuple<Level, SlotAccess>> resultItemAction, Predicate<Tuple<Level, ItemStack>> usingItemCondition, Predicate<Tuple<Level, ItemStack>> onItemCondition, ItemStack resultStack, int resultFromOnStack, int priority) {
		super(power, entity);
		this.usingItemCondition = usingItemCondition;
		this.onItemCondition = onItemCondition;
		this.resultStack = resultStack;
		this.usingItemAction = usingItemAction;
		this.onItemAction = onItemAction;
		this.resultItemAction = resultItemAction;
		this.entityAction = entityAction;
		this.resultFromOnStack = resultFromOnStack;
		this.clickTypes = clickTypes;
		this.clickPhases = clickPhases;
		this.priority = priority;
	}

	public static boolean executeActions(Player user, PriorityPhase priorityPhase, StackClickPhase clickPhase, ClickAction clickType, Slot slot, SlotAccess slotStackReference, SlotAccess cursorStackReference) {

		CallInstance<ItemOnItemPowerType> ioipci = new CallInstance<>();
		ioipci.add(user, ItemOnItemPowerType.class, p -> p.doesApply(cursorStackReference.get(), slotStackReference.get(), clickType, clickPhase, priorityPhase));

		for (int i = ioipci.getMaxPriority(); i >= ioipci.getMinPriority(); i--) {
			ioipci.forEach(i, p -> p.execute(cursorStackReference, slotStackReference, slot));
		}

		return !ioipci.isEmpty();

	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("item_on_item"),
			new SerializableData()
				.add("click_type", ApoliDataTypes.CLICK_TYPE, ClickAction.SECONDARY)
				.addFunctionedDefault("click_types", ApoliDataTypes.CLICK_TYPE_SET, data -> EnumSet.of(data.get("click_type")))
				.add("click_phases", ApoliDataTypes.STACK_CLICK_PHASE_SET, EnumSet.allOf(StackClickPhase.class))
				.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
				.add("using_item_action", ApoliDataTypes.ITEM_ACTION, null)
				.add("on_item_action", ApoliDataTypes.ITEM_ACTION, null)
				.add("result_item_action", ApoliDataTypes.ITEM_ACTION, null)
				.add("using_item_condition", ApoliDataTypes.ITEM_CONDITION, null)
				.add("on_item_condition", ApoliDataTypes.ITEM_CONDITION, null)
				.add("result", SerializableDataTypes.ITEM_STACK, null)
				.addFunctionedDefault("result_stack", SerializableDataTypes.ITEM_STACK, data -> data.get("result"))
				.add("result_from_on_stack", SerializableDataTypes.INT, 0)
				.add("priority", SerializableDataTypes.INT, 0),
			data -> (power, entity) -> new ItemOnItemPowerType(power, entity,
				data.get("click_types"),
				data.get("click_phases"),
				data.get("entity_action"),
				data.get("using_item_action"),
				data.get("on_item_action"),
				data.get("result_item_action"),
				data.get("using_item_condition"),
				data.get("on_item_condition"),
				data.get("result_stack"),
				data.get("result_from_on_stack"),
				data.get("priority")
			)
		).allowCondition();
	}

	@Override
	public int getPriority() {
		return priority;
	}

	public boolean doesApply(ItemStack usingStack, ItemStack onStack, ClickAction clickType, StackClickPhase clickPhase, PriorityPhase priorityPhase) {
		return clickTypes.contains(clickType)
			&& clickPhases.contains(clickPhase)
			&& priorityPhase.test(this.getPriority())
			&& (onItemCondition == null || onItemCondition.test(new Tuple<>(entity.level(), onStack)))
			&& (usingItemCondition == null || usingItemCondition.test(new Tuple<>(entity.level(), usingStack)));
	}

	public void execute(SlotAccess usingStackRef, SlotAccess onStackRef, Slot slot) {

		SlotAccess resultStackRef = InventoryUtil.createStackReference(resultStack != null
			? resultStack.copy()
			: resultFromOnStack > 0
			? onStackRef.get().split(resultFromOnStack)
			: onStackRef.get());

		if (resultItemAction != null) {
			resultItemAction.accept(new Tuple<>(entity.level(), resultStackRef));
		}

		if (usingItemAction != null) {
			usingItemAction.accept(new Tuple<>(entity.level(), usingStackRef));
		}

		if (onItemAction != null) {
			onItemAction.accept(new Tuple<>(entity.level(), onStackRef));
		}

		if (entity instanceof Player player && (resultStack != null || resultItemAction != null)) {

			if (slot.hasItem()) {
				player.getInventory().placeItemBackInInventory(resultStackRef.get());
			} else {
				slot.set(resultStackRef.get());
			}

		}

		if (entityAction != null) {
			entityAction.accept(entity);
		}

	}
}
