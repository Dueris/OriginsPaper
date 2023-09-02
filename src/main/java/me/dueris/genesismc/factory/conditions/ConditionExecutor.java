package me.dueris.genesismc.factory.conditions;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.biEntity.BiEntityCondition;
import me.dueris.genesismc.factory.conditions.biome.BiomeCondition;
import me.dueris.genesismc.factory.conditions.block.BlockCondition;
import me.dueris.genesismc.factory.conditions.damage.DamageCondition;
import me.dueris.genesismc.factory.conditions.entity.EntityCondition;
import me.dueris.genesismc.factory.conditions.fluid.FluidCondition;
import me.dueris.genesismc.factory.conditions.item.ItemCondition;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.Fluid;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static me.dueris.genesismc.factory.powers.CraftPower.findCraftPowerClasses;
import static me.dueris.genesismc.factory.powers.Power.powers_active;

public class ConditionExecutor {
    private static boolean checkSubCondition(JSONObject subCondition, Player p, Entity actor, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent dmgevent, String powerFile) {
        if ("origins:and".equals(subCondition.get("type"))) {
            JSONArray conditionsArray = (JSONArray) subCondition.get("conditions");
            boolean allTrue = true;

            for (Object subConditionObj : conditionsArray) {
                if (subConditionObj instanceof JSONObject subSubCondition) {
                    boolean subSubConditionResult = checkSubCondition(subSubCondition, p, actor, target, block, fluid, itemStack, dmgevent, powerFile);
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
                var check = DamageCondition.check(subCondition, p, dmgevent, powerFile);
                if (check.isPresent()) {
                    subConditionResult = (boolean) check.get();
                }

            } else if (actor != null) {
                var check = EntityCondition.check(subCondition, p, actor, powerFile);
                if (check.isPresent()) {
                    subConditionResult = (boolean) check.get();
                }
            } else if (actor != null && target != null) {
                var check = BiEntityCondition.check(subCondition, p, actor, target, powerFile);
                if (check.isPresent()) {
                    subConditionResult = (boolean) check.get();
                }
            } else if (block != null) {
                var check = BlockCondition.check(subCondition, p, p.getLocation().getBlock(), powerFile);
                if (check.isPresent()) {
                    subConditionResult = (boolean) check.get();
                }
                var check2 = BiomeCondition.check(subCondition, p, p.getLocation().getBlock(), powerFile);
                if (check2.isPresent()) {
                    subConditionResult = (boolean) check.get();
                }
            } else if (fluid != null){
                var check = FluidCondition.check(subCondition, p, fluid, powerFile);
                if (check.isPresent()) {
                    subConditionResult = (boolean) check.get();
                }
            } else if (itemStack != null) {
                var check = ItemCondition.check(subCondition, p, itemStack, powerFile);
                if (check.isPresent()) {
                    subConditionResult = (boolean) check.get();
                }
            }

            return (boolean) subCondition.getOrDefault("inverted", false) != subConditionResult;
        }
    }

    public static boolean checkConditions(JSONArray conditionsArray, Player p, Entity actor, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent dmgevent, String powerFile) {
        boolean allTrue = true;

        for (Object subConditionObj : conditionsArray) {
            if (subConditionObj instanceof JSONObject subCondition) {
                boolean subConditionResult = checkSubCondition(subCondition, p, actor, target, block, fluid, itemStack, dmgevent, powerFile);
                if (!subConditionResult) {
                    allTrue = false;
                    break;
                }
            }
        }

        return allTrue;
    }

    public boolean check(String singular, String plural, Player p, OriginContainer origin, String powerfile, Entity actor, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent dmgevent) {
        if (origin == null) return true;
        if (origin.getPowerFileFromType(powerfile) == null) return true;
        if (origin.getPowerFileFromType(powerfile).getConditionFromString(singular, plural) == null) return true;
        for (HashMap<String, Object> condition : origin.getPowerFileFromType(powerfile).getConditionFromString(singular, plural)) {
            if (condition.get("type").equals("origins:and")) {
                JSONArray conditionsArray = (JSONArray) condition.get("conditions");

                return checkConditions(conditionsArray, p, actor, target, block, fluid, itemStack, dmgevent, powerfile);
            } else if (condition.get("type").equals("origins:or")) {
                JSONArray conditionsArray = (JSONArray) condition.get("conditions");
                boolean anyConditionTrue = false;

                for (Object subConditionObj : conditionsArray) {
                    if (subConditionObj instanceof JSONObject subCondition) {
                        boolean subConditionResult = checkSubCondition(subCondition, p, actor, target, block, fluid, itemStack, dmgevent, powerfile);
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
                List<Class<? extends CraftPower>> craftPowerClasses;
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
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                if (origin.getPowerFileFromType(powerfile).getConditionFromString(singular, plural) == null) {
                    return true;
                }
                if (dmgevent != null) {
                    Optional<Boolean> check = DamageCondition.check(condition, p, dmgevent, powerfile);
                    if (check.isPresent() && check.get()) {
                        return true;
                    }
                }
                if (actor != null) {
                    Optional<Boolean> check = EntityCondition.check(condition, p, actor, powerfile);
                    if (check.isPresent() && check.get()) {
                        return true;
                    }
                }
                if (actor != null && target != null) {
                    Optional<Boolean> check = BiEntityCondition.check(condition, p, actor, target, powerfile);
                    if (check.isPresent() && check.get()) {
                        return true;
                    }
                }
                if (block != null) {
                    Optional<Boolean> check = BlockCondition.check(condition, p, block, powerfile);
                    if (check.isPresent() && check.get()) {
                        return true;
                    }
                    Optional<Boolean> checkB = BiomeCondition.check(condition, p, block, powerfile);
                    if (checkB.isPresent() && checkB.get()) {
                        return true;
                    }
                }
                if (fluid != null) {
                    Optional<Boolean> check = FluidCondition.check(condition, p, fluid, powerfile);
                    if (check.isPresent() && check.get()) {
                        return true;
                    }
                }
                if (itemStack != null) {
                    Optional<Boolean> check = ItemCondition.check(condition, p, itemStack, powerfile);
                    if (check.isPresent() && check.get()) {
                        return true;
                    }
                }
            }
            return FluidCondition.check(condition, p, fluid, powerfile).isEmpty() && ItemCondition.check(condition, p, itemStack, powerfile).isEmpty() && BlockCondition.check(condition, p, block, powerfile).isEmpty() && BiomeCondition.check(condition, p, block, powerfile).isEmpty() && BiEntityCondition.check(condition, p, actor, target, powerfile).isEmpty() && DamageCondition.check(condition, p, dmgevent, powerfile).isEmpty() && EntityCondition.check(condition, p, actor, powerfile).isEmpty();
        }
        return true;
    }

    public static Optional<Boolean> getResult(boolean inverted, boolean condition) {
        return Optional.of(inverted ? !condition : condition);
    }
}