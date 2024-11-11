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
import io.github.dueris.originspaper.util.BlockUsagePhase;
import io.github.dueris.originspaper.util.PriorityPhase;
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
import net.minecraft.world.phys.BlockHitResult;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ActionOnBlockUsePowerType extends ActiveInteractionPowerType {

	public static final TypedDataObjectFactory<ActionOnBlockUsePowerType> DATA_FACTORY = ActiveInteractionPowerType.createConditionedDataFactory(
		new SerializableData()
			.add("entity_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
			.add("block_action", BlockAction.DATA_TYPE.optional(), Optional.empty())
			.add("block_condition", BlockCondition.DATA_TYPE.optional(), Optional.empty())
			.add("use_phases", ApoliDataTypes.BLOCK_USAGE_PHASE_SET, EnumSet.allOf(BlockUsagePhase.class))
			.add("directions", SerializableDataTypes.DIRECTION_SET, EnumSet.allOf(Direction.class)),
		(data, heldItemAction, heldItemCondition, resultItemAction, resultStack, hands, actionResult, priority, condition) -> new ActionOnBlockUsePowerType(
			data.get("entity_action"),
			data.get("block_action"),
			data.get("block_condition"),
			data.get("use_phases"),
			data.get("directions"),
			heldItemAction,
			heldItemCondition,
			resultItemAction,
			resultStack,
			hands,
			actionResult,
			priority,
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("entity_action", powerType.entityAction)
			.set("block_action", powerType.blockAction)
			.set("block_condition", powerType.blockCondition)
			.set("use_phases", powerType.usePhases)
			.set("directions", powerType.directions)
	);

	private final Optional<EntityAction> entityAction;
	private final Optional<BlockAction> blockAction;

	private final Optional<BlockCondition> blockCondition;

	private final EnumSet<BlockUsagePhase> usePhases;
	private final EnumSet<Direction> directions;

	public ActionOnBlockUsePowerType(Optional<EntityAction> entityAction, Optional<BlockAction> blockAction, Optional<BlockCondition> blockCondition, EnumSet<BlockUsagePhase> usePhases, EnumSet<Direction> directions, Optional<ItemAction> heldItemAction, Optional<ItemCondition> heldItemCondition, Optional<ItemAction> resultItemAction, Optional<ItemStack> resultStack, EnumSet<InteractionHand> hands, InteractionResult actionResult, int priority, Optional<EntityCondition> condition) {
		super(heldItemAction, heldItemCondition, resultItemAction, resultStack, hands, actionResult, priority, condition);
		this.entityAction = entityAction;
		this.blockCondition = blockCondition;
		this.directions = directions;
		this.blockAction = blockAction;
		this.usePhases = usePhases;
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.ACTION_ON_BLOCK_USE;
	}

	public boolean shouldExecute(BlockUsagePhase usePhase, PriorityPhase priorityPhase, BlockHitResult hitResult, InteractionHand hand, ItemStack heldStack) {
		return priorityPhase.test(this.getPriority())
			&& usePhases.contains(usePhase)
			&& directions.contains(hitResult.getDirection())
			&& super.shouldExecute(hand, heldStack)
			&& blockCondition.map(condition -> condition.test(getHolder().level(), hitResult.getBlockPos())).orElse(true);
	}

	public InteractionResult executeAction(BlockHitResult hitResult, InteractionHand hand) {

		blockAction.ifPresent(action -> action.execute(getHolder().level(), hitResult.getBlockPos(), Optional.of(hitResult.getDirection())));
		entityAction.ifPresent(action -> action.execute(getHolder()));

		if (getHolder() instanceof Player player) {
			this.performActorItemStuff(player, hand);
		}

		return this.getActionResult();

	}

}
