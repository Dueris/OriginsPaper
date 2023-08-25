package me.dueris.genesismc.factory.powers.value_modifying;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static me.dueris.genesismc.factory.powers.value_modifying.ValueModifyingSuperClass.modify_falling;

public class ModifyFallingPower extends CraftPower implements Listener {

    @Override
    public void setActive(String tag, Boolean bool){
        if(powers_active.containsKey(tag)){
            powers_active.replace(tag, bool);
        }else{
            powers_active.put(tag, bool);
        }
    }

    

    @EventHandler
    public void run(PlayerMoveEvent e){
        Player p = e.getPlayer();
        if(modify_falling.contains(p)){
            if(p.getVelocity().getY() < 0){
                @NotNull Vector velocity = p.getVelocity();
                for(OriginContainer origin : OriginPlayer.getOrigin(p).values()){
                    ConditionExecutor conditionExecutor = new ConditionExecutor();
                    if(conditionExecutor.check("condition", "conditions", p, origin, "origins:modify_falling", null, p)){
                        velocity.setY(Integer.parseInt(origin.getPowerFileFromType("origins:modify_falling").get("velocity", null)));
                        if(!getPowerArray().contains(p)) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                    }else{
                        if(!getPowerArray().contains(p)) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                    }
                }
            }
        }
    }

    @EventHandler
    public void run(EntityDamageEvent e){
        if(e.getEntity() instanceof Player p){
            if(modify_falling.contains(p)){
                for(OriginContainer origin : OriginPlayer.getOrigin(p).values()){
                    ConditionExecutor conditionExecutor = new ConditionExecutor();
                    if(conditionExecutor.check("condition", "conditions", p, origin, "origins:modify_falling", e, p)){
                        if(Boolean.getBoolean(origin.getPowerFileFromType("origins:modify_falling").get("take_fall_damage", "true"))){
                            if(e.getCause() == EntityDamageEvent.DamageCause.FALL){
                                e.setDamage(0);
                                e.setCancelled(true);
                            }
                        }
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
        return "origins:modify_falling";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_falling;
    }
}
