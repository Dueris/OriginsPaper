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
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class FallImmunity extends CraftPower implements Listener {


	@Override
	public void run(Player p) {

	}

	@EventHandler
	public void acrobatics(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player p)) return;
		if (fall_immunity.contains(p)) {
			for (Layer layer : CraftApoli.getLayersFromRegistry()) {
				ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
				for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
					if (ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) p)) {
						setActive(p, power.getTag(), true);
						if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
							e.setCancelled(true);
						}
					} else {
						setActive(p, power.getTag(), false);
					}
				}
			}
		}
	}

	@Override
	public String getPowerFile() {
		return "apoli:fall_immunity";
	}

	@Override
	public ArrayList<Player> getPowerArray() {
		return fall_immunity;
	}
}
