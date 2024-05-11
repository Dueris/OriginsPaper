package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class ActionWhenHit extends CraftPower implements Listener {

	@EventHandler
	public void h(EntityDamageByEntityEvent e) {
		Entity actor = e.getEntity();
		Entity target = e.getDamager();

		if (!(actor instanceof Player player)) return;
		if (!getPlayersWithPower().contains(actor)) return;

		for (Power power : OriginPlayerAccessor.getPowers(player, getType())) {
			if (power == null) continue;
			if (!ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) actor) || !ConditionExecutor.testBiEntity(power.getJsonObject("bientity_condition"), (CraftEntity) actor, (CraftEntity) target))
				return;
			setActive(player, power.getTag(), true);
			Actions.executeBiEntity(actor, target, power.getJsonObject("bientity_action"));
			new BukkitRunnable() {
				@Override
				public void run() {
					if (!getPlayersWithPower().contains(target)) return;
					setActive(player, power.getTag(), false);
				}
			}.runTaskLater(GenesisMC.getPlugin(), 2L);
		}
	}

	@Override
	public String getType() {
		return "apoli:action_when_hit";
	}

	@Override
	public ArrayList<Player> getPlayersWithPower() {
		return action_when_hit;
	}

}
