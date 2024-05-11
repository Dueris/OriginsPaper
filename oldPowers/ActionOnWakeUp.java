package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class ActionOnWakeUp extends CraftPower implements Listener {

	@EventHandler
	public void w(PlayerBedLeaveEvent e) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (!getPlayersWithPower().contains(e.getPlayer()) || !e.getPlayer().getWorld().isDayTime()) return;
				for (Power power : OriginPlayerAccessor.getPowers(e.getPlayer(), getType())) {
					if (!ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) e.getPlayer()))
						return;
					setActive(e.getPlayer(), power.getTag(), true);
					Actions.executeEntity(e.getPlayer(), power.getJsonObject("entity_action"));
					Actions.executeBlock(e.getBed().getLocation(), power.getJsonObject("block_action"));
				}
			}
		}.runTaskLater(GenesisMC.getPlugin(), 2);
	}

	@Override
	public String getType() {
		return "apoli:action_on_wake_up";
	}

	@Override
	public ArrayList<Player> getPlayersWithPower() {
		return action_on_wake_up;
	}

}
