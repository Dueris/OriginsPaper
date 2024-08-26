package io.github.dueris.originspaper.power;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionTypeFactory;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
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
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class PreventBlockUsePower extends PowerType {
	private final ActionTypeFactory<Entity> entityAction;
	private final ActionTypeFactory<Triple<Level, BlockPos, Direction>> blockAction;
	private final ActionTypeFactory<Tuple<Level, SlotAccess>> resultItemAction;
	private final ActionTypeFactory<Tuple<Level, SlotAccess>> heldItemAction;
	private final ConditionTypeFactory<BlockInWorld> blockCondition;
	private final ConditionTypeFactory<Tuple<Level, ItemStack>> itemCondition;
	private final ItemStack resultStack;
	private final EnumSet<Direction> directions;
	private final EnumSet<InteractionHand> hands;

	public PreventBlockUsePower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
								ActionTypeFactory<Entity> entityAction, ActionTypeFactory<Triple<Level, BlockPos, Direction>> blockAction, ActionTypeFactory<Tuple<Level, SlotAccess>> resultItemAction,
								ActionTypeFactory<Tuple<Level, SlotAccess>> heldItemAction, ConditionTypeFactory<BlockInWorld> blockCondition, ConditionTypeFactory<Tuple<Level, ItemStack>> itemCondition, ItemStack resultStack,
								EnumSet<Direction> directions, EnumSet<InteractionHand> hands) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.entityAction = entityAction;
		this.blockAction = blockAction;
		this.resultItemAction = resultItemAction;
		this.heldItemAction = heldItemAction;
		this.blockCondition = blockCondition;
		this.itemCondition = itemCondition;
		this.resultStack = resultStack;
		this.directions = directions;
		this.hands = hands;
	}

	public static SerializableData buildFactory() {
		return PowerType.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("prevent_block_use"))
			.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
			.add("block_action", ApoliDataTypes.BLOCK_ACTION, null)
			.add("result_item_action", ApoliDataTypes.ITEM_ACTION, null)
			.add("held_item_action", ApoliDataTypes.ITEM_ACTION, null)
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

	public void executeActions(Entity entity, Direction direction, BlockPos pos, InteractionHand hand) {

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
			if (resultStack != null) {
				heldStack.set(resultStack);
			}
			SlotAccess resultingStack = Util.createStackReference(resultStack == null ? heldStack.get() : resultStack.copy());
			boolean modified = resultStack != null;
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

	public boolean doesPrevent(Entity entity, Direction direction, BlockPos pos, ItemStack heldStack, InteractionHand hand) {
		return directions.contains(direction)
			&& shouldExecute$apoli$super(hand, heldStack, (ServerLevel) entity.level())
			&& (blockCondition == null || blockCondition.test(new BlockInWorld(entity.level(), pos, true)));
	}

	@EventHandler
	public void onBlockUse(@NotNull PlayerInteractEvent e) {
		if (e.getClickedBlock() != null) {
			Player player = ((CraftPlayer) e.getPlayer()).getHandle();
			Direction direction = CraftBlock.blockFaceToNotch(e.getBlockFace());
			BlockPos pos = CraftLocation.toBlockPosition(e.getClickedBlock().getLocation());
			InteractionHand hand = CraftEquipmentSlot.getHand(e.getHand());

			if (!getPlayers().contains(player) || !isActive(player)) return;

			if (doesPrevent(player, direction, pos, player.getItemInHand(hand), hand)) {
				executeActions(player, direction, pos, hand);
				e.setCancelled(true);
			}
		}
	}

}
