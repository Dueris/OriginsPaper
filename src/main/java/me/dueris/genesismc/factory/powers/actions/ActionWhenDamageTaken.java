package me.dueris.genesismc.factory.powers.actions;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.LayerContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public class ActionWhenDamageTaken extends CraftPower implements Listener {

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void d(EntityDamageEvent e) {
        Entity actor = e.getEntity();
        if (!(actor instanceof Player player)) return;
        for (LayerContainer layer : CraftApoli.getLayers()) {
            for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(player, getPowerFile(), layer)) {
                if (power == null) continue;
                if(GenesisMC.getConditionExecutor().check("damage_condition", "damage_conditions", player, power, getPowerFile(), player, null, player.getLocation().getBlock(), null, null, e)){
                    if(GenesisMC.getConditionExecutor().check("condition", "conditions", player, power, getPowerFile(), player, null, player.getLocation().getBlock(), null, null, e)){
                        Actions.EntityActionType(actor, power.getEntityAction());
                        Actions.EntityActionType(actor, power.getAction("action"));

                        setActive(player, power.getTag(), true);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                setActive(player, power.getTag(), false);
                            }
                        }.runTaskLater(GenesisMC.getPlugin(), 2L);
                    }
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:action_when_damage_taken";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return action_when_damage_taken;
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
