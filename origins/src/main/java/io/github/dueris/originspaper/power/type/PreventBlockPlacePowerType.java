package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.apache.commons.lang3.tuple.Triple;

import java.util.EnumSet;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class PreventBlockPlacePowerType extends ActiveInteractionPowerType {

	private final Consumer<Entity> entityAction;
	private final Consumer<Triple<Level, BlockPos, Direction>> placeToAction;
	private final Consumer<Triple<Level, BlockPos, Direction>> placeOnAction;

	private final Predicate<BlockInWorld> placeToCondition;
	private final Predicate<BlockInWorld> placeOnCondition;

	private final EnumSet<Direction> directions;

	public PreventBlockPlacePowerType(Power power, LivingEntity entity, Consumer<Entity> entityAction, Consumer<Triple<Level, BlockPos, Direction>> placeToAction, Consumer<Triple<Level, BlockPos, Direction>> placeOnAction, Predicate<Tuple<Level, ItemStack>> itemCondition, Predicate<BlockInWorld> placeToCondition, Predicate<BlockInWorld> placeOnCondition, EnumSet<Direction> directions, EnumSet<InteractionHand> hands, ItemStack resultStack, Consumer<Tuple<Level, SlotAccess>> resultItemAction, Consumer<Tuple<Level, SlotAccess>> heldItemAction, int priority) {
		super(power, entity, hands, InteractionResult.FAIL, itemCondition, heldItemAction, resultStack, resultItemAction, priority);
		this.entityAction = entityAction;
		this.placeToAction = placeToAction;
		this.placeOnAction = placeOnAction;
		this.placeToCondition = placeToCondition;
		this.placeOnCondition = placeOnCondition;
		this.directions = directions;
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("prevent_block_place"),
			new SerializableData()
				.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
				.add("place_to_action", ApoliDataTypes.BLOCK_ACTION, null)
				.add("place_on_action", ApoliDataTypes.BLOCK_ACTION, null)
				.add("item_condition", ApoliDataTypes.ITEM_CONDITION, null)
				.add("place_to_condition", ApoliDataTypes.BLOCK_CONDITION, null)
				.add("place_on_condition", ApoliDataTypes.BLOCK_CONDITION, null)
				.add("directions", SerializableDataTypes.DIRECTION_SET, EnumSet.allOf(Direction.class))
				.add("hands", SerializableDataTypes.HAND_SET, EnumSet.allOf(InteractionHand.class))
				.add("result_stack", SerializableDataTypes.ITEM_STACK, null)
				.add("result_item_action", ApoliDataTypes.ITEM_ACTION, null)
				.add("held_item_action", ApoliDataTypes.ITEM_ACTION, null)
				.add("priority", SerializableDataTypes.INT, 0),
			data -> (power, entity) -> new PreventBlockPlacePowerType(power, entity,
				data.get("entity_action"),
				data.get("place_to_action"),
				data.get("place_on_action"),
				data.get("item_condition"),
				data.get("place_to_condition"),
				data.get("place_on_condition"),
				data.get("directions"),
				data.get("hands"),
				data.get("result_stack"),
				data.get("result_item_action"),
				data.get("held_item_action"),
				data.get("priority")
			)
		).allowCondition();
	}

	public boolean doesPrevent(ItemStack heldStack, InteractionHand hand, BlockPos toPos, BlockPos onPos, Direction direction) {
		return (super.shouldExecute(hand, heldStack) && directions.contains(direction))
			&& ((placeOnCondition == null || placeOnCondition.test(new BlockInWorld(entity.level(), onPos, true)))
			&& (placeToCondition == null || placeToCondition.test(new BlockInWorld(entity.level(), toPos, true))));
	}

	public void executeActions(InteractionHand hand, BlockPos toPos, BlockPos onPos, Direction direction) {

		if (placeOnAction != null) {
			placeOnAction.accept(Triple.of(entity.level(), onPos, direction));
		}

		if (placeToAction != null) {
			placeToAction.accept(Triple.of(entity.level(), toPos, direction));
		}

		if (entityAction != null) {
			entityAction.accept(entity);
		}

		if (entity instanceof Player playerEntity) {
			performActorItemStuff(this, playerEntity, hand);
		}

	}

}
