package me.dueris.genesismc.factory.powers.OriginsMod.value_modifying;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.factory.powers.OriginsMod.player.attributes.AttributeHandler.getOperationMappingsFloat;
import static me.dueris.genesismc.factory.powers.OriginsMod.value_modifying.ValueModifyingSuperClass.modify_jump;

public class ModifyJumpPower extends CraftPower implements Listener {

    @Override
    public void setActive(String tag, Boolean bool){
        if(powers_active.containsKey(tag)){
            powers_active.replace(tag, bool);
        }else{
            powers_active.put(tag, bool);
        }
    }

    

    @EventHandler
    public void run(PlayerJumpEvent e){
        Player p = e.getPlayer();
        if(modify_jump.contains(p)){
            for(OriginContainer origin : OriginPlayer.getOrigin(p).values()){
                ConditionExecutor conditionExecutor = new ConditionExecutor();
                if(conditionExecutor.check("condition", "conditions", p, origin, "origins:modify_jump", null, p)){
                    for(HashMap<String, Object> modifier : origin.getPowerFileFromType("origins:modify_jump").getPossibleModifiers("modifier", "modifiers")){
                        Float value = Float.valueOf(modifier.get("value").toString());
                        String operation = modifier.get("operation").toString();
                        BinaryOperator mathOperator = getOperationMappingsFloat().get(operation);
                        if (mathOperator != null) {
                            float result = (float) mathOperator.apply(e.getPlayer().getVelocity().getY(), value);
                            ConditionExecutor executor = new ConditionExecutor();
                            if(executor.check("condition", "conditions", p, origin, getPowerFile(), null, p)){
                                if(!getPowerArray().contains(p)) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                                e.getPlayer().getVelocity().setY(result);
                            }else{
                                if(!getPowerArray().contains(p)) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
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
        return "origins:modify_jump";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_jump;
    }
}
