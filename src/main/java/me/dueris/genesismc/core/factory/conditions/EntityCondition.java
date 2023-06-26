package me.dueris.genesismc.core.factory.conditions;

import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityCondition {
    public static boolean checkEntityCondition(Player p, OriginContainer origin, String powerfile, Entity entity) {
        try{
            String entity_con_type = origin.getPowerFileFromType(powerfile).getEntityConditionFromDamageCondition().get("type").toString();
            String entity_type = origin.getPowerFileFromType(powerfile).getEntityConditionFromDamageCondition().get("entity_type").toString();
            if(entity_con_type.equalsIgnoreCase("origins:entity_type")){
                EntityType entityType = EntityType.valueOf(entity_type.split(":")[1].toUpperCase());
                if(entity.getType().equals(entityType)){
                    return true;
                }
            }
            if(entity_con_type.equalsIgnoreCase("origins:ability")){
                try{
                    String ability = origin.getPowerFileFromType(powerfile).getEntityConditionFromAnywhere().get("ability").toString();
                    if(ability.equalsIgnoreCase("minecraft:flying")){
                        if(entity instanceof Player){
                            return ((Player) entity).isGliding() || ((Player) entity).isFlying();
                        }

                    }
                    if(ability.equalsIgnoreCase("instabuild")){
                        if(entity instanceof Player){
                            Player player = (Player) entity;
                        }
                    }
                }catch (Exception e){
                    return false;
                }

            }
        }catch (Exception e){
            return false;
        }
        return false;
    }

}
