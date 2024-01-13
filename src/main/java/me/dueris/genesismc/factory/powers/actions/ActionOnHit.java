package me.dueris.genesismc.factory.powers.actions;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public class ActionOnHit extends CraftPower implements Listener {

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void action(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player p) {
            Player actor = p;
            Entity target = e.getEntity();
            for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
                if (getPowerArray().contains(p)) {
                    for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                        if (power == null) continue;
                        if (GenesisMC.getConditionExecutor().check("condition", "conditions", p, power, getPowerFile(), actor, target, actor.getLocation().getBlock(), null, p.getActiveItem(), e)) {
                            if (GenesisMC.getConditionExecutor().check("damage_condition", "damage_conditions", p, power, getPowerFile(), actor, target, actor.getLocation().getBlock(), null, p.getActiveItem(), e)) {
                                if (GenesisMC.getConditionExecutor().check("bientity_condition", "bientity_conditions", p, power, getPowerFile(), actor, target, actor.getLocation().getBlock(), null, p.getActiveItem(), e)) {
                                    setActive(p, power.getTag(), true);
                                    Actions.EntityActionType(actor, power.getEntityAction());
                                    Actions.BiEntityActionType(actor, target, power.getBiEntityAction());
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            setActive(p, power.getTag(), false);
                                        }
                                    }.runTaskLater(GenesisMC.getPlugin(), 2L);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:action_on_hit";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return action_on_hit;
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
