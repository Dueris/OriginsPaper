package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.action.BiEntityAction;
import io.github.dueris.originspaper.action.ItemAction;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.condition.BiEntityCondition;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.condition.ItemCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.mixin.ItemEntityAccessor;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.util.InventoryUtil;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ActionOnItemPickupPowerType extends PowerType implements Prioritized<ActionOnItemPickupPowerType> {

	public static final TypedDataObjectFactory<ActionOnItemPickupPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("bientity_action", BiEntityAction.DATA_TYPE.optional(), Optional.empty())
			.add("item_action", ItemAction.DATA_TYPE.optional(), Optional.empty())
			.add("bientity_condition", BiEntityCondition.DATA_TYPE.optional(), Optional.empty())
			.add("item_condition", ItemCondition.DATA_TYPE.optional(), Optional.empty())
			.add("priority", SerializableDataTypes.INT, 0),
		(data, condition) -> new ActionOnItemPickupPowerType(
			data.get("bientity_action"),
			data.get("item_action"),
			data.get("bientity_condition"),
			data.get("item_condition"),
			data.get("priority"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("bientity_action", powerType.biEntityAction)
			.set("item_action", powerType.itemAction)
			.set("bientity_condition", powerType.biEntityCondition)
			.set("item_condition", powerType.itemCondition)
			.set("priority", powerType.getPriority())
	);

	private final Optional<BiEntityAction> biEntityAction;
	private final Optional<ItemAction> itemAction;

	private final Optional<BiEntityCondition> biEntityCondition;
	private final Optional<ItemCondition> itemCondition;

	private final int priority;

	public ActionOnItemPickupPowerType(Optional<BiEntityAction> biEntityAction, Optional<ItemAction> itemAction, Optional<BiEntityCondition> biEntityCondition, Optional<ItemCondition> itemCondition, int priority, Optional<EntityCondition> condition) {
		super(condition);
		this.biEntityAction = biEntityAction;
		this.itemAction = itemAction;
		this.biEntityCondition = biEntityCondition;
		this.itemCondition = itemCondition;
		this.priority = priority;
	}

	public void executeItemAction(SlotAccess stackReference) {
		itemAction.ifPresent(action -> action.execute(getHolder().level(), stackReference));
	}

	public static void executeBiEntityAction(CallInstance<ActionOnItemPickupPowerType> callInstance, Entity throwerEntity) {

		for (int i = callInstance.getMaxPriority(); i >= callInstance.getMinPriority(); i--) {
			callInstance.forEach(i, powerType -> powerType.executeBiEntityAction(throwerEntity));
		}

	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.ACTION_ON_ITEM_PICKUP;
	}

	@Override
	public int getPriority() {
		return priority;
	}

	public boolean doesApply(ItemStack stack, Entity thrower) {
		return itemCondition.map(condition -> condition.test(getHolder().level(), stack)).orElse(true)
			&& biEntityCondition.map(condition -> condition.test(thrower, getHolder())).orElse(true);
	}

	public void executeBiEntityAction(Entity thrower) {
		biEntityAction.ifPresent(action -> action.execute(thrower, getHolder()));
	}

	public static CallInstance<ActionOnItemPickupPowerType> executeItemAction(Entity throwerEntity, SlotAccess stackReference, Entity entity) {

		if (PowerHolderComponent.getOptional(entity).isEmpty()) {
			return new CallInstance<>();
		}

		CallInstance<ActionOnItemPickupPowerType> callInstance = new CallInstance<>();
		callInstance.add(entity, ActionOnItemPickupPowerType.class, powerType -> powerType.doesApply(stackReference.get(), throwerEntity));

		for (int i = callInstance.getMaxPriority(); i >= callInstance.getMinPriority(); i--) {
			callInstance.forEach(i, powerType -> powerType.executeItemAction(stackReference));
		}

		return callInstance;

	}
}
