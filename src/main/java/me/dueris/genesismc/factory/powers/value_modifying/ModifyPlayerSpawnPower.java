package me.dueris.genesismc.factory.powers.value_modifying;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.ArrayList;

import static me.dueris.genesismc.factory.powers.value_modifying.ValueModifyingSuperClass.modify_world_spawn;
import static org.bukkit.Material.OBSIDIAN;

public class ModifyPlayerSpawnPower extends CraftPower implements Listener {

    @Override
    public void setActive(String tag, Boolean bool) {
        powers_active.put(tag, bool); // Simplified code
    }

    public ModifyPlayerSpawnPower() {

    }

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void runD(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        if (modify_world_spawn.contains(p)) {
            if(e.getRespawnReason().equals(PlayerRespawnEvent.RespawnReason.END_PORTAL)) return;
            if(e.getRespawnReason().equals(PlayerRespawnEvent.RespawnReason.PLUGIN)) return;
            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                ConditionExecutor executor = new ConditionExecutor();
                if (executor.check("condition", "conditions", p, origin, getPowerFile(), p, null, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                    String dimension = origin.getPowerFileFromType("origins:modify_player_spawn").get("dimension", null);
                    Location spawnLocation;

                    if ("nether".equals(dimension)) {
                        spawnLocation = NetherSpawn(origin.getPowerFileFromType("origins:modify_player_spawn").get("spawn_strategy", "default"));
                    } else if ("the_end".equals(dimension)) {
                        spawnLocation = EndSpawn(origin.getPowerFileFromType("origins:modify_player_spawn").get("spawn_strategy", "default"));
                    } else {
                        spawnLocation = OverworldSpawn(origin.getPowerFileFromType("origins:modify_player_spawn").get("spawn_strategy", "default"));
                    }

                    if (spawnLocation != null) {
                        p.teleportAsync(spawnLocation);
                    }
                } else {
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                }
            }
        }
    }

    public boolean isInsideBorder(Block block) {
        WorldBorder border = block.getWorld().getWorldBorder();
        double radius = border.getSize() / 2;
        Location location = block.getLocation(), center = border.getCenter();

        return center.distanceSquared(location) >= (radius * radius);
    }

    public Location NetherSpawn(String spawn_strategy) {
        for (World world : Bukkit.getWorlds()) {
            if (world.getEnvironment() == World.Environment.NETHER) {
                if ("default".equals(spawn_strategy)) {
                    World overworld = Bukkit.getWorlds().get(0);
                    if (overworld.getEnvironment() != World.Environment.NORMAL) {
                        return null;
                    }

                    Location overworldSpawn = overworld.getSpawnLocation();
                    double netherX = overworldSpawn.getX() / 8.0;
                    double netherZ = overworldSpawn.getZ() / 8.0;
                    double netherY = overworldSpawn.getY();

                    World netherWorld = Bukkit.getWorlds().stream()
                            .filter(world1 -> world1.getEnvironment() == World.Environment.NETHER)
                            .findFirst().orElse(null);
                    if (netherWorld != null) {
                        Location netherLocation = new Location(netherWorld, netherX, netherY, netherZ);
                        if (netherLocation.getBlock().getType() != Material.AIR) {
                            Location spawnPlatformLocation = new Location(netherWorld, netherX, netherY, netherZ);
                            createSpawnPlatform(spawnPlatformLocation);
                        }
                        return new Location(netherWorld, netherX, netherY, netherZ);
                    } else {
                        return null;
                    }
                } else if ("center".equals(spawn_strategy)) {
                    Location spawnLocation = new Location(world, 0, 0, 0);
                    for (int y = 255; y >= 0; y--) {
                        spawnLocation.setY(y);
                        Block block = spawnLocation.getBlock();

                        if (block.getType() != Material.BEDROCK && block.getType().isSolid()) {
                            return spawnLocation.clone().add(0.5, 1, 0.5);
                        }
                    }

                    Location platformLocation = new Location(world, 0, 70, 0);
                    createSpawnPlatform(platformLocation);
                    return platformLocation.clone().add(0.5, 1, 0.5);

                } else if ("closest_available".equals(spawn_strategy)) {
                    int centerX = 0;
                    int centerY = 70;
                    int centerZ = 0;

                    for (int distance = 0; distance <= 100; distance++) {
                        for (int x = centerX - distance; x <= centerX + distance; x++) {
                            for (int z = centerZ - distance; z <= centerZ + distance; z++) {
                                yLoop:
                                for (int y = centerY; y < centerY + 68; y++) {
                                    Location currentLocation = new Location(world, x, y, z);
                                    if (currentLocation.getBlock().getType() != Material.AIR) continue;

                                    Location aboveLocation = currentLocation.clone().add(0, 1, 0);
                                    if (aboveLocation.getBlock().getType() != Material.AIR) continue;

                                    Material blockBeneath = currentLocation.clone().subtract(0, 1, 0).getBlock().getType();
                                    if (blockBeneath == Material.AIR || blockBeneath == Material.LAVA || blockBeneath == Material.FIRE || blockBeneath == Material.SOUL_FIRE)
                                        continue;

                                    for (int offsetX = -2; offsetX <= 2; offsetX++) {
                                        for (int offsetY = 0; offsetY <= 1; offsetY++) {
                                            for (int offsetZ = -2; offsetZ <= 2; offsetZ++) {
                                                Location potentialLocation = currentLocation.clone().add(offsetX, offsetY, offsetZ);
                                                if (potentialLocation.getBlock().getType() != Material.AIR || isInsideBorder(potentialLocation.getBlock()))
                                                    continue yLoop;
                                            }
                                        }
                                    }
                                    return currentLocation.clone().add(0.5, 0, 0.5);
                                }
                            }
                        }
                    }
                }
                break;
            }
        }
        return null;
    }

    public Location EndSpawn(String spawn_strategy) {
        for (World world : Bukkit.getWorlds()) {
            if (world.getEnvironment() == World.Environment.THE_END) {
                if ("default".equals(spawn_strategy)) {
                    return Bukkit.getWorlds().get(2).getSpawnLocation();
                } else if ("center".equals(spawn_strategy)) {
                    Location spawnLocation = new Location(world, 0, 0, 0);
                    for (int y = 255; y >= 0; y--) {
                        spawnLocation.setY(y);
                        Block block = spawnLocation.getBlock();

                        if (block.getType().isSolid()) {
                            return spawnLocation.clone().add(0.5, 1, 0.5);
                        }
                    }

                    Location platformLocation = new Location(world, 0, 70, 0);
                    createSpawnPlatform(platformLocation);
                    return platformLocation.clone().add(0.5, 1, 0.5);

                } else if ("closest_available".equals(spawn_strategy)) {
                    int centerX = 0;
                    int centerY = 70;
                    int centerZ = 0;

                    for (int distance = 0; distance <= 100; distance++) {
                        for (int x = centerX - distance; x <= centerX + distance; x++) {
                            for (int z = centerZ - distance; z <= centerZ + distance; z++) {
                                yLoop:
                                for (int y = centerY; y < centerY + 68; y++) {
                                    Location currentLocation = new Location(world, x, y, z);
                                    if (currentLocation.getBlock().getType() != Material.AIR) continue;

                                    Location aboveLocation = currentLocation.clone().add(0, 1, 0);
                                    if (aboveLocation.getBlock().getType() != Material.AIR) continue;

                                    Material blockBeneath = currentLocation.clone().subtract(0, 1, 0).getBlock().getType();
                                    if (blockBeneath == Material.AIR || blockBeneath == Material.LAVA || blockBeneath == Material.FIRE || blockBeneath == Material.SOUL_FIRE)
                                        continue;

                                    for (int offsetX = -2; offsetX <= 2; offsetX++) {
                                        for (int offsetY = 0; offsetY <= 1; offsetY++) {
                                            for (int offsetZ = -2; offsetZ <= 2; offsetZ++) {
                                                Location potentialLocation = currentLocation.clone().add(offsetX, offsetY, offsetZ);
                                                if (potentialLocation.getBlock().getType() != Material.AIR || isInsideBorder(potentialLocation.getBlock()))
                                                    continue yLoop;
                                            }
                                        }
                                    }
                                    return currentLocation.clone().add(0.5, 0, 0.5);
                                }
                            }
                        }
                    }
                }
                break;
            }
        }
        return null;
    }

    public Location OverworldSpawn(String spawn_strategy) {
        for (World world : Bukkit.getWorlds()) {
            if (world.getEnvironment() == World.Environment.NORMAL) {
                if ("default".equals(spawn_strategy)) {
                    return Bukkit.getWorlds().get(0).getSpawnLocation();
                } else if ("center".equals(spawn_strategy)) {
                    Location spawnLocation = new Location(world, 0, 0, 0);
                    for (int y = 255; y >= 0; y--) {
                        spawnLocation.setY(y);
                        Block block = spawnLocation.getBlock();

                        if (block.getType().isSolid()) {
                            return spawnLocation.clone().add(0.5, 1, 0.5);
                        }
                    }

                    Location platformLocation = new Location(world, 0, 70, 0);
                    createSpawnPlatform(platformLocation);
                    return platformLocation.clone().add(0.5, 1, 0.5);

                } else if ("closest_available".equals(spawn_strategy)) {
                    int centerX = 0;
                    int centerY = 70;
                    int centerZ = 0;

                    for (int distance = 0; distance <= 100; distance++) {
                        for (int x = centerX - distance; x <= centerX + distance; x++) {
                            for (int z = centerZ - distance; z <= centerZ + distance; z++) {
                                yLoop:
                                for (int y = centerY; y < centerY + 68; y++) {
                                    Location currentLocation = new Location(world, x, y, z);
                                    if (currentLocation.getBlock().getType() != Material.AIR) continue;

                                    Location aboveLocation = currentLocation.clone().add(0, 1, 0);
                                    if (aboveLocation.getBlock().getType() != Material.AIR) continue;

                                    Material blockBeneath = currentLocation.clone().subtract(0, 1, 0).getBlock().getType();
                                    if (blockBeneath == Material.AIR || blockBeneath == Material.LAVA || blockBeneath == Material.FIRE || blockBeneath == Material.SOUL_FIRE)
                                        continue;

                                    for (int offsetX = -2; offsetX <= 2; offsetX++) {
                                        for (int offsetY = 0; offsetY <= 1; offsetY++) {
                                            for (int offsetZ = -2; offsetZ <= 2; offsetZ++) {
                                                Location potentialLocation = currentLocation.clone().add(offsetX, offsetY, offsetZ);
                                                if (potentialLocation.getBlock().getType() != Material.AIR || isInsideBorder(potentialLocation.getBlock()))
                                                    continue yLoop;
                                            }
                                        }
                                    }
                                    return currentLocation.clone().add(0.5, 0, 0.5);
                                }
                            }
                        }
                    }
                }
                break;
            }
        }
        return null;
    }

    private void createSpawnPlatform(Location location) {
        World world = location.getWorld();

        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                Block block = world.getBlockAt(location.getBlockX() + x, location.getBlockY(), location.getBlockZ() + z);
                block.setType(OBSIDIAN);
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:modify_player_spawn";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_world_spawn;
    }
}
