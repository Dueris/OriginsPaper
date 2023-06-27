package me.dueris.genesismc.core.factory.conditions;

import me.dueris.genesismc.core.factory.conditions.damage.DamageCondition;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class Conditions {
    public static boolean check(Player p, OriginContainer origin, String powerfile, EntityDamageEvent dmgevent, Entity entity){
        if(dmgevent != null){
            return DamageCondition.checkDamageCondition(p, origin, powerfile, dmgevent);
        }
        if(entity != null){
            return EntityCondition.checkEntityCondition(p, origin, powerfile, entity);
        }
        return true;
    }
}

