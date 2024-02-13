package me.dueris.genesismc.factory.conditions;

import me.dueris.genesismc.factory.conditions.biEntity.BiEntityCondition;
import me.dueris.genesismc.factory.conditions.biome.BiomeCondition;
import me.dueris.genesismc.factory.conditions.block.BlockCondition;
import me.dueris.genesismc.factory.conditions.damage.DamageCondition;
import me.dueris.genesismc.factory.conditions.entity.EntityCondition;
import me.dueris.genesismc.factory.conditions.fluid.FluidCondition;
import me.dueris.genesismc.factory.conditions.item.ItemCondition;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.Fluid;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static me.dueris.genesismc.factory.conditions.CraftCondition.*;
import static me.dueris.genesismc.factory.conditions.item.ItemCondition.getMeatMaterials;
import static me.dueris.genesismc.factory.conditions.item.ItemCondition.getNonMeatMaterials;
import static me.dueris.genesismc.factory.powers.Power.powers_active;

public class ConditionExecutor {
    public static BiEntityCondition biEntityCondition = new BiEntityCondition();
    public static BiomeCondition biomeCondition = new BiomeCondition();
    public static BlockCondition blockCondition = new BlockCondition();
    public static DamageCondition damageCondition = new DamageCondition();
    public static EntityCondition entityCondition = new EntityCondition();
    public static FluidCondition fluidCondition = new FluidCondition();
    public static ItemCondition itemCondition = new ItemCondition();

    private static boolean checkSubCondition(JSONObject subCondition, Player p, PowerContainer power, String powerfile, Entity actor, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent dmgevent, String powerFile) {
        if ("apoli:and".equals(subCondition.get("type"))) {
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
        } else if (subCondition.get("type").equals("apoli:power_active")) {
            if (!powers_active.containsKey(p)) return false;
            if (subCondition.get("power").toString().contains("*")) {
                String[] powerK = subCondition.get("power").toString().split("\\*");
                for (String string : powers_active.get(p).keySet()) {
                    if (string.startsWith(powerK[0]) && string.endsWith(powerK[1])) {
                        return powers_active.get(p).get(string);
                    }
                }
            } else {
                String powerF = subCondition.get("power").toString();
                boolean invert = (boolean) subCondition.getOrDefault("inverted", false);
                return getResult(invert, Optional.of(powers_active.get(p).getOrDefault(powerF, false))).get();
            }
        } else {
            AtomicBoolean booleanOptional = null;

            if (booleanOptional == null && dmgevent != null) {
                var check = damageCondition.check(subCondition, actor, target, block, fluid, itemStack, dmgevent);
                booleanOptional = new AtomicBoolean(check.get());
            }

            if (booleanOptional == null && actor != null) {
                var check = entityCondition.check(subCondition, actor, target, block, fluid, itemStack, dmgevent);
                booleanOptional = new AtomicBoolean(check.get());
            }

            if (booleanOptional == null && actor != null && target != null) {
                var check = biEntityCondition.check(subCondition, actor, target, block, fluid, itemStack, dmgevent);
                booleanOptional = new AtomicBoolean(check.get());
            }

            if (booleanOptional == null && block != null) {
                var check = blockCondition.check(subCondition, actor, target, block, fluid, itemStack, dmgevent);
                booleanOptional = new AtomicBoolean(check.get());
            }

            if (booleanOptional == null && block != null) {
                var check = biomeCondition.check(subCondition, actor, target, block, fluid, itemStack, dmgevent);
                booleanOptional = new AtomicBoolean(check.get());
            }

            if (booleanOptional == null && fluid != null) {
                var check = fluidCondition.check(subCondition, actor, target, block, fluid, itemStack, dmgevent);
                booleanOptional = new AtomicBoolean(check.get());
            }

            if (booleanOptional == null && itemStack != null) {
                var check = itemCondition.check(subCondition, actor, target, block, fluid, itemStack, dmgevent);
                booleanOptional = new AtomicBoolean(check.get());
            }

            if (booleanOptional == null) {
                return true;
            } else {
                return booleanOptional.get();
            }
        }
        return false;
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

    public static Optional<Boolean> getResult(boolean inverted, Optional<Boolean> condition) {
        if (condition.isPresent()) {
            if (inverted) {
                return Optional.of(!condition.get());
            } else {
                return Optional.of(condition.get());
            }
        } else {
            return Optional.empty();
        }
    }

    public boolean check(String singular, String plural, Player p, PowerContainer powerContainer, String powerfile, Entity actor, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent dmgevent) {
        if (powerContainer == null) return true;
        if (powerContainer.getJsonListSingularPlural(singular, plural) == null) return true;
        if (powerContainer.getJsonListSingularPlural(singular, plural).isEmpty()) return true;
        for (JSONObject condition : powerContainer.getJsonListSingularPlural(singular, plural)) {
            if (condition.get("type").equals("apoli:and")) {
                JSONArray conditionsArray = (JSONArray) condition.get("conditions");

                return checkConditions(conditionsArray, p, powerContainer, powerfile, actor, target, block, fluid, itemStack, dmgevent, powerfile);
            } else if (condition.get("type").equals("apoli:or")) {
                JSONArray conditionsArray = (JSONArray) condition.get("conditions");

                for (Object subConditionObj : conditionsArray) {
                    if (subConditionObj instanceof JSONObject subCondition) {
                        boolean subConditionResult = checkSubCondition(subCondition, p, powerContainer, powerfile, actor, target, block, fluid, itemStack, dmgevent, powerfile);
                        if (subConditionResult) {
                            return true;
                        }
                    }
                }
            } else if (condition.get("type").equals("apoli:constant")) {
                return (boolean) condition.get("value");
            } else if (condition.get("type").toString().equalsIgnoreCase("apoli:meat")) {
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
                AtomicBoolean booleanOptional = null;

                if (booleanOptional == null && (singular.contains("entity_") || plural.contains("entity_") || plural.equals("conditions") || singular.equals("condition"))) {
                    Optional<Boolean> bool = entity.check(condition, actor, target, block, fluid, itemStack, dmgevent);
                    booleanOptional = new AtomicBoolean(bool.get());
                }
                if (booleanOptional == null && (singular.contains("bientity_") || plural.contains("bientity_") || plural.equals("conditions") || singular.equals("condition"))) {
                    Optional<Boolean> bool = bientity.check(condition, actor, target, block, fluid, itemStack, dmgevent);
                    booleanOptional = new AtomicBoolean(bool.get());
                }
                if (booleanOptional == null && (singular.contains("block_") || plural.contains("block_") || plural.equals("conditions") || singular.equals("condition"))) {
                    Optional<Boolean> bool = blockCon.check(condition, actor, target, block, fluid, itemStack, dmgevent);
                    booleanOptional = new AtomicBoolean(bool.get());
                }
                if (booleanOptional == null && (singular.contains("biome_") || plural.contains("biome_") || plural.equals("conditions") || singular.equals("condition"))) {
                    Optional<Boolean> bool = biome.check(condition, actor, target, block, fluid, itemStack, dmgevent);
                    booleanOptional = new AtomicBoolean(bool.get());
                }
                if (booleanOptional == null && (singular.contains("damage_") || plural.contains("damage_") || plural.equals("conditions") || singular.equals("condition"))) {
                    Optional<Boolean> bool = damage.check(condition, actor, target, block, fluid, itemStack, dmgevent);
                    booleanOptional = new AtomicBoolean(bool.get());
                }
                if (booleanOptional == null && (singular.contains("fluid_") || plural.contains("fluid_") || plural.equals("conditions") || singular.equals("condition"))) {
                    Optional<Boolean> bool = fluidCon.check(condition, actor, target, block, fluid, itemStack, dmgevent);
                    booleanOptional = new AtomicBoolean(bool.get());
                }
                if (booleanOptional == null && (singular.contains("item_") || plural.contains("item_") || plural.equals("conditions") || singular.equals("condition"))) {
                    Optional<Boolean> bool = item.check(condition, actor, target, block, fluid, itemStack, dmgevent);
                    booleanOptional = new AtomicBoolean(bool.get());
                }
                // Custom conditions
                if (booleanOptional == null) {
                    try {
                        for (Class<? extends Condition> conditionClass : customConditions) {
                            Optional<Boolean> bool = conditionClass.newInstance().check(condition, actor, target, block, fluid, itemStack, dmgevent);
                            booleanOptional = new AtomicBoolean(bool.get());
                        }
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (booleanOptional == null) {
                    booleanOptional.set(true);
                }
                return booleanOptional.get();
            }
        }
        return false;
    }
}
