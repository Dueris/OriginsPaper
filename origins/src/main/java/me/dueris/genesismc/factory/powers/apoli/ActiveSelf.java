package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.event.KeybindTriggerEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.KeybindingUtils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class ActiveSelf extends CraftPower implements Listener {

	@EventHandler
	public void k(KeybindTriggerEvent e) {
		for (Layer layer : CraftApoli.getLayersFromRegistry()) {
			if (getPlayersWithPower().contains(e.getPlayer())) {
				for (Power power : OriginPlayerAccessor.getPowers(e.getPlayer(), getType(), layer)) {
					if (Cooldown.isInCooldown(e.getPlayer(), power)) continue;
					if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) e.getPlayer())) {
						setActive(e.getPlayer(), power.getTag(), true);
						if (KeybindingUtils.isKeyActive(power.getJsonObject("key").getStringOrDefault("key", "key.origins.primary_active"), e.getPlayer())) {
							Actions.executeEntity(e.getPlayer(), power.getJsonObject("entity_action"));
							if (power.isPresent("cooldown")) {
								Cooldown.addCooldown(e.getPlayer(), power.getNumber("cooldown").getInt(), power);
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
	public String getType() {
		return "apoli:active_self";
	}

	@Override
	public ArrayList<Player> getPlayersWithPower() {
		return active_self;
	}

}
