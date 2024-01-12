package me.dueris.genesismc.factory.powers.world;

import com.google.common.base.Preconditions;
import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.events.AddToSetEvent;
import me.dueris.genesismc.events.OriginChangeEvent;
import me.dueris.genesismc.events.RemoveFromSetEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.LayerContainer;
import me.dueris.genesismc.utils.PowerContainer;
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
            for (LayerContainer layer : CraftApoli.getLayers()) {
                for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(e.getPlayer(), getPowerFile(), layer)) {
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
                    for (LayerContainer layer : CraftApoli.getLayers()) {
                        for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                            if (power.get("action_on_add") == null) return;
                            if (power.getTag() == e.getTag()) {
                                Actions.biEntityActionType(p, e.getEntity(), power.getAction("action_on_add"));
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
                    for (LayerContainer layer : CraftApoli.getLayers()) {
                        for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                            if (power.get("action_on_add") == null) return;
                            if (power.getTag() == e.getTag()) {
                                Actions.biEntityActionType(p, e.getEntity(), power.getAction("action_on_remove"));
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
            for (LayerContainer layer : CraftApoli.getLayers()) {
                for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(e.getPlayer(), getPowerFile(), layer)) {
                    addToEntitySet(e.getPlayer(), power.getTag());
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:entity_set";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return entity_set;
    }

    @Override
    public void setActive(Player p, String tag, Boolean bool) {
        if (powers_active.containsKey(p)) {
            if (powers_active.get(p).containsKey(tag)) {
                powers_active.get(p).replace(tag, bool);
            } else {
                powers_active.get(p).put(tag, bool);
            }
        } else {
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
        }
    }
}
