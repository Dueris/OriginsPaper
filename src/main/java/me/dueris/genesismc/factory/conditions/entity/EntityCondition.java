package me.dueris.genesismc.factory.conditions.entity;

import me.dueris.genesismc.CooldownStuff;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.Condition;
import me.dueris.genesismc.factory.conditions.biome.BiomeCondition;
import me.dueris.genesismc.factory.conditions.block.BlockCondition;
import me.dueris.genesismc.factory.powers.player.Climbing;
import me.dueris.genesismc.factory.powers.player.FlightElytra;
import me.dueris.genesismc.factory.powers.player.RestrictArmor;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static me.dueris.genesismc.factory.conditions.ConditionExecutor.getResult;
import static me.dueris.genesismc.factory.powers.Power.action_on_being_used;
import static me.dueris.genesismc.factory.powers.player.RestrictArmor.compareValues;

public class EntityCondition implements Condition {

    public static Enchantment getEnchantmentByNamespace(String namespaceString) {
        return Enchantment.getByName(namespaceString);
    }

    private static int countBlocksInCube(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, World world, HashMap<String, Object> condition, Player p, OriginContainer origin, String powerfile, Entity entity, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent dmgevent) {
        int blockCount = 0;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location location = new Location(world, x, y, z);
                    Block blockw = location.getBlock();
                    if (blockw.getType() != Material.AIR) {
                        BlockCondition blockCondition = new BlockCondition();
                        if(blockCondition.check(condition, p, origin, powerfile, entity, target, blockw, fluid, itemStack, dmgevent).isPresent()){
                            if(blockCondition.check(condition, p, origin, powerfile, entity, target, blockw, fluid, itemStack, dmgevent).get()){
                                blockCount++;
                            }
                        }else{
                            blockCount++;
                        }
                    }
                }
            }
        }

        return blockCount;
    }

    private static int countBlocksInStar(int centerX, int centerY, int centerZ, int radius, World world, HashMap<String, Object> condition, Player p, OriginContainer origin, String powerfile, Entity entity, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent dmgevent) {
        int blockCount = 0;

        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int y = centerY - radius; y <= centerY + radius; y++) {
                for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                    double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) + Math.pow(z - centerZ, 2));

                    if (distance <= radius && distance >= radius / 2) {
                        Location location = new Location(world, x, y, z);
                        Block block2 = location.getBlock();

                        if (block2.getType() != Material.AIR) {
                            BlockCondition blockCondition = new BlockCondition();
                            if(blockCondition.check(condition, p, origin, powerfile, entity, target, block2, fluid, itemStack, dmgevent).isPresent()){
                                if(blockCondition.check(condition, p, origin, powerfile, entity, target, block2, fluid, itemStack, dmgevent).get()){
                                    blockCount++;
                                }
                            }else{
                                blockCount++;
                            }
                        }
                    }
                }
            }
        }

        return blockCount;
    }

    public static int countBlocksInSphere(int centerX, int centerY, int centerZ, int radius, World world, HashMap<String, Object> condition, Player p, OriginContainer origin, String powerfile, Entity entity, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent dmgevent) {
        int blockCount = 0;
        int squaredRadius = radius * radius;

        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int y = centerY - radius; y <= centerY + radius; y++) {
                for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                    if ((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY) + (z - centerZ) * (z - centerZ) <= squaredRadius) {
                        Location location = new Location(world, x, y, z);
                        if (location.getBlock().getType() != Material.AIR) {
                            BlockCondition blockCondition = new BlockCondition();
                            if(blockCondition.check(condition, p, origin, powerfile, entity, target, location.getBlock(), fluid, itemStack, dmgevent).isPresent()){
                                if(blockCondition.check(condition, p, origin, powerfile, entity, target, location.getBlock(), fluid, itemStack, dmgevent).get()){
                                    blockCount++;
                                }
                            }else{
                                blockCount++;
                            }
                        }
                    }
                }
            }
        }

        return blockCount;
    }


    @Override
    public String condition_type() {
        return "ENTITY_CONDITION";
    }

    @Override
    public Optional<Boolean> check(HashMap<String, Object> condition, Player p, OriginContainer origin, String powerfile, Entity entity, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent dmgevent) {
        if (origin == null) return Optional.empty();
        if (origin.getPowerFileFromType(powerfile) == null) return Optional.empty();
            if (condition.isEmpty()) return Optional.empty();
            boolean inverted = (boolean) condition.getOrDefault("inverted", false);
            if (condition.get("type") == null) return Optional.empty();
            String type = condition.get("type").toString();
            if (type.equalsIgnoreCase("origins:ability")) {
                String ability = condition.get("ability").toString().toLowerCase();

                switch (ability) {
                    case "minecraft:flying" -> {
                        if (entity instanceof Player player) {
                            return getResult(inverted, player.isFlying());
                        }
                    }
                    case "minecraft:instabuild" -> {
                        if (entity instanceof Player player) {
                            return getResult(inverted, player.getGameMode().equals(GameMode.CREATIVE));
                        }
                    }
                    case "minecraft:invulnerable" -> {
                        return getResult(inverted, entity.isInvulnerable());
                    }
                    case "minecraft:maybuild" -> {
                        return getResult(inverted, entity.hasPermission("minecraft.build"));
                    }
                    case "minecraft:mayfly" -> {
                        if (entity instanceof Player player) {
                            return getResult(inverted, player.getAllowFlight());
                        }
                    }
                }
            }

            if (type.equalsIgnoreCase("origins:advancement")) {
                String advancementString = condition.get("advancement").toString();

                if (entity instanceof Player player) {
                    World world = player.getWorld();
                    File worldFolder = world.getWorldFolder();
                    File advancementsFolder = new File(worldFolder, "advancements");
                    File playerAdvancementFile = new File(advancementsFolder, player.getUniqueId() + ".json");

                    if (playerAdvancementFile.exists()) {
                        try {
                            JSONParser parser = new JSONParser();
                            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(playerAdvancementFile));
                            JSONObject advancementJson = (JSONObject) jsonObject.get(advancementString);

                            if (advancementJson != null) {
                                Boolean done = (Boolean) advancementJson.get("done");
                                return getResult(inverted, Objects.requireNonNullElse(done, false));
                            } else {
                                return getResult(inverted, false);
                            }
                        } catch (IOException | ParseException e) {
                            e.printStackTrace();
                        }
                    } else {
                        return getResult(inverted, false);
                    }
                }
            }

            if (type.equalsIgnoreCase("origins:air")) {
                if (entity instanceof Player player) {
                    if (compareValues(player.getRemainingAir(), condition.get("comparison").toString(), Integer.parseInt(condition.get("compare_to").toString()))) {
                        return getResult(inverted, true);
                    }
                }
            }

            if (type.equalsIgnoreCase("origins:attribute")) {
                if (entity instanceof Player player) {
                    String attributeString = condition.get("attribute").toString().split(":")[1].replace(".", "_").toUpperCase();
                    if (compareValues(player.getAttribute(Attribute.valueOf(attributeString)).getValue(), condition.get("comparison").toString(), Integer.parseInt(condition.get("compare_to").toString()))) {
                        return getResult(inverted, true);
                    }
                }
            }

            if (type.equalsIgnoreCase("origins:block_collision")) {
                String offsetX = condition.get("offset_x").toString();
                String offsetY = condition.get("offset_y").toString();
                String offsetZ = condition.get("offset_z").toString();
                if (entity instanceof Player player) {
                    Location playerLocation = player.getEyeLocation();
                    World world = player.getWorld();

                    int blockX = playerLocation.getBlockX() + Integer.parseInt(offsetX);
                    int blockY = playerLocation.getBlockY() + Integer.parseInt(offsetY);
                    int blockZ = playerLocation.getBlockZ() + Integer.parseInt(offsetZ);

                    Block blockAt = world.getBlockAt(blockX, blockY, blockZ);
                    if (!blockAt.getType().isAir()) {
                        return getResult(inverted, true);
                    }
                }
            }

            if (type.equalsIgnoreCase("origins:block_in_radius")) {
                int radius = Math.toIntExact((Long) condition.get("radius"));
                String shape = condition.get("shape").toString();
                String comparison = condition.get("comparison").toString();
                int compare_to = Integer.parseInt(condition.get("compare_to").toString());

                Location center = entity.getLocation();
                int centerX = center.getBlockX();
                int centerY = center.getBlockY();
                int centerZ = center.getBlockZ();
                World world = center.getWorld();

                int minX = center.getBlockX() - radius;
                int minY = center.getBlockY() - radius;
                int minZ = center.getBlockZ() - radius;
                int maxX = center.getBlockX() + radius;
                int maxY = center.getBlockY() + radius;
                int maxZ = center.getBlockZ() + radius;

                int blockCount = 0;
                HashMap<String, Object> ingredientMap = (HashMap<String, java.lang.Object>) condition.get("block_condition");
                if (shape.equalsIgnoreCase("sphere")) {
                    blockCount = countBlocksInSphere(centerX, centerY, centerZ, radius, world, ingredientMap, p, origin, powerfile, entity, target, block, fluid, itemStack, dmgevent);
                } else if (shape.equalsIgnoreCase("star")) {
                    blockCount = countBlocksInStar(centerX, centerY, centerZ, radius, world, ingredientMap, p, origin, powerfile, entity, target, block, fluid, itemStack, dmgevent);
                } else if (shape.equalsIgnoreCase("cube")) {
                    blockCount = countBlocksInCube(minX, minY, minZ, maxX, maxY, maxZ, world, ingredientMap, p, origin, powerfile, entity, target, block, fluid, itemStack, dmgevent);
                }
                if (compareValues(blockCount, comparison, compare_to)) {
                    return getResult(inverted, true);
                }

            }

            if (type.equalsIgnoreCase("origins:brightness")) {
                String comparison = condition.get("comparison").toString();
                double compare_to = Double.parseDouble(condition.get("compare_to").toString());
                double brightness;
                int lightLevel = entity.getLocation().getBlock().getLightLevel();
                int ambientLight = 0;

                //calculate ambient light
                if (entity.getWorld() == Bukkit.getServer().getWorlds().get(0)) { //is overworld
                    ambientLight = 0;
                } else if (entity.getWorld() == Bukkit.getServer().getWorlds().get(2)) {
                    ambientLight = 1;
                }
                brightness = ambientLight + (1 - ambientLight) * lightLevel / (60 - 3 * lightLevel);
                if (compareValues(brightness, comparison, compare_to)) {
                    return getResult(inverted, true);
                }

            }

            if (type.equalsIgnoreCase("origins:climbing")) {
                if (entity instanceof Player player) {
                    if (player.isClimbing()) {
                        return getResult(inverted, true);
                    }
                    Climbing climbing = new Climbing();
                    if (climbing.isActiveClimbing(player)) {
                        return getResult(inverted, true);
                    }

                }
            }

            if (type.equalsIgnoreCase("origins:collided_horizontally")) {
                if (entity instanceof Player player) {
                    BoundingBox playerBoundingBox = player.getBoundingBox();
                    BoundingBox blockBoundingBox = block.getBoundingBox();

                    if (blockBoundingBox.overlaps(playerBoundingBox)) {
                        return getResult(inverted, true);
                    }

                }
            }

            if (type.equalsIgnoreCase("origins:creative_flying")) {
                if (entity instanceof Player player) {
                    if (player.isFlying()) {
                        return getResult(inverted, true);
                    }

                }
            }

            if (type.equalsIgnoreCase("origins:daytime")) {
                if (entity.getWorld().isDayTime()) {
                    return getResult(inverted, true);
                }
            }

            if (type.equalsIgnoreCase("origins:dimension")) {
                if (entity.getWorld().getEnvironment().toString().equals(condition.get("dimension").toString().split(":")[1].replace("the_", "").toUpperCase())) {
                    return getResult(inverted, true);
                }
            }

            if (type.equalsIgnoreCase("origins:fluid_height")) {
                String fluidD = condition.get("fluid").toString();

                if (fluidD.equalsIgnoreCase("lava")) {
                    return getResult(inverted, entity.isInLava());
                } else if (fluidD.equalsIgnoreCase("water")) {
                    return getResult(inverted, entity.isInWaterOrBubbleColumn());
                }
            }

            if(type.equalsIgnoreCase("origins:invisible")){
                if(entity instanceof Player player){
                    return getResult(false, player.isInvisible() || p.getActivePotionEffects().contains(PotionEffectType.INVISIBILITY));
                }
            }

            if (type.equalsIgnoreCase("origins:in_rain")) {
                return getResult(inverted, entity.isInRain());
            }

            if (type.equalsIgnoreCase("origins:health")) {
                if (RestrictArmor.compareValues(p.getHealth(), condition.get("comparison").toString(), Double.parseDouble(condition.get("compare_to").toString()))) {
                    return getResult(inverted, true);
                }
            }

            if (type.equalsIgnoreCase("origins:exposed_to_sun")) {
                if ((p.getLocation().getBlockY() + 1 > p.getWorld().getHighestBlockYAt(p.getLocation()))) {
                    return getResult(inverted, p.getWorld().isDayTime());
                }
            }

            if (type.equalsIgnoreCase("origins:sneaking")) {
                return getResult(inverted, entity.isSneaking());
            }

            if (type.equalsIgnoreCase("origins:resource")) {
                    return getResult(inverted, !CooldownStuff.isPlayerInCooldownFromTag(p, origin.getPowerFileFromType(powerfile).getTag()));
            }

            if (type.equalsIgnoreCase("origins:fall_flying")){
                if(entity instanceof Player player){
                    if(player.isGliding() || FlightElytra.getGlidingPlayers().contains(player)){
                        if(player.getVelocity().getY() < 0 && !player.isOnGround()){
                            return getResult(inverted, true);
                        }
                    }
                }
            }

            if(type.equalsIgnoreCase("origins:submerged_in")){
                if(condition.get("fluid").equals("minecraft:water")){
                    p.sendMessage(String.valueOf(entity.getLocation().getBlock().getType().equals(Material.WATER)) + " waternstff");
                    if(entity.getLocation().getBlock().getType().equals(Material.WATER)) return getResult(inverted, true);
                }else if (condition.get("fluid").equals("minecraft:lava")) {
                    if(entity.getLocation().getBlock().getType().equals(Material.LAVA)) return getResult(inverted, true);
                }
            }

            if (type.equalsIgnoreCase("origins:enchantment")) {
                if (entity instanceof Player player) {
                    String enchantmentNamespace = condition.get("enchantment").toString();
                    String comparison = condition.get("comparison").toString();
                    double compareTo = Double.parseDouble(condition.get("compare_to").toString());

                    for (ItemStack item : player.getInventory().getArmorContents()) {
                        if (item == null) continue;

                        Enchantment enchantment = getEnchantmentByNamespace(enchantmentNamespace);

                        if (enchantment != null) {
                            if (item.containsEnchantment(enchantment)) {
                                int enchantmentLevel = item.getEnchantmentLevel(enchantment);

                                if (RestrictArmor.compareValues(enchantmentLevel, comparison, compareTo)) {
                                    p.sendMessage("true enchants");
                                    return getResult(inverted, true);
                                }
                            } else {
                                if (Double.compare(compareTo, 0.0) == 0 && comparison.equals("==")) {
                                    return getResult(inverted, !item.containsEnchantment(enchantment));
                                }
                            }
                        } else {
                            if(compareTo == 0 && comparison == "==") return getResult(inverted, true);
                            p.sendMessage("Enchantment not found");
                        }
                    }
                }
            }

            if(type.equalsIgnoreCase("origins:on_block")){
                return getResult(inverted, entity.isOnGround());
            }

            if(type.equalsIgnoreCase("origins:on_fire")){
                return getResult(inverted, entity.isVisualFire());
            }

            return getResult(inverted, false);
    }
}
