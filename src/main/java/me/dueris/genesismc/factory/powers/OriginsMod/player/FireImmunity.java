package me.dueris.genesismc.factory.powers.OriginsMod.player;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;

public class FireImmunity extends CraftPower implements Listener {

    @Override
    public void setActive(String tag, Boolean bool){
        if(powers_active.containsKey(tag)){
            powers_active.replace(tag, bool);
        }else{
            powers_active.put(tag, bool);
        }
    }

    

    @EventHandler
    public void OnDamageFire(EntityDamageEvent e) {
        if (e.getEntity().isDead()) return;
        if (e.getEntity() == null) return;
        if (e.getEntity() instanceof Player p) {
            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                if (fire_immunity.contains(p)) {
                    ConditionExecutor conditionExecutor = new ConditionExecutor();
                    if (conditionExecutor.check("condition", "conditions", p, origin, "origins:fire_immunity", null, p)) {
                        setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                        if (e.getCause().equals(EntityDamageEvent.DamageCause.FIRE) || e.getCause().equals(EntityDamageEvent.DamageCause.HOT_FLOOR) || e.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK) || e.getCause().equals(EntityDamageEvent.DamageCause.LAVA)) {
                            e.setCancelled(true);
                            e.setDamage(0);
                        }
                    }else{
                        setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                    }
                }
            }
        }
    }

    @Override
    public void run() {

    }

    @Override
    public String getPowerFile() {
        return "fire_immunity";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return fire_immunity;
    }
}
