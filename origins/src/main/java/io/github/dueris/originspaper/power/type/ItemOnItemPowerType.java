package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.action.EntityAction;
import io.github.dueris.originspaper.action.ItemAction;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.condition.ItemCondition;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.util.InventoryUtil;
import io.github.dueris.originspaper.util.PriorityPhase;
import io.github.dueris.originspaper.util.StackClickPhase;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Optional;

public class ItemOnItemPowerType extends PowerType implements Prioritized<ItemOnItemPowerType> {

	public static final TypedDataObjectFactory<ItemOnItemPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("entity_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
			.add("using_item_action", ItemAction.DATA_TYPE.optional(), Optional.empty())
			.add("on_item_action", ItemAction.DATA_TYPE.optional(), Optional.empty())
			.add("using_item_condition", ItemCondition.DATA_TYPE.optional(), Optional.empty())
			.add("on_item_condition", ItemCondition.DATA_TYPE.optional(), Optional.empty())
			.add("click_types", ApoliDataTypes.CLICK_TYPE_SET, EnumSet.allOf(ClickAction.class))
			.add("click_phases", ApoliDataTypes.STACK_CLICK_PHASE_SET, EnumSet.allOf(StackClickPhase.class))
			.add("result_stack", SerializableDataTypes.ITEM_STACK.optional(), Optional.empty())
			.add("result_item_action", ItemAction.DATA_TYPE.optional(), Optional.empty())
			.add("result_from_on_stack", SerializableDataTypes.NON_NEGATIVE_INT, 0)
			.add("priority", SerializableDataTypes.INT, 0),
		(data, condition) -> new ItemOnItemPowerType(
			data.get("entity_action"),
			data.get("using_item_action"),
			data.get("on_item_action"),
			data.get("using_item_condition"),
			data.get("on_item_condition"),
			data.get("click_types"),
			data.get("click_phases"),
			data.get("result_stack"),
			data.get("result_item_action"),
			data.get("result_from_on_stack"),
			data.get("priority"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("entity_action", powerType.entityAction)
			.set("using_item_action", powerType.usingItemAction)
			.set("on_item_action", powerType.onItemAction)
			.set("using_item_condition", powerType.usingItemCondition)
			.set("on_item_condition", powerType.onItemCondition)
			.set("click_types", powerType.clickTypes)
			.set("click_phases", powerType.clickPhases)
			.set("result_stack", powerType.resultStack)
			.set("result_item_action", powerType.resultItemAction)
			.set("result_from_on_stack", powerType.resultFromOnStack)
			.set("priority", powerType.priority)
	);

	private final Optional<EntityAction> entityAction;
	private final Optional<ItemAction> usingItemAction;
	private final Optional<ItemAction> onItemAction;

	private final Optional<ItemCondition> usingItemCondition;
	private final Optional<ItemCondition> onItemCondition;

	private final EnumSet<ClickAction> clickTypes;
	private final EnumSet<StackClickPhase> clickPhases;

	private final Optional<ItemStack> resultStack;
	private final Optional<ItemAction> resultItemAction;

	private final int resultFromOnStack;
	private final int priority;

	public ItemOnItemPowerType(Optional<EntityAction> entityAction, Optional<ItemAction> usingItemAction, Optional<ItemAction> onItemAction, Optional<ItemCondition> usingItemCondition, Optional<ItemCondition> onItemCondition, EnumSet<ClickAction> clickTypes, EnumSet<StackClickPhase> clickPhases, Optional<ItemStack> resultStack, Optional<ItemAction> resultItemAction, int resultFromOnStack, int priority, Optional<EntityCondition> condition) {
		super(condition);
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

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.ITEM_ON_ITEM;
	}

	@Override
	public int getPriority() {
		return priority;
	}

	public boolean doesApply(ItemStack usingStack, ItemStack onStack, ClickAction clickType, StackClickPhase clickPhase, PriorityPhase priorityPhase) {
		return clickTypes.contains(clickType)
			&& clickPhases.contains(clickPhase)
			&& priorityPhase.test(this.getPriority())
			&& usingItemCondition.map(condition -> condition.test(getHolder().level(), usingStack)).orElse(true)
			&& onItemCondition.map(condition -> condition.test(getHolder().level(), onStack)).orElse(true);
	}

	public void execute(SlotAccess usingStackReference, SlotAccess onStackReference, Slot slot) {

		LivingEntity holder = getHolder();
		ItemStack resultStackCopy = resultStack.isPresent()
			? resultStack.get().copy()
			: resultFromOnStack > 0
			? onStackReference.get().split(resultFromOnStack)
			: onStackReference.get();

		SlotAccess resultStackReference = InventoryUtil.createStackReference(resultStackCopy);
		resultItemAction.ifPresent(action -> action.execute(holder.level(), resultStackReference));

		usingItemAction.ifPresent(action -> action.execute(holder.level(), usingStackReference));
		onItemAction.ifPresent(action -> action.execute(holder.level(), onStackReference));

		if (holder instanceof Player player && (resultStack.isPresent() || resultItemAction.isPresent())) {

			if (slot.hasItem()) {
				player.getInventory().placeItemBackInInventory(resultStackReference.get());
			} else {
				slot.set(resultStackReference.get());
			}

		}

		entityAction.ifPresent(action -> action.execute(holder));

	}

}
