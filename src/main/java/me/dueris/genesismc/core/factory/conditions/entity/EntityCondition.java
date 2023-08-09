package me.dueris.genesismc.core.factory.conditions.entity;

import me.dueris.genesismc.core.factory.powers.OriginsMod.player.RestrictArmor;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class EntityCondition {

    public static String check(String thinger, Player p, OriginContainer origin, String powerfile, Entity entity) {
        if (origin.getPowerFileFromType(powerfile).getConditionFromString(thinger) == null) return "null";
        if (origin.getPowerFileFromType(powerfile).getConditionFromString(thinger).get("type") == null) return "null";
        p.sendMessage("entity_start");
        String type = origin.getPowerFileFromType(powerfile).getConditionFromString(thinger).get("type").toString();
        if (type.equalsIgnoreCase("origins:ability")) {
            String ability = origin.getPowerFileFromType(powerfile).getConditionFromString(thinger).get("ability").toString();
            if (ability.equalsIgnoreCase("minecraft:flying")) {
                if (entity instanceof Player player) {
                    if (player.isFlying()) return "true";
                }
            }
            if (ability.equalsIgnoreCase("minecraft:instabuild")) {
                if (entity instanceof Player player) {
                    if (player.getGameMode().equals(GameMode.CREATIVE)) return "true";
                }
            }
            if (ability.equalsIgnoreCase("minecraft:invuln" +
                    "rable")) {
                if (entity.isInvulnerable()) return "true";
            }
            if (ability.equalsIgnoreCase("minecraft:maybuild")) {
                if (entity.hasPermission("minecraft.build")) {
                    return "true";
                }
            }
            if (ability.equalsIgnoreCase("minecraft:mayfly")) {
                if (entity instanceof Player player) {
                    if (player.getAllowFlight()) return "true";
                }
            }
        }

        if (type.equalsIgnoreCase("origins:advancement")) {
            String advancementString = origin.getPowerFileFromType(powerfile).getConditionFromString(thinger).get("advancement").toString();
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
                            if (done != null) {
                                if (done.toString() == "true") {
                                    return "true";
                                }
                            } else {
                                return "false";
                            }
                        } else {
                            return "false";
                        }
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    return "false";
                }
            }
        }

        if (type.equalsIgnoreCase("origins:air")) {
            if (entity instanceof Player player) {
                if (RestrictArmor.compareValues(player.getRemainingAir(), origin.getPowerFileFromType(powerfile).getConditionFromString(thinger).get("comparison").toString(), Integer.valueOf(origin.getPowerFileFromType(powerfile).getConditionFromString(thinger).get("compare_to").toString()))) {
                    return "true";
                }
            }
        }

        if (type.equalsIgnoreCase("origins:attribute")) {
            if (entity instanceof Player player) {
                String attributeString = origin.getPowerFileFromType(powerfile).getConditionFromString(thinger).get("attribute").toString().split(":")[1].replace(".", "_").toUpperCase();
                if (RestrictArmor.compareValues(player.getAttribute(Attribute.valueOf(attributeString)).getValue(), origin.getPowerFileFromType(powerfile).getConditionFromString(thinger).get("comparison").toString(), Integer.valueOf(origin.getPowerFileFromType(powerfile).getConditionFromString(thinger).get("compare_to").toString()))) {
                    return "true";
                }
            }
        }

        // TODO: continue entity_condition to use biome condition for origins:biome in some cases. see https://origins.readthedocs.io/en/latest/types/entity_condition_types/biome/

        if (type.equalsIgnoreCase("origins:biome")) {
            String biomeString = origin.getPowerFileFromType(powerfile).getConditionFromString(thinger).get("biome").toString().split(":")[1].replace(".", "_").toUpperCase();
            if (entity.getLocation().getBlock().getBiome().equals(Biome.valueOf(biomeString))) {
                return "true";
            }
        }

        if (type.equalsIgnoreCase("origins:block_collision")) {
            // TODO: add block_condition check for origins:block_collision. see https://origins.readthedocs.io/en/latest/types/entity_condition_types/block_collision/
            String offsetX = origin.getPowerFileFromType(powerfile).getConditionFromString(thinger).get("offset_x").toString();
            String offsetY = origin.getPowerFileFromType(powerfile).getConditionFromString(thinger).get("offset_y").toString();
            String offsetZ = origin.getPowerFileFromType(powerfile).getConditionFromString(thinger).get("offset_z").toString();
            if (entity instanceof Player player) {
                Location playerLocation = player.getLocation();
                World world = player.getWorld();

                int blockX = playerLocation.getBlockX() + Integer.parseInt(offsetX);
                int blockY = playerLocation.getBlockY() + Integer.parseInt(offsetY);
                int blockZ = playerLocation.getBlockZ() + Integer.parseInt(offsetZ);

                Block block = world.getBlockAt(blockX, blockY, blockZ);

                if (block.getType() != Material.AIR) {
                    return "true";
                }
            }
        }

        if (type.equalsIgnoreCase("origins:block_in_radius")) {
            // TODO: add block_condition check for origins:block_collision. see https://origins.readthedocs.io/en/latest/types/entity_condition_types/block_collision/
            Integer radius = Math.toIntExact((Long) origin.getPowerFileFromType(powerfile).getConditionFromString(thinger).get("radius"));
            String shape = origin.getPowerFileFromType(powerfile).getConditionFromString(thinger).get("shape").toString();
            String comparison = origin.getPowerFileFromType(powerfile).getConditionFromString(thinger).get("comparison").toString();
            Integer compare_to = Integer.valueOf(origin.getPowerFileFromType(powerfile).getConditionFromString(thinger).get("compare_to").toString());

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

            if (RestrictArmor.compareValues(blockCount, comparison, compare_to)) {
                return "true";
            }
        }

        if (type.equalsIgnoreCase("origins:brightness")) {
            String comparison = origin.getPowerFileFromType(powerfile).getConditionFromString(thinger).get("comparison").toString();
            Double compare_to = Double.valueOf(origin.getPowerFileFromType(powerfile).getConditionFromString(thinger).get("compare_to").toString());
            double brightness = 0;
            int lightLevel = entity.getLocation().getBlock().getLightLevel();
            int ambientLight = 0;

            //calculate ambient light
            if (entity.getWorld() == Bukkit.getServer().getWorlds().get(0)) { //is overworld
                ambientLight = 0;
            } else if (entity.getWorld() == Bukkit.getServer().getWorlds().get(2)) {
                ambientLight = 1;
            }
            brightness = ambientLight + (1 - ambientLight) * lightLevel / (60 - 3 * lightLevel);

            if (RestrictArmor.compareValues(brightness, comparison, compare_to)) {
                return "true";
            }
        }

        if (type.equalsIgnoreCase("origins:climbing")) {
            if (entity instanceof Player player) {
                if (player.isClimbing()) {
                    return "true";
                }
            }
        }

        if (type.equalsIgnoreCase("origins:collided_horizontally")) {
            if (entity instanceof Player player) {
                Block block = player.getLocation().getBlock();
                BoundingBox playerBoundingBox = player.getBoundingBox();
                BoundingBox blockBoundingBox = block.getBoundingBox();

                Location center = player.getLocation();

                World world = center.getWorld();
                double x = center.getX();
                double y = center.getY();
                double z = center.getZ();

                BoundingBox boundingBox = block.getBoundingBox();
                if (boundingBox.overlaps(playerBoundingBox)) {
                    return "true";
                }
            }
        }

        if (type.equalsIgnoreCase("origins:creative_flying")) {
            if (entity instanceof Player player) {
                if (player.isFlying()) {
                    return "true";
                }
            }
        }

        if (type.equalsIgnoreCase("origins:daytime")) {
            if (entity.getWorld().isDayTime()) {
                return "true";
            }
        }

        if (type.equalsIgnoreCase("origins:dimension")) {
            if (origin.getPowerFileFromType(powerfile).getConditionFromString(thinger).get("inverted").toString() == "true") {
                if (!entity.getWorld().getEnvironment().equals(origin.getPowerFileFromType(powerfile).getConditionFromString(thinger).get("dimension").toString().split(":")[1].replace("the_", "").toUpperCase())) {
                    return "true";
                }
            } else {
                if (entity.getWorld().getEnvironment().equals(origin.getPowerFileFromType(powerfile).getConditionFromString(thinger).get("dimension").toString().split(":")[1].replace("the_", "").toUpperCase())) {
                    return "true";
                }
            }

        }

        return "false";
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
