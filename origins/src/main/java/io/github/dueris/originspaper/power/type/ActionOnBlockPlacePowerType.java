package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.BlockAction;
import io.github.dueris.originspaper.action.EntityAction;
import io.github.dueris.originspaper.action.ItemAction;
import io.github.dueris.originspaper.condition.BlockCondition;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.condition.ItemCondition;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerConfiguration;
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
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ActionOnBlockPlacePowerType extends ActiveInteractionPowerType {

	public static final TypedDataObjectFactory<ActionOnBlockPlacePowerType> DATA_FACTORY = ActiveInteractionPowerType.createConditionedDataFactory(
		new SerializableData()
			.add("entity_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
			.add("place_to_action", BlockAction.DATA_TYPE.optional(), Optional.empty())
			.add("place_on_action", BlockAction.DATA_TYPE.optional(), Optional.empty())
			.add("place_to_condition", BlockCondition.DATA_TYPE.optional(), Optional.empty())
			.add("place_on_condition", BlockCondition.DATA_TYPE.optional(), Optional.empty())
			.add("directions", SerializableDataTypes.DIRECTION_SET, EnumSet.allOf(Direction.class)),
		(data, heldItemAction, heldItemCondition, resultItemAction, resultStack, hands, actionResult, priority, condition) -> new ActionOnBlockPlacePowerType(
			data.get("entity_action"),
			data.get("place_to_action"),
			data.get("place_on_action"),
			data.get("place_to_condition"),
			data.get("place_on_condition"),
			data.get("directions"),
			heldItemAction,
			heldItemCondition,
			resultItemAction,
			resultStack,
			hands,
			priority,
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("entity_action", powerType.entityAction)
			.set("place_to_action", powerType.placeToAction)
			.set("place_on_action", powerType.placeOnAction)
			.set("place_to_condition", powerType.placeToCondition)
			.set("place_on_condition", powerType.placeOnCondition)
	);

	private final Optional<EntityAction> entityAction;
	private final Optional<BlockAction> placeToAction;
	private final Optional<BlockAction> placeOnAction;

	private final Optional<BlockCondition> placeToCondition;
	private final Optional<BlockCondition> placeOnCondition;

	private final EnumSet<Direction> directions;

	public ActionOnBlockPlacePowerType(Optional<EntityAction> entityAction, Optional<BlockAction> placeToAction, Optional<BlockAction> placeOnAction, Optional<BlockCondition> placeToCondition, Optional<BlockCondition> placeOnCondition, EnumSet<Direction> directions, Optional<ItemAction> heldItemAction, Optional<ItemCondition> heldItemCondition, Optional<ItemAction> resultItemAction, Optional<ItemStack> resultStack, EnumSet<InteractionHand> hands, int priority, Optional<EntityCondition> condition) {
		super(heldItemAction, heldItemCondition, resultItemAction, resultStack, hands, InteractionResult.SUCCESS, priority, condition);
		this.entityAction = entityAction;
		this.placeToAction = placeToAction;
		this.placeOnAction = placeOnAction;
		this.placeToCondition = placeToCondition;
		this.placeOnCondition = placeOnCondition;
		this.directions = directions;
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.ACTION_ON_BLOCK_PLACE;
	}

	public boolean shouldExecute(ItemStack heldStack, InteractionHand hand, BlockPos toPos, BlockPos onPos, Direction direction) {
		return super.shouldExecute(hand, heldStack)
			&& directions.contains(direction)
			&& placeOnCondition.map(condition -> condition.test(getHolder().level(), onPos)).orElse(true)
			&& placeToCondition.map(condition -> condition.test(getHolder().level(), toPos)).orElse(true);
	}

	public void executeOtherActions(BlockPos toPos, BlockPos onPos, Direction direction) {

		placeOnAction.ifPresent(action -> action.execute(getHolder().level(), onPos, Optional.of(direction)));
		placeToAction.ifPresent(action -> action.execute(getHolder().level(), toPos, Optional.of(direction)));

		entityAction.ifPresent(action -> action.execute(getHolder()));

	}

	public void executeItemActions(InteractionHand hand) {

		if (getHolder() instanceof Player playerEntity) {
			this.performActorItemStuff(playerEntity, hand);
		}

	}

}
