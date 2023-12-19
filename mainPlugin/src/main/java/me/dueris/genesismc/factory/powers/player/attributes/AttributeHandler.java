package me.dueris.genesismc.factory.powers.player.attributes;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.choosing.ChoosingCORE;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.events.AttributeExecuteEvent;
import me.dueris.genesismc.events.OriginChangeEvent;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import me.dueris.genesismc.utils.translation.LangConfig;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;

public class AttributeHandler extends CraftPower implements Listener {

    public static Map<String, BinaryOperator<Double>> getOperationMappingsDouble() {
        Map<String, BinaryOperator<Double>> operationMap = new HashMap<>();
        operationMap.put("addition", (a, b) -> a + b);
        operationMap.put("subtraction", (a, b) -> a - b);
        operationMap.put("multiplication", (a, b) -> a * b);
        operationMap.put("division", (a, b) -> a / b);
        operationMap.put("multiply_base", (a, b) -> a + (a * b));
        operationMap.put("multiply_total", (a, b) -> a * (1 + b));
        operationMap.put("set_total", (a, b) -> b);
        operationMap.put("add_base_early", (a, b) -> a + b);
        operationMap.put("multiply_base_additive", (a, b) -> a + (a * b));
        operationMap.put("multiply_base_multiplicative", (a, b) -> a * (1 + b));
        operationMap.put("add_base_late", (a, b) -> a + b);

        Random random = new Random();

        operationMap.put("add_random_max", (a, b) -> a + random.nextDouble(b));
        operationMap.put("subtract_random_max", (a, b) -> a - random.nextDouble(b));
        operationMap.put("multiply_random_max", (a, b) -> a * random.nextDouble(b));
        operationMap.put("divide_random_max", (a, b) -> a / random.nextDouble(b));

        return operationMap;
    }

    public static Map<String, BinaryOperator<Long>> getOperationMappingsLong() {
        Map<String, BinaryOperator<Long>> operationMap = new HashMap<>();
        operationMap.put("addition", (a, b) -> a + b);
        operationMap.put("subtraction", (a, b) -> a - b);
        operationMap.put("multiplication", (a, b) -> a * b);
        operationMap.put("division", (a, b) -> a / b);
        operationMap.put("multiply_base", (a, b) -> a + (a * b));
        operationMap.put("multiply_total", (a, b) -> a * (1 + b));
        operationMap.put("set_total", (a, b) -> b);
        operationMap.put("add_base_early", (a, b) -> a + b);
        operationMap.put("multiply_base_additive", (a, b) -> a + (a * b));
        operationMap.put("multiply_base_multiplicative", (a, b) -> a * (1 + b));
        operationMap.put("add_base_late", (a, b) -> a + b);

        Random random = new Random();

        operationMap.put("add_random_max", (a, b) -> a + random.nextLong(b));
        operationMap.put("subtract_random_max", (a, b) -> a - random.nextLong(b));
        operationMap.put("multiply_random_max", (a, b) -> a * random.nextLong(b));
        operationMap.put("divide_random_max", (a, b) -> a / random.nextLong(b));

        return operationMap;
    }

    public static Map<String, BinaryOperator<Integer>> getOperationMappingsInteger() {
        Map<String, BinaryOperator<Integer>> operationMap = new HashMap<>();
        operationMap.put("addition", (a, b) -> a + b);
        operationMap.put("subtraction", (a, b) -> a - b);
        operationMap.put("multiplication", (a, b) -> a * b);
        operationMap.put("division", (a, b) -> a / b);
        operationMap.put("multiply_base", (a, b) -> a + (a * b));
        operationMap.put("multiply_total", (a, b) -> a * (1 + b));
        operationMap.put("set_total", (a, b) -> b);
        operationMap.put("add_base_early", (a, b) -> a + b);
        operationMap.put("multiply_base_additive", (a, b) -> a + (a * b));
        operationMap.put("multiply_base_multiplicative", (a, b) -> a * (1 + b));
        operationMap.put("add_base_late", (a, b) -> a + b);

        Random random = new Random();

        operationMap.put("add_random_max", (a, b) -> a + random.nextInt(b));
        operationMap.put("subtract_random_max", (a, b) -> a - random.nextInt(b));
        operationMap.put("multiply_random_max", (a, b) -> a * random.nextInt(b));
        operationMap.put("divide_random_max", (a, b) -> a / random.nextInt(b));

        return operationMap;
    }

    public static Map<String, BinaryOperator<Float>> getOperationMappingsFloat() {
        Map<String, BinaryOperator<Float>> operationMap = new HashMap<>();
        operationMap.put("addition", (a, b) -> a + b);
        operationMap.put("subtraction", (a, b) -> a - b);
        operationMap.put("multiplication", (a, b) -> a * b);
        operationMap.put("division", (a, b) -> a / b);
        operationMap.put("multiply_base", (a, b) -> a + (a * b));
        operationMap.put("multiply_total", (a, b) -> a * (1 + b));
        operationMap.put("set_total", (a, b) -> b);
        operationMap.put("add_base_early", (a, b) -> a + b);
        operationMap.put("multiply_base_additive", (a, b) -> a + (a * b));
        operationMap.put("multiply_base_multiplicative", (a, b) -> a * (1 + b));
        operationMap.put("add_base_late", (a, b) -> a + b);

        Random random = new Random();

        operationMap.put("add_random_max", (a, b) -> a + random.nextFloat(b));
        operationMap.put("subtract_random_max", (a, b) -> a - random.nextFloat(b));
        operationMap.put("multiply_random_max", (a, b) -> a * random.nextFloat(b));
        operationMap.put("divide_random_max", (a, b) -> a / random.nextFloat(b));

        return operationMap;
    }

    public static void executeAttributeModify(String operation, Attribute attribute_modifier, double base_value, Player p, Double value) {
        BinaryOperator mathOperator = getOperationMappingsDouble().get(operation);
        if (mathOperator != null) {
            double result = (Double) mathOperator.apply(base_value, value);
            p.getAttribute(Attribute.valueOf(attribute_modifier.toString())).setBaseValue(result);
        } else {
            Bukkit.getLogger().warning(LangConfig.getLocalizedString(p, "powers.errors.attribute"));
        }
    }

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }

    @EventHandler
    public void respawn(PlayerRespawnEvent e){
        Player p = e.getPlayer();
        ChoosingCORE.setAttributesToDefault(p);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (attribute.contains(p)) {
                    for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                        for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                            if (power == null) continue;

                            for (HashMap<String, Object> modifier : power.getPossibleModifiers("modifier", "modifiers")) {
                                if (modifier.get("attribute").toString().equalsIgnoreCase("reach-entity-attributes:reach")) {
                                    extra_reach.add(p);
                                    return;
                                } else if (modifier.get("attribute").toString().equalsIgnoreCase("reach-entity-attributes:attack_range")) {
                                    extra_reach_attack.add(p);
                                    return;
                                } else {
                                    Reach.setFinalReach(p, Reach.getDefaultReach(p));
                                }

                                try {
                                    Attribute attribute_modifier = Attribute.valueOf(modifier.get("attribute").toString().split(":")[1].replace(".", "_").toUpperCase());

                                    Object valueObj = modifier.get("value");

                                    if (valueObj instanceof Number) {
                                        double value;
                                        if (valueObj instanceof Integer) {
                                            value = ((Number) valueObj).intValue();
                                        } else if (valueObj instanceof Double) {
                                            value = ((Number) valueObj).doubleValue();
                                        } else if (valueObj instanceof Float) {
                                            value = ((Number) valueObj).floatValue();
                                        } else if (valueObj instanceof Long) {
                                            value = ((Number) valueObj).longValue();
                                        } else {
                                            Objects.requireNonNull(valueObj);
                                            continue;
                                        }

                                        double base_value = p.getAttribute(attribute_modifier).getBaseValue();
                                        String operation = String.valueOf(modifier.get("operation"));
                                        executeAttributeModify(operation, attribute_modifier, base_value, p, value);
                                        AttributeExecuteEvent attributeExecuteEvent = new AttributeExecuteEvent(p, attribute_modifier, power.toString(), origin);
                                        Bukkit.getServer().getPluginManager().callEvent(attributeExecuteEvent);
                                        setActive(power.getTag(), true);
                                        p.sendHealthUpdate();
                                    }
                                } catch (Exception ev) {
                                    ev.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskLater(GenesisMC.getPlugin(), 5L);
    }

    @EventHandler
    public void ExecuteAttributeModification(OriginChangeEvent e) {
        Player p = e.getPlayer();
        ChoosingCORE.setAttributesToDefault(p);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (attribute.contains(p)) {
                    for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                        for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                            if (power == null) continue;

                            for (HashMap<String, Object> modifier : power.getPossibleModifiers("modifier", "modifiers")) {
                                if (modifier.get("attribute").toString().equalsIgnoreCase("reach-entity-attributes:reach")) {
                                    extra_reach.add(p);
                                    return;
                                } else if (modifier.get("attribute").toString().equalsIgnoreCase("reach-entity-attributes:attack_range")) {
                                    extra_reach_attack.add(p);
                                    return;
                                } else {
                                    Reach.setFinalReach(p, Reach.getDefaultReach(p));
                                }

                                try {
                                    Attribute attribute_modifier = Attribute.valueOf(modifier.get("attribute").toString().split(":")[1].replace(".", "_").toUpperCase());

                                    Object valueObj = modifier.get("value");

                                    if (valueObj instanceof Number) {
                                        double value;
                                        if (valueObj instanceof Integer) {
                                            value = ((Number) valueObj).intValue();
                                        } else if (valueObj instanceof Double) {
                                            value = ((Number) valueObj).doubleValue();
                                        } else if (valueObj instanceof Float) {
                                            value = ((Number) valueObj).floatValue();
                                        } else if (valueObj instanceof Long) {
                                            value = ((Number) valueObj).longValue();
                                        } else {
                                            Objects.requireNonNull(valueObj);
                                            continue;
                                        }

                                        double base_value = p.getAttribute(attribute_modifier).getBaseValue();
                                        String operation = String.valueOf(modifier.get("operation"));
                                        executeAttributeModify(operation, attribute_modifier, base_value, p, value);
                                        AttributeExecuteEvent attributeExecuteEvent = new AttributeExecuteEvent(p, attribute_modifier, power.toString(), origin);
                                        Bukkit.getServer().getPluginManager().callEvent(attributeExecuteEvent);
                                        setActive(power.getTag(), true);
                                        p.sendHealthUpdate();
                                    }
                                } catch (Exception ev) {
                                    ev.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskLater(GenesisMC.getPlugin(), 20L);
    }

    public AttributeHandler() {

    }

    @Override
    public void run(Player p) {

    }

    @Override
    public String getPowerFile() {
        return "origins:attribute";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return attribute;
    }

    public static class Reach implements Listener {

        private static Block getClosestBlockInSight(Player player, double maxRange, double normalReach) {
            // Get the player's eye location
            Location eyeLocation = player.getEyeLocation();

            // Get the direction the player is looking at
            Vector direction = eyeLocation.getDirection();

            // Iterate through the blocks in the line of sight
            for (double distance = 0.0; distance <= maxRange; distance += 0.1) {
                Location targetLocation = eyeLocation.clone().add(direction.clone().multiply(distance));
                Block targetBlock = targetLocation.getBlock();

                // Check if the block can be broken and it's outside of the normal reach
                if (targetBlock.getType() != Material.AIR && targetBlock.getType().isSolid()
                        && distance > normalReach) {
                    return targetBlock;
                }
            }

            return null; // No block in sight within the range
        }

        public static int getDefaultReach(Player player) {
            if (player.getGameMode().equals(GameMode.CREATIVE)) {
                return 5;
            }
            return 3;
        }

        public static void setFinalReach(Player p, double value) {
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "reach"), PersistentDataType.DOUBLE, value);
        }

        public static double getFinalReach(Player p) {
            if(p.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "reach"), PersistentDataType.DOUBLE) != null){
                return p.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "reach"), PersistentDataType.DOUBLE);
            }else{
                return getDefaultReach(p);
            }
        }

        @EventHandler
        public void OnClickREACH(PlayerInteractEvent e) {
            Player p = e.getPlayer();
            if (extra_reach_attack.contains(e.getPlayer())) {
                for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {

                    for (PowerContainer power : origin.getMultiPowerFileFromType("origins:attribute")) {
                        for (HashMap<String, Object> modifier : power.getPossibleModifiers("modifier", "modifiers")) {
                            if (!e.getAction().isLeftClick()) return;
                            String operation = String.valueOf(modifier.get("operation"));

                            BinaryOperator mathOperator = getOperationMappingsDouble().get(operation);

                            Object valueObj = modifier.get("value");

                            double base = getDefaultReach(p);

                            if (valueObj instanceof Number) {
                                double value;
                                if (valueObj instanceof Integer) {
                                    value = ((Number) valueObj).intValue();
                                } else if (valueObj instanceof Double) {
                                    value = ((Number) valueObj).doubleValue();
                                } else if (valueObj instanceof Float) {
                                    value = ((Number) valueObj).floatValue();
                                } else if (valueObj instanceof Long) {
                                    value = ((Number) valueObj).longValue();
                                } else {
                                    Objects.requireNonNull(valueObj);
                                    continue;
                                }

                                if (mathOperator != null) {
                                    double result = (double) mathOperator.apply(base, value);
                                    setFinalReach(p, result);
                                } else {
                                    Bukkit.getLogger().warning(LangConfig.getLocalizedString(p, "powers.errors.attribute"));
                                }

                                Location eyeloc = p.getEyeLocation();
                                Predicate<Entity> filter = (entity) -> !entity.equals(p);

                                RayTraceResult traceResult4_5F = p.getWorld().rayTrace(eyeloc, eyeloc.getDirection(), getFinalReach(p), FluidCollisionMode.NEVER, false, 0, filter);

                                if (traceResult4_5F != null) {
                                    Entity entity = traceResult4_5F.getHitEntity();
                                    //entity code -- pvp
                                    if (entity == null) return;
                                    Player attacker = p;
                                    if (entity.isDead() || !(entity instanceof LivingEntity)) return;
                                    if (entity.isInvulnerable()) return;
                                    LivingEntity victim = (LivingEntity) traceResult4_5F.getHitEntity();
                                    if (attacker.getLocation().distance(victim.getLocation()) <= getFinalReach(p)) {
                                        if (entity.getPassengers().contains(p)) return;
                                        if (!entity.isDead()) {
                                            LivingEntity ent = (LivingEntity) entity;
                                            p.attack(ent);
                                        }
                                    } else {
                                        e.setCancelled(true);
                                    }
                                }

                            }
                        }
                    }

                }
            }
        }
    }
}
