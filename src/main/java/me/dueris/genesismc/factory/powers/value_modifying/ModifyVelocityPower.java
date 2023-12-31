package me.dueris.genesismc.factory.powers.value_modifying;

import static me.dueris.genesismc.factory.powers.player.attributes.AttributeHandler.getOperationMappingsFloat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BinaryOperator;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;

public class ModifyVelocityPower extends CraftPower implements Listener{

    @Override
    public void run(Player p) {
        // do nothing
    }

    @EventHandler
    public void velcotiyWEEEEEEEE(PlayerVelocityEvent e){
        if(getPowerArray().contains(e.getPlayer())){
            Player p = e.getPlayer();
            for(me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()){
                for(PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(p, getPowerFile(), layer)){
                    List<String> identifiers = power.getJsonArray("axes");
                    if(identifiers.isEmpty()){
                        identifiers.add("x");
                        identifiers.add("y");
                        identifiers.add("z");
                    }
                    Vector vel = e.getVelocity();
                    for (HashMap<String, Object> modifier : power.getConditionFromString("modifier", "modifiers")) {
                        Float value = Float.valueOf(modifier.get("value").toString());
                        String operation = modifier.get("operation").toString();
                        BinaryOperator mathOperator = getOperationMappingsFloat().get(operation);
                            for(String axis : identifiers){
                                if(axis == "x"){
                                    vel.setX((float) mathOperator.apply(vel.getX(), value));
                                }
                                if(axis == "y"){
                                    vel.setY((float) mathOperator.apply(vel.getY(), value));
                                }
                                if(axis == "z"){
                                    vel.setZ((float) mathOperator.apply(vel.getZ(), value));
                                }
                            }
                    }
                    setActive(p, power.getTag(), true);
                    e.setVelocity(vel);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            setActive(p, power.getTag(), false);
                        }
                        
                    }.runTaskLater(GenesisMC.getPlugin(), 1);
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:modify_velocity";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return ValueModifyingSuperClass.modify_velocity;
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
