package me.dueris.genesismc.core.factory.powers.OriginsMod.player.attributes;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.events.OriginChangeEvent;
import me.dueris.genesismc.core.utils.OriginContainer;
import me.dueris.genesismc.core.utils.PowerContainer;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;

import static me.dueris.genesismc.core.factory.powers.Powers.*;

public class AttributeHandler implements Listener {

    public static Map<String, BinaryOperator<Integer>> getOperationMappingsInt(){
        Map<String, BinaryOperator<Integer>> operationMap = new HashMap<>();
        //base value = a
        //modifier value = b
        operationMap.put("addition", Integer::sum);
        operationMap.put("subtraction", (a, b) -> a - b);
        operationMap.put("multiplication", (a, b) -> a * b);
        operationMap.put("division", (a, b) -> a / b);
        operationMap.put("multiply_base", (a, b) -> a + (a * b));
        operationMap.put("multiply_total", (a, b) -> a * (1 + b));
        operationMap.put("set_total", (a, b) -> b);

        Random random = new Random();

        operationMap.put("add_random_max", (a, b) -> a + random.nextInt(b));
        operationMap.put("subtract_random_max", (a, b) -> a - random.nextInt(b));
        operationMap.put("multiply_random_max", (a, b) -> a * random.nextInt(b));
        operationMap.put("divide_random_max", (a, b) -> a / random.nextInt(b));

        return operationMap;
    }

    public static Map<String, BinaryOperator<Double>> getOperationMappingsDouble(){
        Map<String, BinaryOperator<Double>> operationMap = new HashMap<>();
        //base value = a
        //modifier value = b
        operationMap.put("addition", Double::sum);
        operationMap.put("subtraction", (a, b) -> a - b);
        operationMap.put("multiplication", (a, b) -> a * b);
        operationMap.put("division", (a, b) -> a / b);
        operationMap.put("multiply_base", (a, b) -> a + (a * b));
        operationMap.put("multiply_total", (a, b) -> a * (1 + b));
        operationMap.put("set_total", (a, b) -> b);

        Random random = new Random();

        operationMap.put("add_random_max", (a, b) -> a + random.nextDouble(b));
        operationMap.put("subtract_random_max", (a, b) -> a - random.nextDouble(b));
        operationMap.put("multiply_random_max", (a, b) -> a * random.nextDouble(b));
        operationMap.put("divide_random_max", (a, b) -> a / random.nextDouble(b));

        return operationMap;
    }

    @EventHandler
    public void ExecuteAttributeModification(OriginChangeEvent e) {
        Player p = e.getPlayer();
        if (natural_armor.contains(p)) {
            p.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(8);
        }
        if (nine_lives.contains(p)) {
            p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(18);
        }
        if (attribute.contains(p)) {

            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {

                PowerContainer power = origin.getPowerFileFromType("origins:attribute");
                if (power == null) continue;

                if(power.getModifier().get("attribute").toString().equalsIgnoreCase("reach-entity-attributes:reach")){
                    extra_reach.add(p);
                    p.sendMessage("YEE");
                    return;
                } else if (power.getModifier().get("attribute").toString().equalsIgnoreCase("reach-entity-attributes:attack_range")) {
                    extra_reach_attack.add(p);
                    p.sendMessage("YEE");
                    return;
                } else {Reach.setFinalReachInteger(p, Reach.getDefaultReach(p)); p.sendMessage("NOPE");}

                Attribute attribute_modifier = Attribute.valueOf(power.getModifier().get("attribute").toString().split(":")[1].replace(".", "_").toUpperCase());

                if(power.getModifier().get("value") instanceof Integer){
                    int value = Integer.valueOf(power.getModifier().get("value").toString());
                    int base_value = (int) p.getAttribute(Attribute.valueOf(attribute_modifier.toString())).getBaseValue();
                    String operation = String.valueOf(power.getModifier().get("operation"));
                    executeAttributeModify(operation, attribute_modifier, base_value, p, value);
                    if(power.getModifier().get("update_health").toString() != null){
                        if(power.getModifier().get("update_health").toString().equalsIgnoreCase("true")) p.sendHealthUpdate();
                    }
                } else if (power.getModifier().get("value") instanceof Double) {
                    Double value = Double.valueOf(power.getModifier().get("value").toString());
                    int base_value = (int) p.getAttribute(Attribute.valueOf(attribute_modifier.toString())).getBaseValue();
                    String operation = String.valueOf(power.getModifier().get("operation"));
                    executeAttributeModify(operation, attribute_modifier, base_value, p, value);
                    if(power.getModifier().get("update_health").toString() != null){
                        if(power.getModifier().get("update_health").toString().equalsIgnoreCase("true")) p.sendHealthUpdate();
                    }
                }

            }

        }
    }

    public static void executeAttributeModify(String operation, Attribute attribute_modifier, int base_value, Player p, int value){

        BinaryOperator mathOperator = getOperationMappingsInt().get(operation);
        if (mathOperator != null) {
            int result = (int) mathOperator.apply(base_value, value);
            p.getAttribute(Attribute.valueOf(attribute_modifier.toString())).setBaseValue(result);
        } else {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Unable to parse origins:attribute, unable to get result");
        }
    }

    public static void executeAttributeModify(String operation, Attribute attribute_modifier, int base_value, Player p, Double value){

        BinaryOperator mathOperator = getOperationMappingsDouble().get(operation);
        if (mathOperator != null) {
            double result = (Double) mathOperator.apply(base_value, value);
            p.getAttribute(Attribute.valueOf(attribute_modifier.toString())).setBaseValue(result);
        } else {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Unable to parse origins:attribute, unable to get result");
        }
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

        public static int getDefaultReach(Player player){
            if(player.getGameMode().equals(GameMode.CREATIVE)){
                return 5;
            }
            return 3;
        }

        public static int getFinalReachInteger(Player p){
            return p.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "reach"), PersistentDataType.INTEGER);
        }

        public static void setFinalReachInteger(Player p, int value){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "reach"), PersistentDataType.INTEGER, value);
        }

        public static void setFinalReachDouble(Player p, double value){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "reach"), PersistentDataType.DOUBLE, value);
        }

        public static double getFinalReachDouble(Player p){
            return p.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "reach"), PersistentDataType.DOUBLE);
        }

        public static void DamageReachExecute(Player p, double value, int base, String operation, PlayerInteractEvent e){
            BinaryOperator mathOperator = getOperationMappingsDouble().get(operation);
            if (mathOperator != null) {
                double result = (Double) mathOperator.apply(base, value);
                setFinalReachDouble(p, result);
            } else {
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Unable to parse origins:attribute, unable to get result");
            }

            Location eyeloc = p.getEyeLocation();
            @NotNull Vector direction = eyeloc.getDirection();
            Predicate<Entity> filter = (entity) -> !entity.equals(p);

            RayTraceResult traceResult4_5F = p.getWorld().rayTrace(eyeloc, eyeloc.getDirection(), getFinalReachDouble(p), FluidCollisionMode.NEVER, false, 0, filter);

            if (traceResult4_5F != null) {
                Entity entity = traceResult4_5F.getHitEntity();
                //entity code -- pvp
                if (entity == null) return;
                Player attacker = p;
                if (entity.isDead() || !(entity instanceof LivingEntity)) return;
                if (entity.isInvulnerable()) return;
                LivingEntity victim = (LivingEntity) traceResult4_5F.getHitEntity();
                if (attacker.getLocation().distance(victim.getLocation()) <= getFinalReachInteger(p)) {
                    if (entity.getPassengers().contains(p)) return;
                    if (!entity.isDead()) {
                        LivingEntity ent = (LivingEntity) entity;
                        p.attack(ent);
                    }
                }else{
                    e.setCancelled(true);
                }
            }
        }

        public static void DamageReachExecute(Player p, int value, int base, String operation, PlayerInteractEvent e){
            BinaryOperator mathOperator = getOperationMappingsInt().get(operation);
            if (mathOperator != null) {
                int result = (int) mathOperator.apply(base, value);
                setFinalReachInteger(p, result);
            } else {
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Unable to parse origins:attribute, unable to get result");
            }

            Location eyeloc = p.getEyeLocation();
            @NotNull Vector direction = eyeloc.getDirection();
            Predicate<Entity> filter = (entity) -> !entity.equals(p);

            RayTraceResult traceResult4_5F = p.getWorld().rayTrace(eyeloc, eyeloc.getDirection(), getFinalReachInteger(p), FluidCollisionMode.NEVER, false, 0, filter);

            if (traceResult4_5F != null) {
                Entity entity = traceResult4_5F.getHitEntity();
                //entity code -- pvp
                if (entity == null) return;
                Player attacker = p;
                if (entity.isDead() || !(entity instanceof LivingEntity)) return;
                if (entity.isInvulnerable()) return;
                LivingEntity victim = (LivingEntity) traceResult4_5F.getHitEntity();
                if (attacker.getLocation().distance(victim.getLocation()) <= getFinalReachInteger(p)) {
                    if (entity.getPassengers().contains(p)) return;
                    if (!entity.isDead()) {
                        LivingEntity ent = (LivingEntity) entity;
                        p.attack(ent);
                    }
                }else{
                    e.setCancelled(true);
                }
            }
        }

        @EventHandler
        public void OnClickREACH(PlayerInteractEvent e) {
            Player p = e.getPlayer();
            if (extra_reach_attack.contains(e.getPlayer())) {
                for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {

                    PowerContainer power = origin.getPowerFileFromType("origins:attribute");

                    if (!e.getAction().isLeftClick()) return;
                    String operation = String.valueOf(power.getModifier().get("operation"));
                    int base = getDefaultReach(p);
                    p.sendMessage("pass1");

                    BinaryOperator mathOperator = getOperationMappingsInt().get(operation);
                    if(power.getModifier().get("value") instanceof Integer){
                        if(power.getModifier().get("update_health").toString() != null){
                            if(power.getModifier().get("update_health").toString().equalsIgnoreCase("true")) p.sendHealthUpdate();
                        }
                        DamageReachExecute(p, (int) power.getModifier().get("value"), base, operation, e);
                        p.sendMessage("32");
                    } else if (power.getModifier().get("value") instanceof Double) {
                        if(power.getModifier().get("update_health").toString() != null){
                            if(power.getModifier().get("update_health").toString().equalsIgnoreCase("true")) p.sendHealthUpdate();
                        }
                        DamageReachExecute(p, (Double) power.getModifier().get("value"), base, operation, e);
                        p.sendMessage("32233322");
                    }

                }
            }
        }

        public class Type {

        }

    }
}
