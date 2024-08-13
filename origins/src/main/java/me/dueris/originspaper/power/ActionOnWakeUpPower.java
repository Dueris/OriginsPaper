package me.dueris.originspaper.power;

import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.action.ActionFactory;
import me.dueris.originspaper.condition.ConditionFactory;
import me.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ActionOnWakeUpPower extends PowerType {
	private final ActionFactory<Entity> entityAction;
	private final ActionFactory<Triple<Level, BlockPos, Direction>> blockAction;
	private final ConditionFactory<BlockInWorld> blockCondition;
	private final List<org.bukkit.entity.Player> tickedAlready = new ArrayList<>();

	public ActionOnWakeUpPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority,
							   ActionFactory<Entity> entityAction, ActionFactory<Triple<Level, BlockPos, Direction>> blockAction, ConditionFactory<BlockInWorld> blockCondition) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.entityAction = entityAction;
		this.blockAction = blockAction;
		this.blockCondition = blockCondition;
	}

	public static InstanceDefiner buildDefiner() {
		return PowerType.buildDefiner().typedRegistry(OriginsPaper.apoliIdentifier("action_on_wake_up"))
			.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
			.add("block_action", ApoliDataTypes.BLOCK_ACTION, null)
			.add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null);
	}

	public boolean doesApply(BlockPos pos, @NotNull Entity entity) {
		BlockInWorld cbp = new BlockInWorld(entity.level(), pos, true);
		return doesApply(cbp);
	}

	public boolean doesApply(BlockInWorld pos) {
		return blockCondition == null || blockCondition.test(pos);
	}

	public void executeActions(BlockPos pos, Direction dir, Entity entity) {
		if (blockAction != null) {
			blockAction.accept(Triple.of(entity.level(), pos, dir));
		}
		if (entityAction != null) {
			entityAction.accept(entity);
		}
	}

	@EventHandler
	public void onWakeUp(PlayerBedLeaveEvent e) {
		new BukkitRunnable() {
			@Override
			public void run() {
				Player player = ((CraftPlayer) e.getPlayer()).getHandle();
				if (e.getPlayer().getWorld().isDayTime() && getPlayers().contains(player)) {
					if (tickedAlready.contains(e.getPlayer())) return;
					tickedAlready.add(e.getPlayer());
					BlockPos pos = CraftLocation.toBlockPosition(e.getBed().getLocation());
					if (doesApply(pos, player)) {
						executeActions(pos, Direction.DOWN, player);
					}
					new BukkitRunnable() {
						@Override
						public void run() {
							tickedAlready.remove(player.getBukkitEntity());
						}
					}.runTaskLater(OriginsPaper.getPlugin(), 1);
				}
			}
		}
			// We delay this by 3 ticks to give time for the world to become marked as "daytime", because it doesnt when the event is called...
			.runTaskLater(OriginsPaper.getPlugin(), 3);
	}
}
