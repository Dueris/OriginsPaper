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
import org.bukkit.event.world.GenericGameEvent;

import java.util.ArrayList;

import static me.dueris.genesismc.factory.powers.apoli.superclass.PreventSuperClass.prevent_game_event;

public class PreventGameEvent extends CraftPower implements Listener {
	@Override
	public void run(Player p) {

	}

	@EventHandler
	public void event(GenericGameEvent e) {
		if (e.getEntity() == null) return;
		if (e.getEntity() instanceof Player p) {
			if (!this.getPowerArray().contains(p)) return;
			for (Layer layer : CraftApoli.getLayersFromRegistry()) {
				for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
					if (ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) p)) {
						String event = power.getString("event");
						if (event.contains(":")) {
							event = event.split(":")[1];
						}
						if (e.getEvent().key().asString().equals(event)) {
							e.setCancelled(true);
						}
					}
				}
			}
		}
	}

	@Override
	public String getPowerFile() {
		return "apoli:prevent_game_event";
	}

	@Override
	public ArrayList<Player> getPowerArray() {
		return prevent_game_event;
	}

}
