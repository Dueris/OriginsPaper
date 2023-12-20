package me.dueris.genesismc.factory.powers.actions;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public class AttackerActionWhenHit extends CraftPower implements Listener {

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void a(EntityDamageByEntityEvent e) {
        Entity actor = e.getEntity();

        if (!(actor instanceof Player player)) return;
        if (!getPowerArray().contains(actor)) return;

        for (OriginContainer origin : OriginPlayer.getOrigin(player).values()) {
            for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                if (power == null) continue;

                if (!getPowerArray().contains(actor)) return;
                setActive(player, power.getTag(), true);
                Actions.biEntityActionType(actor, actor, power.getBiEntityAction());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!getPowerArray().contains(actor)) return;
                        setActive(player, power.getTag(), false);
                    }
                }.runTaskLater(GenesisMC.getPlugin(), 2L);
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:attacker_action_when_hit";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return attacker_action_when_hit;
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
}
