package me.dueris.genesismc.core.factory.conditions;

import me.dueris.genesismc.core.factory.conditions.damage.DamageCondition;
import me.dueris.genesismc.core.factory.conditions.entity.EntityCondition;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class Conditions {
    public static boolean check(Player p, OriginContainer origin, String powerfile, EntityDamageEvent dmgevent, Entity entity){
        if(origin.getPowerFileFromType(powerfile).getDamageCondition() != null && dmgevent != null){
            if(DamageCondition.checkDamageCondition(p, origin, powerfile, dmgevent)){
                return true;
            }
        }
        if(origin.getPowerFileFromType(powerfile).getEntityCondition() != null && entity != null){
            if(EntityCondition.check(p, origin, powerfile, entity)){
                return true;
            }
        }
        return true;
    }
}

