package me.dueris.genesismc.core.factory.conditions;

import me.dueris.genesismc.core.factory.conditions.damage.DamageCondition;
import me.dueris.genesismc.core.factory.conditions.entity.EntityCondition;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class ConditionExecutor {
    public static boolean check(String keythingerthatineedtogetforthethingeridkwhyimadethissolonglmao, Player p, OriginContainer origin, String powerfile, EntityDamageEvent dmgevent, Entity entity) {
        if(origin.getPowerFileFromType(powerfile) == null) return false;
        if (origin.getPowerFileFromType(powerfile).getConditionFromString(keythingerthatineedtogetforthethingeridkwhyimadethissolonglmao) == null)
            return true;

        if (dmgevent != null) {
            if (DamageCondition.check(keythingerthatineedtogetforthethingeridkwhyimadethissolonglmao, p, origin, powerfile, dmgevent) == "true")
                return true;
        }
        if (entity != null) {
            if (EntityCondition.check(keythingerthatineedtogetforthethingeridkwhyimadethissolonglmao, p, origin, powerfile, entity) == "true")
                return true;
        }

        //final check
        return DamageCondition.check(keythingerthatineedtogetforthethingeridkwhyimadethissolonglmao, p, origin, powerfile, dmgevent) == "null" && EntityCondition.check(keythingerthatineedtogetforthethingeridkwhyimadethissolonglmao, p, origin, powerfile, entity) == "null";
    }
}