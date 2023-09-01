package me.dueris.genesismc.factory.conditions.entity;

import me.dueris.genesismc.CooldownStuff;
import me.dueris.genesismc.entity.OriginPlayer;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

import static me.dueris.genesismc.factory.powers.player.RestrictArmor.compareValues;

public class EntityCondition {

    public static Optional<Boolean> check(HashMap<String, Object> condition, Player p, Entity entity, String powerfile) {
        // TODO: use inverted
        boolean inverted = (boolean) condition.getOrDefault("inverted", false);
        if (condition.get("type") == null) return Optional.empty();
        String type = condition.get("type").toString();

        if (type.equalsIgnoreCase("origins:ability")) {
            String ability = condition.get("ability").toString().toLowerCase();

            switch (ability) {
                case "minecraft:flying" -> {
                    if (entity instanceof Player player) {
                        return Optional.of(player.isFlying());
                    }
                }
                case "minecraft:instabuild" -> {
                    if (entity instanceof Player player) {
                        return Optional.of(player.getGameMode().equals(GameMode.CREATIVE));
                    }
                }
                case "minecraft:invulnerable" -> {
                    return Optional.of(entity.isInvulnerable());
                }
                case "minecraft:maybuild" -> {
                    return Optional.of(entity.hasPermission("minecraft.build"));
                }
                case "minecraft:mayfly" -> {
                    if (entity instanceof Player player) {
                        return Optional.of(player.getAllowFlight());
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
                            return Optional.of(Objects.requireNonNullElse(done, false));
                        } else {
                            return Optional.of(false);
                        }
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    return Optional.of(false);
                }
            }
        }

        if (type.equalsIgnoreCase("origins:air")) {
            if (entity instanceof Player player) {
                if (compareValues(player.getRemainingAir(), condition.get("comparison").toString(), Integer.parseInt(condition.get("compare_to").toString()))) {
                    return Optional.of(true);
                }
            }
        }

        if (type.equalsIgnoreCase("origins:attribute")) {
            if (entity instanceof Player player) {
                String attributeString = condition.get("attribute").toString().split(":")[1].replace(".", "_").toUpperCase();
                if (compareValues(player.getAttribute(Attribute.valueOf(attributeString)).getValue(), condition.get("comparison").toString(), Integer.parseInt(condition.get("compare_to").toString()))) {
                    return Optional.of(true);
                }
            }
        }

        if (type.equalsIgnoreCase("origins:biome")) {
            if (condition.get("biome") == null) return Optional.empty();
            String biomeString = condition.get("biome").toString().split(":")[1].replace(".", "_").toUpperCase();
            if (BiomeCondition.check(condition, entity, entity.getLocation().getBlock(), powerfile).equals(Optional.of(true))) {
                if (entity.getLocation().getBlock().getBiome().equals(Biome.valueOf(biomeString))) {
                    return Optional.of(true);
                }
            }
        }

        if (type.equalsIgnoreCase("origins:block_collision")) {
            String offsetX = condition.get("offset_x").toString();
            String offsetY = condition.get("offset_y").toString();
            String offsetZ = condition.get("offset_z").toString();
            if (entity instanceof Player player) {
                Location playerLocation = player.getLocation();
                World world = player.getWorld();

                int blockX = playerLocation.getBlockX() + Integer.parseInt(offsetX);
                int blockY = playerLocation.getBlockY() + Integer.parseInt(offsetY);
                int blockZ = playerLocation.getBlockZ() + Integer.parseInt(offsetZ);

                Block block = world.getBlockAt(blockX, blockY, blockZ);

                if (BlockCondition.check(condition, player, block, powerfile).equals(Optional.of(true))) {
                    if (block.getType() != Material.AIR) {
                        return Optional.of(true);
                    }
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

            if (shape.equalsIgnoreCase("sphere")) {
                blockCount = countBlocksInSphere(centerX, centerY, centerZ, radius, world);
            } else if (shape.equalsIgnoreCase("star")) {
                blockCount = countBlocksInStar(centerX, centerY, centerZ, radius, world);
            } else if (shape.equalsIgnoreCase("cube")) {
                blockCount = countBlocksInCube(minX, minY, minZ, maxX, maxY, maxZ, world);
            }
            if (compareValues(blockCount, comparison, compare_to)) {
                return Optional.of(true);
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
                return Optional.of(true);
            }

        }

        if (type.equalsIgnoreCase("origins:climbing")) {
            if (entity instanceof Player player) {
                if (player.isClimbing()) {
                    return Optional.of(true);
                }
                Climbing climbing = new Climbing();
                if (climbing.isActiveClimbing(player)) {
                    return Optional.of(true);
                }

            }
        }

        if (type.equalsIgnoreCase("origins:collided_horizontally")) {
            if (entity instanceof Player player) {
                Block block = player.getLocation().getBlock();
                BoundingBox playerBoundingBox = player.getBoundingBox();
                BoundingBox blockBoundingBox = block.getBoundingBox();

                if (blockBoundingBox.overlaps(playerBoundingBox)) {
                    return Optional.of(true);
                }

            }
        }

        if (type.equalsIgnoreCase("origins:creative_flying")) {
            if (entity instanceof Player player) {
                if (player.isFlying()) {
                    return Optional.of(true);
                }

            }
        }

        if (type.equalsIgnoreCase("origins:daytime")) {
            if (entity.getWorld().isDayTime()) {
                return Optional.of(true);
            }
        }

        if (type.equalsIgnoreCase("origins:dimension")) {
            if (entity.getWorld().getEnvironment().toString().equals(condition.get("dimension").toString().split(":")[1].replace("the_", "").toUpperCase())) {
                return Optional.of(true);
            }
        }

        if (type.equalsIgnoreCase("origins:fluid_height")) {
            String fluid = condition.get("fluid").toString();

            if (fluid.equalsIgnoreCase("lava")) {
                return Optional.of(entity.isInLava());
            } else if (fluid.equalsIgnoreCase("water")) {
                return Optional.of(entity.isInWaterOrBubbleColumn());
            }
        }

        if (type.equalsIgnoreCase("origins:in_rain")) {
            return Optional.of(entity.isInRain());
        }

        if (type.equalsIgnoreCase("origins:health")) {
            if (RestrictArmor.compareValues(p.getHealth(), condition.get("comparison").toString(), Double.parseDouble(condition.get("compare_to").toString()))) {
                return Optional.of(true);
            }
        }

        if (type.equalsIgnoreCase("origins:exposed_to_sun")) {
            if ((p.getLocation().getBlockY() + 1 > p.getWorld().getHighestBlockYAt(p.getLocation()))) {
                if (p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE) {
                    return Optional.of(p.getWorld().isDayTime());
                }
            }
        }

        if (type.equalsIgnoreCase("origins:sneaking")) {
            return Optional.of(entity.isSneaking());
        }

        if (type.equalsIgnoreCase("origins:resource")) {
            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                return Optional.of(!CooldownStuff.isPlayerInCooldownFromTag(p, origin.getPowerFileFromType(powerfile).getTag()));
            }
        }

        if (type.equalsIgnoreCase("origins:fall_flying")){
            if(entity instanceof Player player){
                if(player.isGliding() || FlightElytra.getGlidingPlayers().contains(player)){
                    if(player.getVelocity().getY() < 0 && !player.isOnGround()){
                        return Optional.of(true);
                    }
                }
            }
        }

        if(type.equalsIgnoreCase("origins:submerged_in")){
            if(condition.get("fluid").equals("minecraft:water")){
                if(entity.getLocation().getBlock().getType().equals(Material.WATER)) return Optional.of(true);
            }else if (condition.get("fluid").equals("minecraft:lava")) {
                if(entity.getLocation().getBlock().getType().equals(Material.LAVA)) return Optional.of(true);
            }
        }

        if(type.equalsIgnoreCase("origins:enchantment")){
            if(entity instanceof Player player){
                for(ItemStack item : player.getInventory().getArmorContents()){
                    if(item == null) continue;
                    if(condition.get("compare_to").toString() == "0" && condition.get("comparison").toString() == "==") return Optional.of(!item.containsEnchantment(getEnchantmentByNamespace(condition.get("enchantment").toString())));
                    if(item.containsEnchantment(getEnchantmentByNamespace(condition.get("enchantment").toString()))){
                        if(RestrictArmor.compareValues(item.getEnchantmentLevel(getEnchantmentByNamespace(condition.get("enchantment").toString())), condition.get("comparison").toString(), Double.parseDouble(condition.get("compare_to").toString()))){
                            return Optional.of(true);
                        }
                    }
                }
            }
        }

        if(type.equalsIgnoreCase("origins:on_block")){
            return Optional.of(entity.isOnGround());
        }
        return Optional.of(false);
    }

    public static Enchantment getEnchantmentByNamespace(String namespaceString) {
        return Enchantment.getByName(namespaceString);
    }

    private static int countBlocksInCube(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, World world) {
        int blockCount = 0;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location location = new Location(world, x, y, z);
                    Block block = location.getBlock();
                    if (block.getType() != Material.AIR) {
                        blockCount++;
                    }
                }
            }
        }

        return blockCount;
    }

    private static int countBlocksInStar(int centerX, int centerY, int centerZ, int radius, World world) {
        int blockCount = 0;

        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int y = centerY - radius; y <= centerY + radius; y++) {
                for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                    double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) + Math.pow(z - centerZ, 2));

                    if (distance <= radius && distance >= radius / 2) {
                        Location location = new Location(world, x, y, z);
                        Block block = location.getBlock();

                        if (block.getType() != Material.AIR) {
                            blockCount++;
                        }
                    }
                }
            }
        }

        return blockCount;
    }

    public static int countBlocksInSphere(int centerX, int centerY, int centerZ, int radius, World world) {
        int blockCount = 0;
        int squaredRadius = radius * radius;

        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int y = centerY - radius; y <= centerY + radius; y++) {
                for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                    if ((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY) + (z - centerZ) * (z - centerZ) <= squaredRadius) {
                        Location location = new Location(world, x, y, z);
                        if (location.getBlock().getType() != Material.AIR) {
                            blockCount++;
                        }
                    }
                }
            }
        }

        return blockCount;
    }


}
