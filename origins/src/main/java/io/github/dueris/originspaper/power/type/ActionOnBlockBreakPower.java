package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.power.factory.PowerTypeFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class ActionOnBlockBreakPower extends PowerType {
	private final ActionTypeFactory<Entity> entityAction;
	private final ActionTypeFactory<Triple<Level, BlockPos, Direction>> blockAction;
	private final ConditionTypeFactory<BlockInWorld> blockCondition;
	private final boolean onlyWhenHarvested;

	public ActionOnBlockBreakPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
								   ActionTypeFactory<Entity> entityAction, ActionTypeFactory<Triple<Level, BlockPos, Direction>> blockAction, ConditionTypeFactory<BlockInWorld> blockCondition, boolean onlyWhenHarvested) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.entityAction = entityAction;
		this.blockAction = blockAction;
		this.blockCondition = blockCondition;
		this.onlyWhenHarvested = onlyWhenHarvested;
	}

	public static @NotNull PowerTypeFactory getFactory() {
		return new PowerTypeFactory(OriginsPaper.apoliIdentifier("action_on_block_break"), PowerType.getFactory().getSerializableData()
			.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
			.add("block_action", ApoliDataTypes.BLOCK_ACTION, null)
			.add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null)
			.add("only_when_harvested", SerializableDataTypes.BOOLEAN, false));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBreak(@NotNull BlockBreakEvent e) {
		Player player = ((CraftPlayer) e.getPlayer()).getHandle();
		if (!getPlayers().contains(player) || !isActive(player)) return;
		boolean harvestedSuccessfully = player.hasCorrectToolForDrops(((CraftBlock) e.getBlock()).getNMS());
		BlockPos pos = CraftLocation.toBlockPosition(e.getBlock().getLocation());

		Vector blockCenter = e.getBlock().getLocation().toVector().add(new Vector(0.5, 0.5, 0.5));
		Vector playerToBlock = blockCenter.subtract(player.getBukkitEntity().getEyeLocation().toVector()).normalize();

		BlockFace blockFace = null;

		double maxDot = -1.0;
		for (BlockFace face : BlockFace.values()) {
			Vector faceVector = new Vector(face.getModX(), face.getModY(), face.getModZ()).normalize();
			double dot = playerToBlock.dot(faceVector);

			if (dot > maxDot) {
				maxDot = dot;
				blockFace = face;
			}
		}

		if (doesApply(new BlockInWorld(player.level(), pos, true))) {
			executeActions(harvestedSuccessfully, pos, CraftBlock.blockFaceToNotch(blockFace), player);
		}
	}

	public boolean doesApply(BlockInWorld pos) {
		return blockCondition == null || blockCondition.test(pos);
	}

	public void executeActions(boolean successfulHarvest, BlockPos pos, Direction direction, Entity entity) {

		if (!successfulHarvest && onlyWhenHarvested) {
			return;
		}

		if (blockAction != null) {
			blockAction.accept(Triple.of(entity.level(), pos, direction));
		}

		if (entityAction != null) {
			entityAction.accept(entity);
		}

	}
}
