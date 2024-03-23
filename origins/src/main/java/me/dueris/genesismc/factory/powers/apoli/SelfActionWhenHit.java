package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.CooldownUtils;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;

public class SelfActionWhenHit extends CraftPower implements Listener {

	@Override
	public void run(Player p) {

	}

	@EventHandler
	public void s(EntityDamageByEntityEvent e) {
		Entity actor = e.getEntity();
		Entity target = e.getDamager();

		if (!(actor instanceof Player player)) return;
		if (!getPowerArray().contains(player)) return;

		for (Layer layer : CraftApoli.getLayersFromRegistry()) {
			for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(player, getPowerFile(), layer)) {
				if (CooldownUtils.isPlayerInCooldownFromTag(player, Utils.getNameOrTag(power))) continue;
				if (ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) player)) {
					setActive(player, power.getTag(), true);
					Actions.EntityActionType(player, power.getEntityAction());
					if (power.getObjectOrDefault("cooldown", 1) != null) {
						CooldownUtils.addCooldown(player, Utils.getNameOrTag(power), power.getType(), power.getIntOrDefault("cooldown", power.getIntOrDefault("max", 1)), power.get("hud_render"));
					}
				} else {
					setActive(player, power.getTag(), false);
				}
			}
		}
	}

	@Override
	public String getPowerFile() {
		return "apoli:self_action_when_hit";
	}

	@Override
	public ArrayList<Player> getPowerArray() {
		return self_action_when_hit;
	}

}
