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

public class ActionOnItemPickupPowerType extends PowerType implements Prioritized<ActionOnItemPickupPowerType> {

	private final Consumer<Tuple<Entity, Entity>> biEntityAction;
	private final Consumer<Tuple<Level, SlotAccess>> itemAction;

	private final Predicate<Tuple<Entity, Entity>> biEntityCondition;
	private final Predicate<Tuple<Level, ItemStack>> itemCondition;

	private final int priority;

	public ActionOnItemPickupPowerType(Power power, LivingEntity entity, Consumer<Tuple<Entity, Entity>> biEntityAction, Predicate<Tuple<Entity, Entity>> biEntityCondition, Consumer<Tuple<Level, SlotAccess>> itemAction, Predicate<Tuple<Level, ItemStack>> itemCondition, int priority) {
		super(power, entity);
		this.biEntityAction = biEntityAction;
		this.itemAction = itemAction;
		this.biEntityCondition = biEntityCondition;
		this.itemCondition = itemCondition;
		this.priority = priority;
	}

	public static void executeActions(ItemEntity itemEntity, Entity entity) {

		if (!PowerHolderComponent.KEY.isProvidedBy(entity)) {
			return;
		}

		ItemStack stack = itemEntity.getItem();
		Entity throwerEntity = Util.getEntityByUuid(((ItemEntityAccessor) itemEntity).getThrower(), entity.getServer());

		CallInstance<ActionOnItemPickupPowerType> aoippci = new CallInstance<>();
		aoippci.add(entity, ActionOnItemPickupPowerType.class, p -> p.doesApply(stack, throwerEntity));

		for (int i = aoippci.getMaxPriority(); i >= aoippci.getMinPriority(); i--) {
			aoippci.forEach(i, p -> p.executeActions(stack, throwerEntity));
		}

	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("action_on_item_pickup"),
			new SerializableData()
				.add("bientity_action", ApoliDataTypes.BIENTITY_ACTION, null)
				.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
				.add("item_action", ApoliDataTypes.ITEM_ACTION, null)
				.add("item_condition", ApoliDataTypes.ITEM_CONDITION, null)
				.add("priority", SerializableDataTypes.INT, 0),
			data -> (power, entity) -> new ActionOnItemPickupPowerType(power, entity,
				data.get("bientity_action"),
				data.get("bientity_condition"),
				data.get("item_action"),
				data.get("item_condition"),
				data.get("priority")
			)
		).allowCondition();
	}

	@Override
	public int getPriority() {
		return priority;
	}

	public boolean doesApply(ItemStack stack, Entity thrower) {
		return (itemCondition == null || itemCondition.test(new Tuple<>(entity.level(), stack)))
			&& (biEntityCondition == null || biEntityCondition.test(new Tuple<>(thrower, entity)));
	}

	public void executeActions(ItemStack stack, Entity thrower) {
		if (itemAction != null)
			itemAction.accept(new Tuple<>(entity.level(), InventoryUtil.getStackReferenceFromStack(entity, stack)));
		if (biEntityAction != null) biEntityAction.accept(new Tuple<>(thrower, entity));
	}

}

