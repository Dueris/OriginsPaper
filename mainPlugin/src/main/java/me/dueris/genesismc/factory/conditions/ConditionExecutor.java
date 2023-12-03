package me.dueris.genesismc.factory.conditions;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.biEntity.BiEntityCondition;
import me.dueris.genesismc.factory.conditions.biome.BiomeCondition;
import me.dueris.genesismc.factory.conditions.block.BlockCondition;
import me.dueris.genesismc.factory.conditions.damage.DamageCondition;
import me.dueris.genesismc.factory.conditions.entity.EntityCondition;
import me.dueris.genesismc.factory.conditions.fluid.FluidCondition;
import me.dueris.genesismc.factory.conditions.item.ItemCondition;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.Bukkit;
import org.bukkit.Fluid;
import org.bukkit.block.Biome;
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
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static me.dueris.genesismc.factory.conditions.CraftCondition.*;
import static me.dueris.genesismc.factory.conditions.item.ItemCondition.getMeatMaterials;
import static me.dueris.genesismc.factory.conditions.item.ItemCondition.getNonMeatMaterials;
import static me.dueris.genesismc.factory.powers.CraftPower.findCraftPowerClasses;
import static me.dueris.genesismc.factory.powers.Power.powers_active;

public class ConditionExecutor {
    private static boolean checkSubCondition(JSONObject subCondition, Player p, PowerContainer power, String powerfile, Entity actor, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent dmgevent, String powerFile) {
        if ("origins:and".equals(subCondition.get("type"))) {
            JSONArray conditionsArray = (JSONArray) subCondition.get("conditions");
            boolean allTrue = true;

            for (Object subConditionObj : conditionsArray) {
                if (subConditionObj instanceof JSONObject subSubCondition) {
                    boolean subSubConditionResult = checkSubCondition(subSubCondition, p, power, powerfile, actor, target, block, fluid, itemStack, dmgevent, powerFile);
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
                DamageCondition damageCondition = new DamageCondition();
                var check = damageCondition.check(subCondition, p, power, powerfile, actor, target, block, fluid, itemStack, dmgevent);
                if (check.isPresent()) {
                    subConditionResult = (boolean) check.get();
                }
            }

            if (!subConditionResult && actor != null) {
                EntityCondition entityCondition = new EntityCondition();
                var check = entityCondition.check(subCondition, p, power, powerfile, actor, target, block, fluid, itemStack, dmgevent);
                if (check.isPresent()) {
                    subConditionResult = (boolean) check.get();
                }
            }

            if (!subConditionResult && actor != null && target != null) {
                BiEntityCondition biEntityCondition = new BiEntityCondition();
                var check = biEntityCondition.check(subCondition, p, power, powerfile, actor, target, block, fluid, itemStack, dmgevent);
                if (check.isPresent()) {
                    subConditionResult = (boolean) check.get();
                }
            }

            if (!subConditionResult && block != null) {
                BlockCondition blockCondition = new BlockCondition();
                var check = blockCondition.check(subCondition, p, power, powerfile, actor, target, block, fluid, itemStack, dmgevent);
                if (check.isPresent()) {
                    subConditionResult = (boolean) check.get();
                }

                BiomeCondition biomeCondition = new BiomeCondition();
                var check2 = biomeCondition.check(subCondition, p, power, powerfile, actor, target, block, fluid, itemStack, dmgevent);
                if (check2.isPresent()) {
                    subConditionResult = (boolean) check2.get();
                }
            }

            if (!subConditionResult && fluid != null) {
                FluidCondition fluidCondition = new FluidCondition();
                var check = fluidCondition.check(subCondition, p, power, powerfile, actor, target, block, fluid, itemStack, dmgevent);
                if (check.isPresent()) {
                    subConditionResult = (boolean) check.get();
                }
            }

            if (!subConditionResult && itemStack != null) {
                ItemCondition condition = new ItemCondition();
                var check = condition.check(subCondition, p, power, powerfile, actor, target, block, fluid, itemStack, dmgevent);
                if (check.isPresent()) {
                    subConditionResult = (boolean) check.get();
                }
            }

            return subConditionResult;
        }
    }

    public static boolean checkConditions(JSONArray conditionsArray, Player p, PowerContainer power, String powerfile, Entity actor, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent dmgevent, String powerFile) {
        boolean allTrue = true;

        for (Object subConditionObj : conditionsArray) {
            if (subConditionObj instanceof JSONObject subCondition) {
                boolean subConditionResult = checkSubCondition(subCondition, p, power, powerfile, actor, target, block, fluid, itemStack, dmgevent, powerFile);
                if (!subConditionResult) {
                    allTrue = false;
                    break;
                }
            }
        }

        return allTrue;
    }

    private BiEntityCondition biEntityCondition = new BiEntityCondition();
    private BiomeCondition biomeCondition = new BiomeCondition();
    private BlockCondition blockCondition = new BlockCondition();
    private DamageCondition damageCondition = new DamageCondition();
    private EntityCondition entityCondition = new EntityCondition();
    private FluidCondition fluidCondition = new FluidCondition();
    private ItemCondition itemCondition = new ItemCondition();

    public boolean check(String singular, String plural, Player p, PowerContainer powerContainer, String powerfile, Entity actor, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent dmgevent) {
        if (powerContainer == null) return true;
        if (powerContainer.getConditionFromString(singular, plural) == null) return true;
        if (powerContainer.getConditionFromString(singular, plural).isEmpty()) return true;
        for (HashMap<String, Object> condition : powerContainer.getConditionFromString(singular, plural)) {
            if (condition.get("type").equals("origins:and")) {
                JSONArray conditionsArray = (JSONArray) condition.get("conditions");

                return checkConditions(conditionsArray, p, powerContainer, powerfile, actor, target, block, fluid, itemStack, dmgevent, powerfile);
            } else if (condition.get("type").equals("origins:or")) {
                JSONArray conditionsArray = (JSONArray) condition.get("conditions");
                boolean anyConditionTrue = false;

                for (Object subConditionObj : conditionsArray) {
                    if (subConditionObj instanceof JSONObject subCondition) {
                        boolean subConditionResult = checkSubCondition(subCondition, p, powerContainer, powerfile, actor, target, block, fluid, itemStack, dmgevent, powerfile);
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
                    return powers_active.getOrDefault(power, false);
                }
            } else if (condition.get("type").equals("origins:power")) {
                for (OriginContainer origin : CraftApoli.getOrigins()) {
                    for (String string : origin.getPowers()) {
                        String power = condition.get("power").toString();
                        return string.equalsIgnoreCase(power);
                    }
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
            } else if (condition.get("type").toString().equalsIgnoreCase("origins:meat")) {
                boolean inverted = Boolean.valueOf(condition.getOrDefault("inverted", false).toString());
                if (itemStack.getType().isEdible()) {
                    if (inverted) {
                        if (getNonMeatMaterials().contains(itemStack.getType())) {
                            return true;
                        }
                    } else {
                        if (getMeatMaterials().contains(itemStack.getType())) {
                            return true;
                        }
                    }
                } else {
                    return false;
                }
            } else {
                String boolResult = "empty";

                if(boolResult != "true"){
                    Optional<Boolean> bool = bientity.check(condition, p, powerContainer, powerfile, actor, target, block, fluid, itemStack, dmgevent);
                    if (bool.isPresent()) {
                        boolResult = String.valueOf(bool.get());
                    }
                }
                if(boolResult != "true"){
                    Optional<Boolean> bool = biome.check(condition, p, powerContainer, powerfile, actor, target, block, fluid, itemStack, dmgevent);
                    if (bool.isPresent()) {
                        boolResult = String.valueOf(bool.get());
                    }
                }
                if(boolResult != "true"){
                    Optional<Boolean> bool = blockCon.check(condition, p, powerContainer, powerfile, actor, target, block, fluid, itemStack, dmgevent);
                    if (bool.isPresent()) {
                        boolResult = String.valueOf(bool.get());
                    }
                }
                if(boolResult != "true"){
                    Optional<Boolean> bool = damage.check(condition, p, powerContainer, powerfile, actor, target, block, fluid, itemStack, dmgevent);
                    if (bool.isPresent()) {
                        boolResult = String.valueOf(bool.get());
                    }
                }
                if(boolResult != "true"){
                    Optional<Boolean> bool = entity.check(condition, p, powerContainer, powerfile, actor, target, block, fluid, itemStack, dmgevent);
                    if (bool.isPresent()) {
                        boolResult = String.valueOf(bool.get());
                    }
                }
                if(boolResult != "true"){
                    Optional<Boolean> bool = fluidCon.check(condition, p, powerContainer, powerfile, actor, target, block, fluid, itemStack, dmgevent);
                    if (bool.isPresent()) {
                        boolResult = String.valueOf(bool.get());
                    }
                }
                if(boolResult != "true"){
                    Optional<Boolean> bool = item.check(condition, p, powerContainer, powerfile, actor, target, block, fluid, itemStack, dmgevent);
                    if (bool.isPresent()) {
                        boolResult = String.valueOf(bool.get());
                    }
                }
                // Custom conditions
                if(boolResult != "true"){
                    try {
                        for (Class<? extends Condition> conditionClass : customConditions) {
                            Optional<Boolean> bool = conditionClass.newInstance().check(condition, p, powerContainer, powerfile, actor, target, block, fluid, itemStack, dmgevent);
                            if (bool.isPresent()) {
                                boolResult = String.valueOf(bool.get());
                            }
                        }
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
                if(boolResult == "empty"){
                    boolResult = "true";
                }
                return Boolean.parseBoolean(boolResult);
            }
        }
        return false;
    }

    public static Optional<Boolean> getResult(boolean inverted, boolean condition) {
        if(inverted){
            return Optional.of(!condition);
        }else{
            return Optional.of(condition);
        }
    }
}