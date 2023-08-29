package me.dueris.genesismc.factory.conditions;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.damage.DamageCondition;
import me.dueris.genesismc.factory.conditions.entity.EntityCondition;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static me.dueris.genesismc.factory.powers.CraftPower.findCraftPowerClasses;
import static me.dueris.genesismc.factory.powers.Power.powers_active;

public class ConditionExecutor {
    private static boolean checkSubCondition(JSONObject subCondition, Player p, Entity entity, EntityDamageEvent dmgevent, String powerFile) {
        if ("origins:and".equals(subCondition.get("type"))) {
            JSONArray conditionsArray = (JSONArray) subCondition.get("conditions");
            boolean allTrue = true;

            for (Object subConditionObj : conditionsArray) {
                if (subConditionObj instanceof JSONObject subSubCondition) {
                    boolean subSubConditionResult = checkSubCondition(subSubCondition, p, entity, dmgevent, powerFile);
                    if (!subSubConditionResult) {
                        allTrue = false;
                        break;
                    }
                }
            }

            return allTrue;
        } else {
            boolean subConditionResult = false;
            if (dmgevent != null) {
                subConditionResult = DamageCondition.check(subCondition, p, dmgevent, powerFile).equals("true");
            } else if (entity != null) {
                subConditionResult = EntityCondition.check(subCondition, p, entity, powerFile).equals("true");
            }
            return (boolean) subCondition.getOrDefault("inverted", false) != subConditionResult;
        }
    }

    public static boolean checkConditions(JSONArray conditionsArray, Player p, Entity entity, EntityDamageEvent dmgevent, String powerFile) {
        boolean allTrue = true;

        for (Object subConditionObj : conditionsArray) {
            if (subConditionObj instanceof JSONObject subCondition) {
                boolean subConditionResult = checkSubCondition(subCondition, p, entity, dmgevent, powerFile);
                if (!subConditionResult) {
                    allTrue = false;
                    break;
                }
            }
        }

        return allTrue;
    }

    public boolean check(String singular, String plural, Player p, OriginContainer origin, String powerfile, EntityDamageEvent dmgevent, Entity entity) {
        if (origin == null) return true;
        if (origin.getPowerFileFromType(powerfile) == null) return true;
        if (origin.getPowerFileFromType(powerfile).getConditionFromString(singular, plural) == null) return true;
        for (HashMap<String, Object> condition : origin.getPowerFileFromType(powerfile).getConditionFromString(singular, plural)) {
            if (condition.get("type").equals("origins:and")) {
                JSONArray conditionsArray = (JSONArray) condition.get("conditions");
                boolean allConditionsTrue = checkConditions(conditionsArray, p, entity, dmgevent, powerfile);

                return allConditionsTrue;
            } else if (condition.get("type").equals("origins:or")) {
                JSONArray conditionsArray = (JSONArray) condition.get("conditions");
                boolean anyConditionTrue = false;

                for (Object subConditionObj : conditionsArray) {
                    if (subConditionObj instanceof JSONObject subCondition) {
                        boolean subConditionResult = checkSubCondition(subCondition, p, entity, dmgevent, powerfile);
                        if (subConditionResult) {
                            return true;
                        }
                    }
                }
            } else if (condition.get("type").equals("origins:constant")) {
                return (boolean) condition.get("value");
            } else if (condition.get("type").equals("origins:power_active")) {
                if (condition.get("power").toString().contains("*")) {
                    String[] powerK = condition.get("power").toString().split("\\*");
                    for (String string : powers_active.keySet()) {
                        if (string.startsWith(powerK[0]) && string.endsWith(powerK[1])) {
                            return powers_active.get(string);
                        }
                    }
                } else {
                    String power = condition.get("power").toString();
                    if (powers_active.containsKey(power)) {
                        return powers_active.get(power);
                    } else {
                        return false;
                    }
                }
            } else if (condition.get("type").equals("origins:power")) {
                for (String string : origin.getPowers()) {
                    String power = condition.get("power").toString();
                    return string.equalsIgnoreCase(power);
                }
            } else if (condition.get("type").equals("origins:origin")) {
                if (OriginPlayer.hasOrigin(p, condition.get("origin").toString())) return true;
            } else if (condition.get("type").equals("origins:power_type")) {
                List<Class<? extends CraftPower>> craftPowerClasses = null;
                try {
                    craftPowerClasses = findCraftPowerClasses();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                for (Class<? extends CraftPower> c : craftPowerClasses) {
                    String pt = condition.get("power_type").toString();
                    try {
                        if (c.newInstance().getPowerFile().equals(pt)) {
                            return c.newInstance().getPowerArray().contains(p);
                        } else {
                            return false;
                        }
                    } catch (InstantiationException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                if (origin.getPowerFileFromType(powerfile).getConditionFromString(singular, plural) == null) {
                    return true;
                }
                if (dmgevent != null) {
                    if (DamageCondition.check(condition, p, dmgevent, powerfile) == "true") {
                        return true;
                    }
                }
                if (entity != null) {
                    if (EntityCondition.check(condition, p, entity, powerfile) == "true") {
                        return true;
                    }
                }
            }
            return DamageCondition.check(condition, p, dmgevent, powerfile) == "null" && EntityCondition.check(condition, p, entity, powerfile) == "null";
        }
        return true;
    }
}