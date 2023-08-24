package me.dueris.genesismc.factory.powers.OriginsMod.prevent;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static me.dueris.genesismc.factory.powers.OriginsMod.prevent.PreventSuperClass.prevent_entity_collision;

public class PreventEntityCollision extends CraftPower {

    @Override
    public void setActive(String tag, Boolean bool){
        if(powers_active.containsKey(tag)){
            powers_active.replace(tag, bool);
        }else{
            powers_active.put(tag, bool);
        }
    }

    

    @Override
    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()){
            for(OriginContainer origin : OriginPlayer.getOrigin(p).values()){
                if(prevent_entity_collision.contains(p)){
                    ConditionExecutor conditionExecutor = new ConditionExecutor();
                    if(conditionExecutor.check("bientity_condition", "bientity_condition", p, origin, "origins:prevent_entity_collision", null, p)){
                        p.setCollidable(false);
                        setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                    }else{
                        setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                        p.setCollidable(true);
                    }
                }else{
                    p.setCollidable(true);
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:prevent_entity_collision";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return prevent_entity_collision;
    }
}
