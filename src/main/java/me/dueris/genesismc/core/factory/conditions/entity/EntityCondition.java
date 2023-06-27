package me.dueris.genesismc.core.factory.conditions.entity;

import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class EntityCondition {
    public static boolean check(Player p, OriginContainer origin, String powerfile, Entity entity){
        String type = origin.getPowerFileFromType(powerfile).getEntityCondition().get("type").toString();
        if(type.equalsIgnoreCase("origins:ability")){
            p.sendMessage("dfs");
            return true;
        }
        return false;
    }
}
