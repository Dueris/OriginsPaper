package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.HashMap;

import static me.dueris.genesismc.factory.powers.apoli.superclass.PreventSuperClass.prevent_death;

public class PreventDeath extends CraftPower implements Listener {

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


	@EventHandler
	public void run(PlayerDeathEvent e) {
		if (prevent_death.contains(e.getPlayer())) {
			for (Layer layer : CraftApoli.getLayersFromRegistry()) {
				ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
				for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(e.getPlayer(), getPowerFile(), layer)) {
					if (ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) e.getEntity()) && ConditionExecutor.testDamage(power.get("damage_condition"), e.getEntity().getLastDamageCause())) {
						e.setCancelled(true);
						if (!getPowerArray().contains(e.getPlayer())) return;
						setActive(e.getPlayer(), power.getTag(), true);
					} else {
						if (!getPowerArray().contains(e.getPlayer())) return;
						setActive(e.getPlayer(), power.getTag(), false);
					}
				}
			}
		}
	}

	@Override
	public void run(Player p) {

	}

	@Override
	public String getPowerFile() {
		return "apoli:prevent_death";
	}

	@Override
	public ArrayList<Player> getPowerArray() {
		return prevent_death;
	}
}
