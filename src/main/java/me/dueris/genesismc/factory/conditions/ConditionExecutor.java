package me.dueris.genesismc.factory.conditions;

import me.dueris.genesismc.factory.conditions.damage.DamageCondition;
import me.dueris.genesismc.factory.conditions.entity.EntityCondition;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.files.GenesisDataFiles;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.print.attribute.standard.JobKOctets;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static me.dueris.genesismc.factory.powers.CraftPower.findCraftPowerClasses;
import static me.dueris.genesismc.factory.powers.Power.powers_active;

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
            }else if(condition.get("type").equals("origins:power_active")){
                for(String string : powers_active.keySet()){
                    String power = condition.get("power").toString();
                    if(powers_active.containsKey(power)){
                        if(powers_active.get(power)){
                            return true;
                        }else{
                            return false;
                        }
                    }else{
                        return false;
                    }
                }
            }else if(condition.get("type").equals("origins:power_type")){
                List<Class<? extends CraftPower>> craftPowerClasses = null;
                try {
                    craftPowerClasses = findCraftPowerClasses();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                for (Class<? extends CraftPower> c : craftPowerClasses) {
                    String pt = condition.get("power_type").toString();
                    try {
                        if(c.newInstance().getPowerFile().equals(pt)){
                            if(c.newInstance().getPowerArray().contains(p)) {
                                return true;
                            }else{
                                return false;
                            }
                        }else{
                            return false;
                        }
                    } catch (InstantiationException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
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