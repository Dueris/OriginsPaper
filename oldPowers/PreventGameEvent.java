package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.GenericGameEvent;

import java.util.ArrayList;

import static me.dueris.genesismc.factory.powers.apoli.superclass.PreventSuperClass.prevent_game_event;

public class PreventGameEvent extends CraftPower implements Listener {

	@EventHandler
	public void event(GenericGameEvent e) {
		if (e.getEntity() == null) return;
		if (e.getEntity() instanceof Player p) {
			if (!this.getPlayersWithPower().contains(p)) return;
			for (Power power : OriginPlayerAccessor.getPowers(p, getType())) {
				if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) {
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

	@Override
	public String getType() {
		return "apoli:prevent_game_event";
	}

	@Override
	public ArrayList<Player> getPlayersWithPower() {
		return prevent_game_event;
	}

}
