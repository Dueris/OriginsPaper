package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public class ActionWhenHit extends CraftPower implements Listener {

	@Override
	public void run(Player p) {

	}

	@EventHandler
	public void h(EntityDamageByEntityEvent e) {
		if (e.getDamage() == 0 || e.isCancelled()) return;
		Entity actor = e.getEntity();
		Entity target = e.getDamager();

		if (!(target instanceof Player player)) return;
		if (!getPowerArray().contains(target)) return;

		for (Layer layer : CraftApoli.getLayersFromRegistry()) {
			for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(player, getPowerFile(), layer)) {
				if (power == null) continue;
				if (!ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) actor)) return;
				if (!getPowerArray().contains(target)) return;
				setActive(player, power.getTag(), true);
				Actions.BiEntityActionType(actor, target, power.getBiEntityAction());
				new BukkitRunnable() {
					@Override
					public void run() {
						if (!getPowerArray().contains(target)) return;
						setActive(player, power.getTag(), false);
					}
				}.runTaskLater(GenesisMC.getPlugin(), 2L);
			}
		}
	}

	@Override
	public String getPowerFile() {
		return "apoli:action_when_hit";
	}

	@Override
	public ArrayList<Player> getPowerArray() {
		return action_when_hit;
	}

	@Override
	public void setActive(Player p, String tag, Boolean bool) {
		if (powers_active.containsKey(p)) {
			if (powers_active.get(p).containsKey(tag)) {
				powers_active.get(p).replace(tag, bool);
			} else {
				powers_active.get(p).put(tag, bool);
			}
		} else {
			powers_active.put(p, new HashMap());
			setActive(p, tag, bool);
		}
	}
}
