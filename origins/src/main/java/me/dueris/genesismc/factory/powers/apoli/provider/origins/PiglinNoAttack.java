package me.dueris.genesismc.factory.powers.apoli.provider.origins;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.apoli.provider.PowerProvider;
import me.dueris.genesismc.registry.registries.Power;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class PiglinNoAttack extends CraftPower implements Listener, PowerProvider {
    public static ArrayList<Player> piglinPlayers = new ArrayList<>();
    protected static NamespacedKey powerReference = GenesisMC.originIdentifier("piglin_brothers");
    static ArrayList<EntityType> piglinValid = new ArrayList<>();

    static {
        piglinValid.add(EntityType.PIGLIN);
        piglinValid.add(EntityType.PIGLIN_BRUTE);
        piglinValid.add(EntityType.ZOMBIFIED_PIGLIN);
    }

    private final HashMap<Player, HashMap<Entity, Integer>> cooldowns = new HashMap<>();

    @Override
    public void run(Player p, Power power) {
        if (cooldowns.containsKey(p)) {
            for (Entity en : cooldowns.get(p).keySet()) {
                if (cooldowns.get(p).get(en) <= 1) {
                    cooldowns.get(p).remove(en);
                } else {
                    HashMap<Entity, Integer> map = new HashMap<>();
                    map.put(en, cooldowns.get(p).get(en) - 1);
                    cooldowns.put(p, map);
                }
            }
        }
    }

    @EventHandler
    public void target(EntityTargetEvent e) {
        if (piglinValid.contains(e.getEntity().getType())) {
            if (piglinPlayers.contains(e.getTarget())) {
                if (!cooldowns.containsKey(e.getTarget())) {
                    cooldowns.put((Player) e.getTarget(), new HashMap<>());
                }
                if (!cooldowns.get((Player) e.getTarget()).containsKey(e.getEntity())) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void damageEntity(EntityDamageByEntityEvent e) {
        if (piglinValid.contains(e.getEntity().getType())) {
            if (piglinPlayers.contains(e.getDamager())) {
                Player p = (Player) e.getDamager();
                HashMap<Entity, Integer> map = new HashMap<>();
                map.put(e.getEntity(), 600);
                cooldowns.put(p, map);
            }
        }
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
        return piglinPlayers;
    }

}
