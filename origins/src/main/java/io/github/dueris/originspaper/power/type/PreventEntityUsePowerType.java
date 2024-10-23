package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.EnumSet;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class PreventEntityUsePowerType extends InteractionPowerType {

	private final Consumer<Tuple<Entity, Entity>> biEntityAction;
	private final Predicate<Tuple<Entity, Entity>> bientityCondition;

	public PreventEntityUsePowerType(Power power, LivingEntity entity, EnumSet<InteractionHand> hands, Predicate<Tuple<Level, ItemStack>> itemCondition, Consumer<Tuple<Level, SlotAccess>> heldItemAction, ItemStack itemResult, Consumer<Tuple<Level, SlotAccess>> itemAction, Consumer<Tuple<Entity, Entity>> biEntityAction, Predicate<Tuple<Entity, Entity>> bientityCondition) {
		super(power, entity, hands, InteractionResult.FAIL, itemCondition, heldItemAction, itemResult, itemAction);
		this.biEntityAction = biEntityAction;
		this.bientityCondition = bientityCondition;
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("prevent_entity_use"),
			new SerializableData()
				.add("bientity_action", ApoliDataTypes.BIENTITY_ACTION, null)
				.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
				.add("item_condition", ApoliDataTypes.ITEM_CONDITION, null)
				.add("hands", SerializableDataTypes.HAND_SET, EnumSet.allOf(InteractionHand.class))
				.add("result_stack", SerializableDataTypes.ITEM_STACK, null)
				.add("held_item_action", ApoliDataTypes.ITEM_ACTION, null)
				.add("result_item_action", ApoliDataTypes.ITEM_ACTION, null),
			data -> (power, entity) -> new PreventEntityUsePowerType(power, entity,
				data.get("hands"),
				data.get("item_condition"),
				data.get("held_item_action"),
				data.get("result_stack"),
				data.get("result_item_action"),
				data.get("bientity_action"),
				data.get("bientity_condition")
			)
		).allowCondition();
	}

	public boolean doesApply(Entity other, InteractionHand hand, ItemStack heldStack) {
		if (!shouldExecute(hand, heldStack)) {
			return false;
		}
		return bientityCondition == null || bientityCondition.test(new Tuple<>(entity, other));
	}

	public InteractionResult executeAction(Entity other, InteractionHand hand) {

		if (biEntityAction != null) {
			biEntityAction.accept(new Tuple<>(entity, other));
		}

		if (entity instanceof Player player) {
			this.performActorItemStuff(this, player, hand);
		}

		return this.getActionResult();

	}

}
