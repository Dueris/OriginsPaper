package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.event.PowerUpdateEvent;
import me.dueris.genesismc.factory.data.types.EntityGroup;
import me.dueris.genesismc.factory.powers.CraftPower;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;

public class EntityGroupManager extends CraftPower implements Listener {
    public static HashMap<Entity, EntityGroup> modifiedEntityGroups = new HashMap<>();

    @EventHandler
    public void modifyFromPower(PowerUpdateEvent e) {
	if (e.getPower().getType().equalsIgnoreCase(getType())) {
	    if (e.isRemoved() && modifiedEntityGroups.containsKey(e.getPlayer())) {
		modifiedEntityGroups.remove(e.getPlayer());
	    } else {
		modifiedEntityGroups.put(e.getPlayer(), e.getPower().getEnumValue("group", EntityGroup.class));
	    }
	}
    }

    @Override
    public String getType() {
	return "apoli:entity_group";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
	return entity_group;
    }

}
