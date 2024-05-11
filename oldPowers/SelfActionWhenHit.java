package me.dueris.genesismc.factory.powers.apoli;

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

import java.util.ArrayList;

public class SelfActionWhenHit extends CraftPower implements Listener {

	@EventHandler
	public void s(EntityDamageByEntityEvent e) {
		Entity actor = e.getEntity();

		if (!(actor instanceof Player player)) return;
		if (!getPlayersWithPower().contains(player)) return;

		for (Power power : OriginPlayerAccessor.getPowers(player, getType())) {
			if (Cooldown.isInCooldown(player, power)) continue;
			if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) player)) {
				setActive(player, power.getTag(), true);
				Actions.executeEntity(player, power.getJsonObject("entity_action"));
				if (power.isPresent("cooldown")) {
					Cooldown.addCooldown(player, power.getNumber("cooldown").getInt(), power);
				}
			} else {
				setActive(player, power.getTag(), false);
			}
		}
	}

	@Override
	public String getType() {
		return "apoli:self_action_when_hit";
	}

	@Override
	public ArrayList<Player> getPlayersWithPower() {
		return self_action_when_hit;
	}

}
