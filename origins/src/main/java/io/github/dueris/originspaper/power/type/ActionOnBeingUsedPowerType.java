package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import io.github.dueris.originspaper.util.PriorityPhase;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ActionOnBeingUsedPowerType extends ActiveInteractionPowerType {

	private final Consumer<Tuple<Entity, Entity>> biEntityAction;
	private final Predicate<Tuple<Entity, Entity>> bientityCondition;

	public ActionOnBeingUsedPowerType(Power power, LivingEntity entity, Consumer<Tuple<Entity, Entity>> biEntityAction, Predicate<Tuple<Entity, Entity>> bientityCondition, Predicate<Tuple<Level, ItemStack>> itemCondition, EnumSet<InteractionHand> hands, ItemStack resultStack, Consumer<Tuple<Level, SlotAccess>> heldItemAction, Consumer<Tuple<Level, SlotAccess>> resultItemAction, InteractionResult actionResult, int priority) {
		super(power, entity, hands, actionResult, itemCondition, heldItemAction, resultStack, resultItemAction, priority);
		this.biEntityAction = biEntityAction;
		this.bientityCondition = bientityCondition;
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("action_on_being_used"),
			new SerializableData()
				.add("bientity_action", ApoliDataTypes.BIENTITY_ACTION, null)
				.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
				.add("item_condition", ApoliDataTypes.ITEM_CONDITION, null)
				.add("hands", SerializableDataTypes.HAND_SET, EnumSet.allOf(InteractionHand.class))
				.add("result_stack", SerializableDataTypes.ITEM_STACK, null)
				.add("held_item_action", ApoliDataTypes.ITEM_ACTION, null)
				.add("result_item_action", ApoliDataTypes.ITEM_ACTION, null)
				.add("action_result", SerializableDataTypes.ACTION_RESULT, InteractionResult.SUCCESS)
				.add("priority", SerializableDataTypes.INT, 0),
			data -> (power, entity) -> new ActionOnBeingUsedPowerType(power, entity,
				data.get("bientity_action"),
				data.get("bientity_condition"),
				data.get("item_condition"),
				data.get("hands"),
				data.get("result_stack"),
				data.get("held_item_action"),
				data.get("result_item_action"),
				data.get("action_result"),
				data.get("priority")
			)
		).allowCondition();
	}

	public boolean shouldExecute(Player other, InteractionHand hand, ItemStack heldStack, @NotNull PriorityPhase priorityPhase) {
		return priorityPhase.test(this.getPriority())
			&& super.shouldExecute(hand, heldStack)
			&& (bientityCondition == null || bientityCondition.test(new Tuple<>(other, entity)));
	}

	public InteractionResult executeAction(Player other, InteractionHand hand) {
		if (biEntityAction != null) {
			biEntityAction.accept(new Tuple<>(other, entity));
		}
		performActorItemStuff(this, other, hand);
		return getActionResult();
	}
}


