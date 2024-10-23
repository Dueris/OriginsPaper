package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.mixin.ItemEntityAccessor;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import io.github.dueris.originspaper.util.InventoryUtil;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class PreventItemPickupPowerType extends PowerType implements Prioritized<PreventItemPickupPowerType> {

	private final Consumer<Tuple<Entity, Entity>> biEntityActionThrower;
	private final Consumer<Tuple<Entity, Entity>> biEntityActionItem;
	private final Consumer<Tuple<Level, SlotAccess>> itemAction;

	private final Predicate<Tuple<Entity, Entity>> biEntityCondition;
	private final Predicate<Tuple<Level, ItemStack>> itemCondition;

	private final int priority;

	public PreventItemPickupPowerType(Power power, LivingEntity entity, Consumer<Tuple<Entity, Entity>> biEntityActionThrower, Consumer<Tuple<Entity, Entity>> biEntityActionItem, Consumer<Tuple<Level, SlotAccess>> itemAction, Predicate<Tuple<Entity, Entity>> biEntityCondition, Predicate<Tuple<Level, ItemStack>> itemCondition, int priority) {
		super(power, entity);
		this.biEntityActionThrower = biEntityActionThrower;
		this.biEntityActionItem = biEntityActionItem;
		this.itemAction = itemAction;
		this.biEntityCondition = biEntityCondition;
		this.itemCondition = itemCondition;
		this.priority = priority;
	}

	public static boolean doesPrevent(ItemEntity itemEntity, Entity entity) {

		if (!PowerHolderComponent.KEY.isProvidedBy(entity)) {
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

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("prevent_item_pickup"),
			new SerializableData()
				.add("bientity_action_thrower", ApoliDataTypes.BIENTITY_ACTION, null)
				.add("bientity_action_item", ApoliDataTypes.BIENTITY_ACTION, null)
				.add("item_action", ApoliDataTypes.ITEM_ACTION, null)
				.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
				.add("item_condition", ApoliDataTypes.ITEM_CONDITION, null)
				.add("priority", SerializableDataTypes.INT, 0),
			data -> (power, entity) -> new PreventItemPickupPowerType(power, entity,
				data.get("bientity_action_thrower"),
				data.get("bientity_action_item"),
				data.get("item_action"),
				data.get("bientity_condition"),
				data.get("item_condition"),
				data.get("priority")
			)
		).allowCondition();
	}

	@Override
	public int getPriority() {
		return priority;
	}

	public boolean doesPrevent(ItemStack stack, Entity thrower) {
		return (itemCondition == null || itemCondition.test(new Tuple<>(entity.level(), stack)))
			&& (biEntityCondition == null || biEntityCondition.test(new Tuple<>(thrower, entity)));
	}

	public void executeActions(ItemEntity itemEntity, Entity thrower) {
		if (itemAction != null) {
			SlotAccess reference = InventoryUtil.createStackReference(itemEntity.getItem());
			itemAction.accept(new Tuple<>(entity.level(), reference));
			itemEntity.setItem(reference.get());
		}
		if (biEntityActionThrower != null) {
			biEntityActionThrower.accept(new Tuple<>(thrower, entity));
		}
		if (biEntityActionItem != null) {
			biEntityActionItem.accept(new Tuple<>(entity, itemEntity));
		}
	}

}
