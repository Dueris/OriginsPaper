package me.dueris.genesismc.core.factory.conditions.entity;

import me.dueris.genesismc.core.utils.OriginContainer;
import net.minecraft.advancements.AdvancementList;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementDisplayType;
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
            if(ability.equalsIgnoreCase("minecraft:invuln" +
                    "rable")){
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

        if(type.equalsIgnoreCase("origins:advancement")){
            String advancementString = origin.getPowerFileFromType(powerfile).getEntityCondition().get("advancement").toString();
            if(entity instanceof Player){
                Player player = (Player) entity;
                Advancement advancement = Bukkit.getAdvancement(NamespacedKey.minecraft(advancementString));
                //advancementString.split("/")[1].toUpperCase();
                if(player.hasAI()){
                    return true;
                }
            }
        }
        return false;
    }
}
