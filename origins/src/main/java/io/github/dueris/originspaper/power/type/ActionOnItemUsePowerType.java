package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import io.github.dueris.originspaper.util.PriorityPhase;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ActionOnItemUsePowerType extends PowerType implements Prioritized<ActionOnItemUsePowerType> {

	private final Predicate<Tuple<Level, ItemStack>> itemCondition;
	private final Consumer<Entity> entityAction;
	private final Consumer<Tuple<Level, SlotAccess>> itemAction;
	private final TriggerType triggerType;
	private final int priority;

	public ActionOnItemUsePowerType(Power power, LivingEntity entity, Consumer<Entity> entityAction, Consumer<Tuple<Level, SlotAccess>> itemAction, Predicate<Tuple<Level, ItemStack>> itemCondition, TriggerType triggerType, int priority) {
		super(power, entity);
		this.itemCondition = itemCondition;
		this.entityAction = entityAction;
		this.itemAction = itemAction;
		this.triggerType = triggerType;
		this.priority = priority;
	}

	public static void executeActions(Entity user, SlotAccess useStack, ItemStack checkStack, TriggerType triggerType, PriorityPhase phase) {

		if (user.level().isClientSide) {
			return;
		}

		ActionOnItemUsePowerType.CallInstance<ActionOnItemUsePowerType> aoiupci = new ActionOnItemUsePowerType.CallInstance<>();
		aoiupci.add(user, ActionOnItemUsePowerType.class, p -> p.doesApply(checkStack, triggerType, phase));

		for (int i = aoiupci.getMaxPriority(); i >= aoiupci.getMinPriority(); i--) {
			aoiupci.forEach(i, aoiup -> aoiup.executeActions(useStack));
		}

	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("action_on_item_use"),
			new SerializableData()
				.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
				.add("item_action", ApoliDataTypes.ITEM_ACTION, null)
				.add("item_condition", ApoliDataTypes.ITEM_CONDITION, null)
				.add("trigger", SerializableDataType.enumValue(TriggerType.class), TriggerType.FINISH)
				.add("priority", SerializableDataTypes.INT, 0),
			data -> (power, entity) -> new ActionOnItemUsePowerType(power, entity,
				data.get("entity_action"),
				data.get("item_action"),
				data.get("item_condition"),
				data.get("trigger"),
				data.get("priority")
			)
		).allowCondition();
	}

	public boolean doesApply(ItemStack stack, TriggerType triggerType, PriorityPhase priorityPhase) {
		return this.triggerType == triggerType
			&& priorityPhase.test(this.getPriority())
			&& (itemCondition == null || itemCondition.test(new Tuple<>(entity.level(), stack)));
	}

	public void executeActions(SlotAccess stack) {

		if (itemAction != null) {
			itemAction.accept(new Tuple<>(entity.level(), stack));
		}

		if (entityAction != null) {
			entityAction.accept(entity);
		}

	}

	@Override
	public int getPriority() {
		return this.priority;
	}

	public enum TriggerType {
		INSTANT, START, STOP, FINISH, DURING
	}
}

