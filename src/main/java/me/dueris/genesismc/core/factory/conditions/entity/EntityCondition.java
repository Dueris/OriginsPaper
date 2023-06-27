package me.dueris.genesismc.core.factory.conditions.entity;

import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class EntityCondition {
    public static boolean check(Player p, OriginContainer origin, String powerfile, Entity entity){
        if(origin.getPowerFileFromType(powerfile).getEntityCondition() == null) return true;

        String type = origin.getPowerFileFromType(powerfile).getEntityCondition().get("type").toString();
        if(type.equalsIgnoreCase("origins:ability")){
            String ability = origin.getPowerFileFromType(powerfile).getEntityCondition().get("ability").toString();
            if(ability.equalsIgnoreCase("minecraft:flying")){
                if(entity instanceof Player){
                    Player player = (Player) entity;
                    if(player.isFlying()) return true;
                }
            }
            if(ability.equalsIgnoreCase("minecraft:instabuild")){
                if(entity instanceof Player){
                    Player player = (Player) entity;
                    if(player.getGameMode().equals(GameMode.CREATIVE)) return true;
                }
            }
            if(ability.equalsIgnoreCase("minecraft:invulnerable")){
                if(entity.isInvulnerable()) return true;
            }
            if(ability.equalsIgnoreCase("minecraft:maybuild")){
                if(entity.hasPermission("minecraft.build")){
                    return true;
                }
            }
            if(ability.equalsIgnoreCase("minecraft:mayfly")){
                if(entity instanceof Player){
                    Player player = (Player) entity;
                    if(player.getAllowFlight() == true) return true;
                }
            }
        }
        return false;
    }
}
