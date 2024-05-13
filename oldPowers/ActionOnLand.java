package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.GameEvent;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.GenericGameEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class ActionOnLand extends CraftPower implements Listener {

	@EventHandler
	public void e(GenericGameEvent e) {
		if (e.getEvent() != GameEvent.HIT_GROUND) return;
		if (!(e.getEntity() instanceof Player player)) return;
		if (!getPlayersWithPower().contains(player)) return;
		for (Power power : OriginPlayerAccessor.getPowers(player, getType())) {
			if (!ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) player)) return;
			setActive(player, power.getTag(), true);
			Actions.executeEntity(player, power.getJsonObject("entity_action"));
			new BukkitRunnable() {
				@Override
				public void run() {
					setActive(player, power.getTag(), false);
				}
			}.runTaskLater(GenesisMC.getPlugin(), 2L);
		}
	}

	@Override
	public String getType() {
		return "apoli:action_on_land";
	}

	@Override
	public ArrayList<Player> getPlayersWithPower() {
		return action_on_land;
	}

}