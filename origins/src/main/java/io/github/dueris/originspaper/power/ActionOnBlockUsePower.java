package io.github.dueris.originspaper.power;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.SlotRanges;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class ActionOnBlockUsePower extends PowerType {
	private final ActionFactory<Entity> entityAction;
	private final ActionFactory<Triple<Level, BlockPos, Direction>> blockAction;
	private final ActionFactory<Tuple<Level, SlotAccess>> heldItemAction;
	private final ActionFactory<Tuple<Level, SlotAccess>> resultItemAction;
	private final ConditionFactory<BlockInWorld> blockCondition;
	private final ConditionFactory<Tuple<Level, ItemStack>> itemCondition;
	private final ItemStack itemResult;
	private final EnumSet<Direction> directions;
	private final EnumSet<InteractionHand> hands;

	public ActionOnBlockUsePower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority,
								 ActionFactory<Entity> entityAction, ActionFactory<Triple<Level, BlockPos, Direction>> blockAction, ActionFactory<Tuple<Level, SlotAccess>> heldItemAction,
								 ActionFactory<Tuple<Level, SlotAccess>> resultItemAction, ConditionFactory<BlockInWorld> blockCondition, ConditionFactory<Tuple<Level, ItemStack>> itemCondition, ItemStack itemResult, EnumSet<Direction> directions,
								 EnumSet<InteractionHand> hands) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.entityAction = entityAction;
		this.blockAction = blockAction;
		this.heldItemAction = heldItemAction;
		this.resultItemAction = resultItemAction;
		this.blockCondition = blockCondition;
		this.itemCondition = itemCondition;
		this.itemResult = itemResult;
		this.directions = directions;
		this.hands = hands;
	}

	public static SerializableData buildFactory() {
		return PowerType.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("action_on_block_use"))
			.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
			.add("block_action", ApoliDataTypes.BLOCK_ACTION, null)
			.add("held_item_action", ApoliDataTypes.ITEM_ACTION, null)
			.add("result_item_action", ApoliDataTypes.ITEM_ACTION, null)
			.add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null)
			.add("item_condition", ApoliDataTypes.ITEM_CONDITION, null)
			.add("result_stack", SerializableDataTypes.ITEM_STACK, null)
			.add("directions", SerializableDataTypes.DIRECTION_SET, EnumSet.allOf(Direction.class))
			.add("hands", SerializableDataTypes.enumSet(InteractionHand.class, SerializableDataTypes.HAND), EnumSet.allOf(InteractionHand.class));
	}

	public boolean shouldExecute$apoli$super(InteractionHand hand, ItemStack heldStack, ServerLevel level) {
		if (!doesApplyToHand(hand)) {
			return false;
		}
		return doesApplyToItem(heldStack, level);
	}

	public boolean doesApplyToHand(InteractionHand hand) {
		return hands.contains(hand);
	}

	public boolean doesApplyToItem(ItemStack heldStack, ServerLevel level) {
		return itemCondition == null || itemCondition.test(new Tuple<>(level, heldStack));
	}

	public boolean shouldExecute(InteractionHand hand, ItemStack heldStack, Direction direction, BlockPos pos, Entity entity) {
		return directions.contains(direction)
			&& shouldExecute$apoli$super(hand, heldStack, (ServerLevel) entity.level())
			&& (blockCondition == null || blockCondition.test(new BlockInWorld(entity.level(), pos, true)));
	}

	public void executeAction(Direction direction, BlockPos pos, InteractionHand hand, Entity entity) {

		if (blockAction != null) {
			this.blockAction.accept(Triple.of(entity.level(), pos, direction));
		}

		if (entityAction != null) {
			this.entityAction.accept(entity);
		}

		if (entity instanceof Player other) {
			SlotAccess heldStack = other.getSlot(hand == InteractionHand.OFF_HAND ? SlotRanges.nameToIds("weapon.offhand").slots().getFirst() : SlotRanges.nameToIds("weapon.mainhand").slots().getFirst());
			if (heldItemAction != null) {
				heldItemAction.accept(new Tuple<>(other.level(), heldStack));
			}
			if (itemResult != null) {
				heldStack.set(itemResult);
			}
			SlotAccess resultingStack = Util.createStackReference(itemResult == null ? heldStack.get() : itemResult.copy());
			boolean modified = itemResult != null;
			if (resultItemAction != null) {
				resultItemAction.accept(new Tuple<>(other.level(), heldStack));
				modified = true;
			}
			if (modified) {
				if (heldStack.get().isEmpty()) {
					other.setItemInHand(hand, resultingStack.get());
				} else {
					other.getInventory().placeItemBackInInventory(resultingStack.get());
				}
			}
		}

	}

	@EventHandler
	public void onBlockUse(@NotNull PlayerInteractEvent e) {
		if (e.getClickedBlock() != null) {
			Player player = ((CraftPlayer) e.getPlayer()).getHandle();
			Direction direction = CraftBlock.blockFaceToNotch(e.getBlockFace());
			BlockPos pos = CraftLocation.toBlockPosition(e.getClickedBlock().getLocation());
			InteractionHand hand = CraftEquipmentSlot.getHand(e.getHand());

			if (getPlayers().contains(player) && isActive(player) && shouldExecute(hand, e.getItem() != null ? CraftItemStack.unwrap(e.getItem()) : ItemStack.EMPTY, direction, pos, player)) {
				executeAction(direction, pos, hand, player);
			}
		}
	}
}
