package me.dueris.genesismc.factory.powers.OriginsMod.value_modifying;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.factory.powers.OriginsMod.player.attributes.AttributeHandler.getOperationMappingsFloat;
import static me.dueris.genesismc.factory.powers.OriginsMod.value_modifying.ValueModifyingSuperClass.modify_healing;

public class ModifyHealingPower extends CraftPower implements Listener {

    @Override
    public void setActive(Boolean bool){
        if(powers_active.containsKey(getPowerFile())){
            powers_active.replace(getPowerFile(), bool);
        }else{
            powers_active.put(getPowerFile(), bool);
        }
    }

    @Override
    public Boolean getActive(){
        return powers_active.get(getPowerFile());
    }

    @EventHandler
    public void run(EntityRegainHealthEvent e){
        if(e.getEntity() instanceof Player){
            if(!modify_healing.contains(e.getEntity())) return;
            Player p = (Player) e.getEntity();
            for(OriginContainer origin : OriginPlayer.getOrigin(p).values()){
                for(HashMap<String, Object> modifier : origin.getPowerFileFromType("origins:modify_healing").getPossibleModifiers("modifier", "modifiers")){
                    Float value = Float.valueOf(modifier.get("value").toString());
                    String operation = modifier.get("operation").toString();
                    BinaryOperator mathOperator = getOperationMappingsFloat().get(operation);
                    if (mathOperator != null) {
                        float result = (float) mathOperator.apply(e.getAmount(), value);
                        ConditionExecutor executor = new ConditionExecutor();
                        if(executor.check("condition", "conditions", p, origin, getPowerFile(), null, p)){
                            setActive(true);
                            e.setAmount(result);
                        }else{
                            setActive(false);
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
        return "origins:modify_healing";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_healing;
    }
}
