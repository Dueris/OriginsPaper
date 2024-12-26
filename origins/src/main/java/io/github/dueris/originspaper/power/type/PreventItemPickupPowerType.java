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

public class PreventItemPickupPowerType extends PowerType implements Prioritized<PreventItemPickupPowerType> {

	public static final TypedDataObjectFactory<PreventItemPickupPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("bientity_action_thrower", BiEntityAction.DATA_TYPE.optional(), Optional.empty())
			.add("bientity_action_item", BiEntityAction.DATA_TYPE.optional(), Optional.empty())
			.add("item_action", ItemAction.DATA_TYPE.optional(), Optional.empty())
			.add("bientity_condition", BiEntityCondition.DATA_TYPE.optional(), Optional.empty())
			.add("item_condition", ItemCondition.DATA_TYPE.optional(), Optional.empty())
			.add("priority", SerializableDataTypes.INT, 0),
		(data, condition) -> new PreventItemPickupPowerType(
			data.get("bientity_action_thrower"),
			data.get("bientity_action_item"),
			data.get("item_action"),
			data.get("bientity_condition"),
			data.get("item_condition"),
			data.get("priority"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("bientity_action_thrower", powerType.biEntityActionThrower)
			.set("bientity_action_item", powerType.biEntityActionItem)
			.set("item_action", powerType.itemAction)
			.set("bientity_condition", powerType.biEntityCondition)
			.set("item_condition", powerType.itemCondition)
			.set("priority", powerType.getPriority())
	);

	private final Optional<BiEntityAction> biEntityActionThrower;
	private final Optional<BiEntityAction> biEntityActionItem;

	private final Optional<ItemAction> itemAction;

	private final Optional<BiEntityCondition> biEntityCondition;
	private final Optional<ItemCondition> itemCondition;

	private final int priority;

	public PreventItemPickupPowerType(Optional<BiEntityAction> biEntityActionThrower, Optional<BiEntityAction> biEntityActionItem, Optional<ItemAction> itemAction, Optional<BiEntityCondition> biEntityCondition, Optional<ItemCondition> itemCondition, int priority, Optional<EntityCondition> condition) {
		super(condition);
		this.biEntityActionThrower = biEntityActionThrower;
		this.biEntityActionItem = biEntityActionItem;
		this.itemAction = itemAction;
		this.biEntityCondition = biEntityCondition;
		this.itemCondition = itemCondition;
		this.priority = priority;
	}

	public static boolean doesPrevent(ItemEntity itemEntity, Entity entity) {

		if (PowerHolderComponent.getOptional(entity).isEmpty()) {
			return false;
		}

		ItemStack stack = itemEntity.getItem();
		Entity throwerEntity = Util.getEntityByUuid(((ItemEntityAccessor) itemEntity).getThrower(), entity.getServer());

		CallInstance<PreventItemPickupPowerType> pippci = new CallInstance<>();
		pippci.add(entity, PreventItemPickupPowerType.class, p -> p.doesPrevent(stack, throwerEntity));

		boolean prevented = false;
		for (int i = pippci.getMaxPriority(); i >= pippci.getMinPriority(); i--) {

			if (!pippci.hasPowerTypes(i)) {
				continue;
			}

			pippci.forEach(i, p -> p.executeActions(itemEntity, throwerEntity));
			prevented = true;

		}

		return prevented;

	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.PREVENT_ITEM_PICKUP;
	}

	@Override
	public int getPriority() {
		return priority;
	}

	public boolean doesPrevent(ItemStack stack, Entity thrower) {
		return itemCondition.map(condition -> condition.test(getHolder().level(), stack)).orElse(true)
			&& biEntityCondition.map(condition -> condition.test(getHolder(), thrower)).orElse(true);
	}

	public void executeActions(ItemEntity itemEntity, Entity thrower) {

		SlotAccess stackReference = InventoryUtil.createStackReference(itemEntity.getItem());
		itemAction.ifPresent(action -> action.execute(getHolder().level(), stackReference));

		biEntityActionThrower.ifPresent(action -> action.execute(thrower, getHolder()));
		biEntityActionItem.ifPresent(action -> action.execute(getHolder(), itemEntity));

		itemEntity.setItem(stackReference.get());
	}

}
