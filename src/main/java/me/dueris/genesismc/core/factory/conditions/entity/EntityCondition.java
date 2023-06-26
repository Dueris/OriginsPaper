package me.dueris.genesismc.core.factory.conditions.entity;

import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.entity.Player;

public class EntityCondition {
    public static boolean check(Player p, OriginContainer origin, String powerfile){
        String type = origin.getPowerFileFromType(powerfile).getEntityCondition().get("type").toString();
        return false;
    }
}
