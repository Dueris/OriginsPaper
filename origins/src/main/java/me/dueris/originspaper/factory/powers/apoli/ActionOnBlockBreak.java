package me.dueris.originspaper.factory.powers.apoli;

import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.actions.Actions;
import me.dueris.originspaper.factory.conditions.ConditionExecutor;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import net.minecraft.core.BlockPos;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class ActionOnBlockBreak extends PowerType {
	public static HashMap<Player, Boolean> playersMining = new HashMap<>();
	public static HashMap<Player, BlockPos> playersMiningBlockPos = new HashMap<>();
	private final FactoryJsonObject entityAction;
	private final FactoryJsonObject blockAction;
	private final FactoryJsonObject blockCondition;
	private final boolean onlyWhenHarvested;

	public ActionOnBlockBreak(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject entityAction, FactoryJsonObject blockAction, FactoryJsonObject blockCondition, boolean onlyWhenHarvested) {
		super(name, description, hidden, condition, loading_priority);
		this.entityAction = entityAction;
		this.blockAction = blockAction;
		this.blockCondition = blockCondition;
		this.onlyWhenHarvested = onlyWhenHarvested;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("action_on_block_break"))
			.add("entity_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("block_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("block_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("only_when_harvested", boolean.class, true);
	}

	@EventHandler
	public void brek(@NotNull BlockBreakEvent e) {
		Player actor = e.getPlayer();

		if (!getPlayers().contains(actor)) return;

		if (!ConditionExecutor.testBlock(blockCondition, (CraftBlock) e.getBlock())) return;
		boolean pass = true;
		if (isOnlyWhenHarvested()) {
			pass = ((CraftPlayer) actor).getHandle().hasCorrectToolForDrops(((CraftBlock) e.getBlock()).getNMS());
		}

		if (pass) {
			new BukkitRunnable() {
				@Override
				public void run() {
					Actions.executeBlock(e.getBlock().getLocation(), blockAction);
					Actions.executeEntity(e.getPlayer(), entityAction);
				}
			}.runTaskLater(OriginsPaper.getPlugin(), 1);
		}
	}

	@EventHandler
	public void breakTick(@NotNull BlockDamageEvent e) {
		if (!e.isCancelled()) {
			playersMining.put(e.getPlayer(), true);
			new BukkitRunnable() {
				@Override
				public void run() {
					playersMining.put(e.getPlayer(), false);
				}
			}.runTaskLater(OriginsPaper.getPlugin(), 1);
			playersMiningBlockPos.put(e.getPlayer(), new BlockPos(e.getBlock().getLocation().getBlockX(), e.getBlock().getLocation().getBlockY(), e.getBlock().getLocation().getBlockZ()));
		}
	}

	public boolean isOnlyWhenHarvested() {
		return onlyWhenHarvested;
	}
}
