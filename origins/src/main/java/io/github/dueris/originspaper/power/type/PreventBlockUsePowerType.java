package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import io.github.dueris.originspaper.util.BlockUsagePhase;
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

import java.util.EnumSet;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class PreventBlockUsePowerType extends ActiveInteractionPowerType {

	private final Predicate<BlockInWorld> blockCondition;

	private final Consumer<Entity> entityAction;
	private final Consumer<Triple<Level, BlockPos, Direction>> blockAction;

	private final EnumSet<Direction> directions;
	private final EnumSet<BlockUsagePhase> usePhases;

	public PreventBlockUsePowerType(Power power, LivingEntity entity, Consumer<Entity> entityAction, Consumer<Triple<Level, BlockPos, Direction>> blockAction, Consumer<Tuple<Level, SlotAccess>> resultItemAction, Consumer<Tuple<Level, SlotAccess>> heldItemAction, Predicate<BlockInWorld> blockCondition, Predicate<Tuple<Level, ItemStack>> itemCondition, ItemStack resultStack, EnumSet<Direction> directions, EnumSet<InteractionHand> hands, EnumSet<BlockUsagePhase> usePhases, int priority) {
		super(power, entity, hands, InteractionResult.FAIL, itemCondition, heldItemAction, resultStack, resultItemAction, priority);
		this.blockCondition = blockCondition;
		this.entityAction = entityAction;
		this.blockAction = blockAction;
		this.directions = directions;
		this.usePhases = usePhases;
	}

	public void executeActions(BlockHitResult hitResult, InteractionHand hand) {

		if (blockAction != null) {
			this.blockAction.accept(Triple.of(entity.level(), hitResult.getBlockPos(), hitResult.getDirection()));
		}

		if (entityAction != null) {
			this.entityAction.accept(entity);
		}

		if (entity instanceof Player player) {
			this.performActorItemStuff(this, player, hand);
		}

	}

	public boolean doesPrevent(BlockUsagePhase usePhase, BlockHitResult hitResult, ItemStack heldStack, InteractionHand hand) {
		return usePhases.contains(usePhase)
			&& directions.contains(hitResult.getDirection())
			&& super.shouldExecute(hand, heldStack)
			&& (blockCondition == null || blockCondition.test(new BlockInWorld(entity.level(), hitResult.getBlockPos(), true)));
	}

	public static boolean doesPrevent(Entity holder, BlockUsagePhase usePhase, BlockHitResult hitResult, ItemStack heldStack, InteractionHand hand) {

		CallInstance<ActiveInteractionPowerType> aipci = new CallInstance<>();
		aipci.add(holder, PreventBlockUsePowerType.class, p -> p.doesPrevent(usePhase, hitResult, heldStack, hand));

		for (int i = aipci.getMaxPriority(); i >= aipci.getMinPriority(); i--) {
			aipci.forEach(i, p -> ((PreventBlockUsePowerType) p).executeActions(hitResult, hand));
		}

		return !aipci.isEmpty();

	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("prevent_block_use"),
			new SerializableData()
				.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
				.add("block_action", ApoliDataTypes.BLOCK_ACTION, null)
				.add("result_item_action", ApoliDataTypes.ITEM_ACTION, null)
				.add("held_item_action", ApoliDataTypes.ITEM_ACTION, null)
				.add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null)
				.add("item_condition", ApoliDataTypes.ITEM_CONDITION, null)
				.add("result_stack", SerializableDataTypes.ITEM_STACK, null)
				.add("directions", SerializableDataTypes.DIRECTION_SET, EnumSet.allOf(Direction.class))
				.add("hands", SerializableDataTypes.HAND_SET, EnumSet.allOf(InteractionHand.class))
				.add("usage_phases", ApoliDataTypes.BLOCK_USAGE_PHASE_SET, EnumSet.allOf(BlockUsagePhase.class))
				.add("priority", SerializableDataTypes.INT, 0),
			data -> (power, entity) -> new PreventBlockUsePowerType(power, entity,
				data.get("entity_action"),
				data.get("block_action"),
				data.get("result_item_action"),
				data.get("held_item_action"),
				data.get("block_condition"),
				data.get("item_condition"),
				data.get("result_stack"),
				data.get("directions"),
				data.get("hands"),
				data.get("usage_phases"),
				data.get("priority")
			)
		).allowCondition();
	}

}
