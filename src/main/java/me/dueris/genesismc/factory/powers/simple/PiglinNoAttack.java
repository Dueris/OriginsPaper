package me.dueris.genesismc.factory.powers.simple;

import it.unimi.dsi.fastutil.Hash;
import me.dueris.genesismc.FoliaOriginScheduler;
import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.events.OriginChangeEvent;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class PiglinNoAttack extends CraftPower implements OriginSimple, Listener  {
    public static ArrayList<Player> piglinPlayers = new ArrayList<>();
    static ArrayList<EntityType> piglinValid = new ArrayList<>();
    private HashMap<Player, HashMap<Entity, Integer>> cooldowns = new HashMap<>();
    static {
        piglinValid.add(EntityType.PIGLIN);
        piglinValid.add(EntityType.PIGLIN_BRUTE);
        piglinValid.add(EntityType.ZOMBIFIED_PIGLIN);
    }

    @Override
    public void run(Player p) {
        if(cooldowns.containsKey(p)){
            for(Entity en : cooldowns.get(p).keySet()){
                if(cooldowns.get(p).get(en) <= 1){
                    cooldowns.get(p).remove(en);
                }else{
                    HashMap<Entity, Integer> map = new HashMap<>();
                    map.put(en,  cooldowns.get(p).get(en) - 1);
                    cooldowns.put(p, map);
                }
            }
        }
    }

    @EventHandler
    public void target(EntityTargetEvent e){
        if(piglinValid.contains(e.getEntity().getType())){
            if(piglinPlayers.contains(e.getTarget())){
                if(!cooldowns.containsKey(e.getTarget())){
                    cooldowns.put((Player) e.getTarget(), new HashMap<>());
                }
                if(!cooldowns.get((Player) e.getTarget()).containsKey(e.getEntity())){
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void damageEntity(EntityDamageByEntityEvent e){
        if(piglinValid.contains(e.getEntity().getType())){
            if(piglinPlayers.contains(e.getDamager())){
                Player p = (Player) e.getDamager();
                HashMap<Entity, Integer> map = new HashMap<>();
                map.put(e.getEntity(), 600);
                cooldowns.put(p, map);
            }
        }
    }

    @EventHandler
    public void event(OriginChangeEvent e) {
        boolean hasMimicWardenPower = false;
        if(OriginPlayerUtils.powerContainer.get(e.getPlayer()) == null) return;

        for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
            for (PowerContainer power : OriginPlayerUtils.powerContainer.get(e.getPlayer()).get(layer)) {
                if (power.getTag().equals("origins:piglin_brothers")) {
                    hasMimicWardenPower = true;
                    break;
                }
            }
        }

        if (hasMimicWardenPower && !piglinPlayers.contains(e.getPlayer())) {
            piglinPlayers.add(e.getPlayer());
        } else if (!hasMimicWardenPower) {
            piglinPlayers.remove(e.getPlayer());
        }
    }

    @EventHandler
    public void event(PlayerJoinEvent e) {
        boolean hasMimicWardenPower = false;
        if(OriginPlayerUtils.powerContainer.get(e.getPlayer()) == null) return;

        for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
            for (PowerContainer power : OriginPlayerUtils.powerContainer.get(e.getPlayer()).get(layer)) {
                if (power.getTag().equals("origins:piglin_brothers")) {
                    hasMimicWardenPower = true;
                    break;
                }
            }
        }

        if (hasMimicWardenPower && !piglinPlayers.contains(e.getPlayer())) {
            piglinPlayers.add(e.getPlayer());
        } else if (!hasMimicWardenPower) {
            piglinPlayers.remove(e.getPlayer());
        }
    }

    @Override
    public String getPowerFile() {
        return "genesis:simple-implementation-piglin-no-attack[@dueris]";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return FoliaOriginScheduler.OriginSchedulerTree.piglin_no_attack;
    }

    @Override
    public void setActive(Player p, String tag, Boolean bool) {
        if(powers_active.containsKey(p)){
            if(powers_active.get(p).containsKey(tag)){
                powers_active.get(p).replace(tag, bool);
            }else{
                powers_active.get(p).put(tag, bool);
            }
        }else{
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
        }
    }

    @Override
    public String getSimpleTagID() {
        return "origins:piglin_brothers";
    }
}
