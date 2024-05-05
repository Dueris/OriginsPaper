package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.ArrayList;

public class SelfActionOnKill extends CraftPower implements Listener {

	@EventHandler
	public void k(EntityDeathEvent e) {
		Entity target = e.getEntity();

		if (!(target instanceof Player player)) return;
		if (!getPlayersWithPower().contains(target)) return;

		for (Layer layer : CraftApoli.getLayersFromRegistry()) {
			for (Power power : OriginPlayerAccessor.getPowers(player, getType(), layer)) {
				if (Cooldown.isInCooldown(player, power)) continue;
				if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) player)) {
					setActive(player, power.getTag(), true);
					Actions.executeEntity(target, power.getJsonObject("entity_action"));
					if (power.isPresent("cooldown")) {
						Cooldown.addCooldown(player, power.getNumber("cooldown").getInt(), power);
					}
				} else {
					setActive(player, power.getTag(), false);
				}
			}
		}
	}

	@Override
	public String getType() {
		return "apoli:self_action_on_kill";
	}

	@Override
	public ArrayList<Player> getPlayersWithPower() {
		return self_action_on_kill;
	}

}
