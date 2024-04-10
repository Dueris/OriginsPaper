package me.dueris.genesismc.factory.powers.apoli;

import com.google.common.base.Preconditions;
import me.dueris.genesismc.event.AddToSetEvent;
import me.dueris.genesismc.event.OriginChangeEvent;
import me.dueris.genesismc.event.RemoveFromSetEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class EntitySetPower extends CraftPower implements Listener {
    // Map<KEY, ARRAY_OF_ENTITIES>
    public static HashMap<String, ArrayList<Entity>> entity_sets = new HashMap<>();

    public static void addToEntitySet(Entity p, String tag) {
        Preconditions.checkArgument(p != null, "Entity must not be null");
        Preconditions.checkArgument(tag != null, "EntitySetTag must not be null");
        if (entity_sets.containsKey(tag)) {
            entity_sets.get(tag).add(p);
        } else {
            entity_sets.put(tag, new ArrayList<>());
            addToEntitySet(p, tag);
        }
    }

    public static void removeFromEntitySet(Entity p, String tag) {
        Preconditions.checkArgument(p != null, "Entity must not be null");
        Preconditions.checkArgument(tag != null, "EntitySetTag must not be null");
        if (entity_sets.containsKey(tag)) {
            if (entity_sets.get(tag).isEmpty()) return;
            entity_sets.get(tag).removeIf(entity -> entity == p);
        } else {
            entity_sets.put(tag, new ArrayList<>());
            removeFromEntitySet(p, tag);
        }
    }

    public static boolean isInEntitySet(Entity p, String tag) {
        Preconditions.checkArgument(p != null, "Entity must not be null");
        Preconditions.checkArgument(tag != null, "EntitySetTag must not be null");
        if (entity_sets.containsKey(tag)) {
            return entity_sets.get(tag).contains(p);
        } else {
            entity_sets.put(tag, new ArrayList<>());
            return isInEntitySet(p, tag);
        }
    }

    public static boolean isInEntitySet(Entity p) {
        Preconditions.checkArgument(p != null, "Entity must not be null");
        boolean isIn = false;
        for (String tag : entity_sets.keySet()) {
            if (isIn) break;
            if (entity_sets.containsKey(tag)) {
                isIn = entity_sets.get(tag).contains(p);
            } else {
                entity_sets.put(tag, new ArrayList<>());
                isIn = isInEntitySet(p, tag);
            }
        }
        return isIn;
    }

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        if (entity_set.contains(e.getPlayer())) {
            for (String tag : entity_sets.keySet()) {
                if (entity_sets.containsKey(tag)) {
                    entity_sets.get(tag).removeIf(entity -> entity == e.getPlayer());
                }
            }
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(e.getPlayer(), getPowerFile(), layer)) {
                    addToEntitySet(e.getPlayer(), power.getTag());
                }
            }
        }
    }

    @EventHandler
    public void addEvent(AddToSetEvent e) {
        addToEntitySet(e.getEntity(), e.getTag());
        for (Entity entity : entity_sets.get(e.getTag())) {
            if (entity instanceof Player p) {
                if (entity_set.contains(p)) {
                    for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                        for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                            if (!ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) p)) return;
                            if (power.get("action_on_add") == null) return;
                            if (power.getTag() == e.getTag()) {
                                Actions.executeBiEntity(p, e.getEntity(), power.get("action_on_add"));
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void removeEvent(RemoveFromSetEvent e) {
        removeFromEntitySet(e.getEntity(), e.getTag());
        for (Entity entity : entity_sets.get(e.getTag())) {
            if (entity instanceof Player p) {
                if (entity_set.contains(p)) {
                    for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                        for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                            if (!ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) p)) return;
                            if (power.get("action_on_add") == null) return;
                            if (power.getTag() == e.getTag()) {
                                Actions.executeBiEntity(p, e.getEntity(), power.get("action_on_remove"));
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void changeOrigin(OriginChangeEvent e) {
        if (entity_set.contains(e.getPlayer())) {
            for (String tag : entity_sets.keySet()) {
                if (entity_sets.containsKey(tag)) {
                    entity_sets.get(tag).removeIf(entity -> entity == e.getPlayer());
                }
            }
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(e.getPlayer(), getPowerFile(), layer)) {
                    if (!ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) e.getPlayer())) return;
                    addToEntitySet(e.getPlayer(), power.getTag());
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:entity_set";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return entity_set;
    }

}
