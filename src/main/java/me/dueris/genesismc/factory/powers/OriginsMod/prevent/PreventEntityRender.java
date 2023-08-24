package me.dueris.genesismc.factory.powers.OriginsMod.prevent;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static me.dueris.genesismc.factory.powers.OriginsMod.prevent.PreventSuperClass.prevent_entity_render;

public class PreventEntityRender extends CraftPower {

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

    @Override
    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()){
            if(prevent_entity_render.contains(p)){
                for(OriginContainer origin : OriginPlayer.getOrigin(p).values()){
                    for(Entity entity : p.getWorld().getEntities()){
                        ConditionExecutor conditionExecutor = new ConditionExecutor();
                        if(conditionExecutor.check("entity_condition", "entity_condition", p, origin, "origins:prevent_entity_render", null, p)){
                            if(conditionExecutor.check("bientity_condition", "bientity_condition", p, origin, "origins:prevent_entity_render", null, p)){
                                p.hideEntity(GenesisMC.getPlugin(), entity);
                                setActive(true);
                            }else{
                                setActive(false);
                                p.showEntity(GenesisMC.getPlugin(), entity);
                            }
                        }else{
                            setActive(false);
                            p.showEntity(GenesisMC.getPlugin(), entity);
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:prevent_entity_render";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return prevent_entity_render;
    }
}
