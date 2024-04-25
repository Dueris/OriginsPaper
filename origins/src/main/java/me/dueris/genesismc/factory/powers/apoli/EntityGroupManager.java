package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.PowerUpdateEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.data.types.EntityGroup;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
