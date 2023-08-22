package me.dueris.genesismc.core.factory.conditions;

import me.dueris.genesismc.core.factory.conditions.damage.DamageCondition;
import me.dueris.genesismc.core.factory.conditions.entity.EntityCondition;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class ConditionExecutor {
    public boolean check(String singular, String plural, Player p, OriginContainer origin, String powerfile, EntityDamageEvent dmgevent, Entity entity) {
        if(origin == null) return true;
        if(origin.getPowerFileFromType(powerfile) == null) return true;
        if (origin.getPowerFileFromType(powerfile).getConditionFromString(singular, plural) == null){
            return true;
        }
        if (dmgevent != null) {
            if (DamageCondition.check(singular, plural, p, origin, powerfile, dmgevent) == "true"){
                return true;
            }
        }
        if (entity != null) {
            if (EntityCondition.check(singular, plural, p, origin, powerfile, entity) == "true"){
                return true;
            }
        }

        //final check
        return DamageCondition.check(singular, plural, p, origin, powerfile, dmgevent) == "null" && EntityCondition.check(singular, plural, p, origin, powerfile, entity) == "null";
    }
}