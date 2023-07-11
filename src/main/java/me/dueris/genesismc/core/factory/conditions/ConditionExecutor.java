package me.dueris.genesismc.core.factory.conditions;

import me.dueris.genesismc.core.factory.conditions.damage.DamageCondition;
import me.dueris.genesismc.core.factory.conditions.entity.EntityCondition;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.awt.*;

public class ConditionExecutor {
    public static boolean check(Player p, OriginContainer origin, String powerfile, EntityDamageEvent dmgevent, Entity entity){
        if(dmgevent != null){
            if(DamageCondition.check(p, origin, powerfile, dmgevent) == "true") return true;
        }
        if(entity != null){
            if(EntityCondition.check(p, origin, powerfile, entity) == "true") return true;
        }

        //final check
        if(origin.getPowerFileFromType(powerfile).getEntityCondition() == null && origin.getPowerFileFromType(powerfile).getDamageCondition() == null) return true;
        if(DamageCondition.check(p, origin, powerfile, dmgevent) == "null" && EntityCondition.check(p, origin, powerfile, entity) == "null") return true;

        return false;
    }
}

