package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.power.factory.PowerTypeFactory;
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
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class ActionOnBlockPlacePower extends PowerType {
	private final ActionTypeFactory<Entity> entityAction;
	private final ActionTypeFactory<Triple<Level, BlockPos, Direction>> placeToAction;
	private final ActionTypeFactory<Triple<Level, BlockPos, Direction>> placeOnAction;
	private final ConditionTypeFactory<Tuple<Level, ItemStack>> itemCondition;
	private final ConditionTypeFactory<BlockInWorld> placeToCondition;
	private final ConditionTypeFactory<BlockInWorld> placeOnCondition;
	private final EnumSet<Direction> directions;
	private final EnumSet<InteractionHand> hands;
	private final ItemStack resultStack;
	private final ActionTypeFactory<Tuple<Level, SlotAccess>> resultItemAction;
	private final ActionTypeFactory<Tuple<Level, SlotAccess>> heldItemAction;

	public ActionOnBlockPlacePower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
								   ActionTypeFactory<Entity> entityAction, ActionTypeFactory<Triple<Level, BlockPos, Direction>> placeToAction, ActionTypeFactory<Triple<Level, BlockPos, Direction>> placeOnAction,
								   ConditionTypeFactory<Tuple<Level, ItemStack>> itemCondition, ConditionTypeFactory<BlockInWorld> placeToCondition, ConditionTypeFactory<BlockInWorld> placeOnCondition, EnumSet<Direction> directions,
								   EnumSet<InteractionHand> hands, ItemStack resultStack, ActionTypeFactory<Tuple<Level, SlotAccess>> resultItemAction, ActionTypeFactory<Tuple<Level, SlotAccess>> heldItemAction) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.entityAction = entityAction;
		this.placeToAction = placeToAction;
		this.placeOnAction = placeOnAction;
		this.itemCondition = itemCondition;
		this.placeToCondition = placeToCondition;
		this.placeOnCondition = placeOnCondition;
		this.directions = directions;
		this.hands = hands;
		this.resultStack = resultStack;
		this.resultItemAction = resultItemAction;
		this.heldItemAction = heldItemAction;
	}

	public static @NotNull PowerTypeFactory getFactory() {
		return new PowerTypeFactory(OriginsPaper.apoliIdentifier("action_on_block_place"), PowerType.getFactory().getSerializableData()
			.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
			.add("place_to_action", ApoliDataTypes.BLOCK_ACTION, null)
			.add("place_on_action", ApoliDataTypes.BLOCK_ACTION, null)
			.add("item_condition", ApoliDataTypes.ITEM_CONDITION, null)
			.add("place_to_condition", ApoliDataTypes.BLOCK_CONDITION, null)
			.add("place_on_condition", ApoliDataTypes.BLOCK_CONDITION, null)
			.add("directions", SerializableDataTypes.DIRECTION_SET, EnumSet.allOf(Direction.class))
			.add("hands", SerializableDataTypes.enumSet(InteractionHand.class, SerializableDataTypes.HAND), EnumSet.allOf(InteractionHand.class))
			.add("result_stack", SerializableDataTypes.ITEM_STACK, null)
			.add("result_item_action", ApoliDataTypes.ITEM_ACTION, null)
			.add("held_item_action", ApoliDataTypes.ITEM_ACTION, null));
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

	public boolean shouldExecute(ItemStack heldStack, InteractionHand hand, BlockPos toPos, BlockPos onPos, Direction direction, ServerLevel level) {
		return (shouldExecute$apoli$super(hand, heldStack, level) && directions.contains(direction))
			&& ((placeOnCondition == null || placeOnCondition.test(new BlockInWorld(level, onPos, true)))
			&& (placeToCondition == null || placeToCondition.test(new BlockInWorld(level, toPos, true))));
	}

	public void executeOtherActions(BlockPos toPos, BlockPos onPos, Direction direction, Entity entity) {

		if (placeOnAction != null) {
			placeOnAction.accept(Triple.of(entity.level(), onPos, direction));
		}

		if (placeToAction != null) {
			placeToAction.accept(Triple.of(entity.level(), toPos, direction));
		}

		if (entityAction != null) {
			entityAction.accept(entity);
		}

	}

	public void executeItemActions(InteractionHand hand, Entity entity) {

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

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlace(@NotNull BlockPlaceEvent e) {
		Player player = ((CraftPlayer) e.getPlayer()).getHandle();
		if (!getPlayers().contains(player) || !isActive(player)) return;

		PlaceData data = new PlaceData(e);
		if (shouldExecute(e.getItemInHand() != null ? CraftItemStack.unwrap(e.getItemInHand()) : ItemStack.EMPTY, data.hand, data.pos, data.onPos, data.direction, (ServerLevel) player.level())) {
			executeOtherActions(data.pos, data.onPos, data.direction, player);
			new BukkitRunnable() {
				@Override
				public void run() {
					executeItemActions(data.hand, player);
				}
			}.runTaskLater(OriginsPaper.getPlugin(), 1);
		}
	}

	private class PlaceData {
		private final Direction direction;
		private final BlockPos pos;
		private final BlockPos onPos;
		private final InteractionHand hand;

		public PlaceData(@NotNull BlockPlaceEvent e) {
			this.direction = CraftBlock.blockFaceToNotch(e.getBlockAgainst().getFace(e.getBlockPlaced()));
			this.pos = CraftLocation.toBlockPosition(e.getBlockPlaced().getLocation());
			this.onPos = CraftLocation.toBlockPosition(e.getBlockAgainst().getLocation());
			this.hand = CraftEquipmentSlot.getHand(e.getHand());
		}
	}
}
