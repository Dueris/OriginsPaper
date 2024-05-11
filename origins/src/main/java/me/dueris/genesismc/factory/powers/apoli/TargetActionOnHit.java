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

public class TargetActionOnHit extends CraftPower implements Listener {

	@EventHandler
	public void s(EntityDamageByEntityEvent e) {
		Entity actor = e.getDamager();
		Entity target = e.getEntity();

		if (!(actor instanceof Player player)) return;
		if (!getPlayersWithPower().contains(actor)) return;

		for (Power power : OriginPlayerAccessor.getPowers(player, getType())) {
			if (Cooldown.isInCooldown(player, power)) continue;
			new BukkitRunnable() {
				@Override
				public void run() {
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
			}.runTaskLater(GenesisMC.getPlugin(), 1);
		}
	}

	@Override
	public String getType() {
		return "apoli:target_action_on_hit";
	}

	@Override
	public ArrayList<Player> getPlayersWithPower() {
		return target_action_on_hit;
	}

}
