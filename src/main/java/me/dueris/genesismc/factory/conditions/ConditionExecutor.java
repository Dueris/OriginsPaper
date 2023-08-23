package me.dueris.genesismc.factory.conditions;

import me.dueris.genesismc.factory.conditions.damage.DamageCondition;
import me.dueris.genesismc.factory.conditions.entity.EntityCondition;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.print.attribute.standard.JobKOctets;
import java.util.HashMap;

public class ConditionExecutor {
    public boolean check(String singular, String plural, Player p, OriginContainer origin, String powerfile, EntityDamageEvent dmgevent, Entity entity) {
        if(origin == null) return true;
        if(origin.getPowerFileFromType(powerfile) == null) return true;
        for(HashMap<String, Object> condition : origin.getPowerFileFromType(powerfile).getConditionFromString(singular, plural)){
            if(condition.get("type").equals("origins:and")){
                JSONArray conditionsArray = (JSONArray) condition.get("conditions");
                boolean allConditionsTrue = checkConditions(conditionsArray, p, entity, dmgevent);

                if (allConditionsTrue) {
                    return true;
                } else {
                    return false;
                }
            }else if(condition.get("type").equals("origins:or")){
                JSONArray conditionsArray = (JSONArray) condition.get("conditions");
                boolean anyConditionTrue = false;

                for (Object subConditionObj : conditionsArray) {
                    if (subConditionObj instanceof JSONObject) {
                        JSONObject subCondition = (JSONObject) subConditionObj;
                        boolean subConditionResult = checkSubCondition(subCondition, p, entity, dmgevent);
                        if (subConditionResult) {
                            return true;
                        }
                    }
                }
            }else if(condition.get("type").equals("origins:constant")){
                return (boolean) condition.get("value");
            }else{
                if (origin.getPowerFileFromType(powerfile).getConditionFromString(singular, plural) == null){
                    return true;
                }
                if (dmgevent != null) {
                    if (DamageCondition.check(condition, p, dmgevent) == "true"){
                        return true;
                    }
                }
                if (entity != null) {
                    if (EntityCondition.check(condition, p, entity) == "true"){
                        return true;
                    }
                }
            }
            return DamageCondition.check(condition, p, dmgevent) == "null" && EntityCondition.check(condition, p, entity) == "null";
        }
        return true;
    }

    private static boolean checkSubCondition(JSONObject subCondition, Player p, Entity entity, EntityDamageEvent dmgevent) {
        if ("origins:and".equals(subCondition.get("type"))) {
            JSONArray conditionsArray = (JSONArray) subCondition.get("conditions");
            boolean allTrue = true;

            for (Object subConditionObj : conditionsArray) {
                if (subConditionObj instanceof JSONObject) {
                    JSONObject subSubCondition = (JSONObject) subConditionObj;
                    boolean subSubConditionResult = checkSubCondition(subSubCondition, p, entity, dmgevent);
                    if (!subSubConditionResult) {
                        allTrue = false;
                        break;
                    }
                }
            }

            return allTrue;
        } else {
            if (dmgevent != null) {
                return DamageCondition.check(subCondition, p, dmgevent).equals("true");
            } else if (entity != null) {
                return EntityCondition.check(subCondition, p, entity).equals("true");
            }
            return false;
        }
    }

    public static boolean checkConditions(JSONArray conditionsArray, Player p, Entity entity, EntityDamageEvent dmgevent) {
        boolean allTrue = true;

        for (Object subConditionObj : conditionsArray) {
            if (subConditionObj instanceof JSONObject) {
                JSONObject subCondition = (JSONObject) subConditionObj;
                boolean subConditionResult = checkSubCondition(subCondition, p, entity, dmgevent);
                if (!subConditionResult) {
                    allTrue = false;
                    break;
                }
            }
        }

        return allTrue;
    }
}