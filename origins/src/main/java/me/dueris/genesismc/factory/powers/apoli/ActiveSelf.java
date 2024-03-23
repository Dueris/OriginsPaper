package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.event.KeybindTriggerEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.CooldownUtils;
import me.dueris.genesismc.util.KeybindingUtils;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class ActiveSelf extends CraftPower implements Listener {

	@Override
	public void run(Player p) {

	}

	@EventHandler
	public void k(KeybindTriggerEvent e) {
		for (Layer layer : CraftApoli.getLayersFromRegistry()) {
			if (getPowerArray().contains(e.getPlayer())) {
				ConditionExecutor executor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
				for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(e.getPlayer(), getPowerFile(), layer)) {
					if (CooldownUtils.isPlayerInCooldownFromTag(e.getPlayer(), Utils.getNameOrTag(power))) continue;
					if (ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) e.getPlayer())) {
						setActive(e.getPlayer(), power.getTag(), true);
						if (KeybindingUtils.isKeyActive(power.get("key").getOrDefault("key", "key.origins.primary_active").toString(), e.getPlayer())) {
							Actions.EntityActionType(e.getPlayer(), power.getEntityAction());
							if (power.getObjectOrDefault("cooldown", 1) != null) {
								CooldownUtils.addCooldown(e.getPlayer(), Utils.getNameOrTag(power), power.getType(), power.getIntOrDefault("cooldown", power.getIntOrDefault("max", 1)), power.get("hud_render"));
							}
						}
					} else {
						setActive(e.getPlayer(), power.getTag(), false);
					}

				}

			}
		}
	}

	@Override
	public String getPowerFile() {
		return "apoli:active_self";
	}

	@Override
	public ArrayList<Player> getPowerArray() {
		return active_self;
	}

}
