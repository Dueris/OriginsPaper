package me.dueris.genesismc.core.factory.powers.OriginsMod.world;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Random;

import static me.dueris.genesismc.core.factory.powers.Powers.nether_spawn;
import static org.bukkit.Material.*;

public class WorldSpawnHandler implements Listener {
    public static boolean isInsideBorder(Block block) {
        WorldBorder border = block.getWorld().getWorldBorder();
        double radius = border.getSize() / 2;
        Location location = block.getLocation(), center = border.getCenter();

        return center.distanceSquared(location) >= (radius * radius);
    }

    /*
    public void initiate() {
        this.logger.info("Preparing spawns...");

        var world = Bukkit.getWorld("world");
        if (world == null) throw new IllegalStateException("The world does not exist");

        var bedrock = Material.BEDROCK.createBlockData();
        var doneCount = new AtomicInteger();
        var future = new CompletableFuture<Void>();

        var spawns = this.plugin.getSpawnsConfig().spawns();
        for (int i = 0; i < spawns.length; ++i) {
            var vec = spawns[i];
            var i1 = i;
            Bukkit.getRegionScheduler().execute(this.plugin, world, vec.x() >> 4, vec.z() >> 4, () -> {
                var y = world.getHighestBlockYAt(vec.x(), vec.z());
                world.setBlockData(vec.x(), y, vec.z(), bedrock);

                var spawn = new Location(world, vec.x() + 0.5, y + 1, vec.z() + 0.5);
                this.spawns[i1] = spawn;

                if (doneCount.incrementAndGet() == spawns.length) {
                    future.complete(null);
                }
            });
        }

        future.thenAccept((v) -> {
            this.ready = true;
            this.logger.info("Spawns prepared");
        });
    }
     */

    public static Location NetherSpawn() {
        for (World world : Bukkit.getWorlds()) {
            if (world.getEnvironment() == World.Environment.NETHER) {

                Random random = new Random();
                Location location = new Location(world, random.nextInt(-150, 150), 32, random.nextInt(-150, 150));

                for (int x = (int) (location.getX() - 100); x < location.getX() + 100; x++) {
                    for (int z = (int) (location.getZ() - 100); z < location.getZ() + 100; z++) {
                        yLoop:
                        for (int y = (int) (location.getY()); y < location.getY() + 68; y++) {
                            if (new Location(world, x, y, z).getBlock().getType() != AIR) continue;
                            if (new Location(world, x, y + 1, z).getBlock().getType() != AIR) continue;
                            Material blockBeneath = new Location(world, x, y - 1, z).getBlock().getType();
                            if (blockBeneath == AIR || blockBeneath == LAVA || blockBeneath == FIRE || blockBeneath == SOUL_FIRE)
                                continue;

                            for (int potentialX = (int) (new Location(world, x, y, z).getX() - 2); potentialX < new Location(world, x, y, z).getX() + 2; potentialX++) {
                                for (int potentialY = (int) (new Location(world, x, y, z).getY()); potentialY < new Location(world, x, y, z).getY() + 2; potentialY++) {
                                    for (int potentialZ = (int) (new Location(world, x, y, z).getZ() - 2); potentialZ < new Location(world, x, y, z).getZ() + 2; potentialZ++) {
                                        if ((new Location(world, potentialX, potentialY, potentialZ).getBlock().getType() != AIR) || (isInsideBorder(new Location(world, potentialX, potentialY, potentialZ).getBlock())))
                                            continue yLoop;
                                    }
                                }
                            }
                            return (new Location(world, x + 0.5, y, z + 0.5));
                        }
                    }
                }
                break;
            }
        }
        return null;
    }

    @EventHandler
    public void netherSpawn(PlayerRespawnEvent e) {
        if (nether_spawn.contains(e.getPlayer())) {
            if (!(e.isBedSpawn() || e.isAnchorSpawn())) {
                Location spawnLocation = null;
                for (World world : Bukkit.getWorlds()) {
                    if (world.getEnvironment() == World.Environment.NETHER) {

                        Random random = new Random();
                        Location location = new Location(world, random.nextInt(-300, 300), 32, random.nextInt(-300, 300));

                        mainLoop:
                        for (int x = (int) (location.getX() - 100); x < location.getX() + 100; x++) {
                            for (int z = (int) (location.getZ() - 100); z < location.getZ() + 100; z++) {
                                yLoop:
                                for (int y = (int) (location.getY()); y < location.getY() + 68; y++) {
                                    if (new Location(world, x, y, z).getBlock().getType() != AIR) continue;
                                    if (new Location(world, x, y + 1, z).getBlock().getType() != AIR) continue;
                                    Material blockBeneath = new Location(world, x, y - 1, z).getBlock().getType();
                                    if (blockBeneath == AIR || blockBeneath == LAVA || blockBeneath == FIRE || blockBeneath == SOUL_FIRE)
                                        continue;

                                    for (int potentialX = (int) (new Location(world, x, y, z).getX() - 2); potentialX < new Location(world, x, y, z).getX() + 2; potentialX++) {
                                        for (int potentialY = (int) (new Location(world, x, y, z).getY()); potentialY < new Location(world, x, y, z).getY() + 2; potentialY++) {
                                            for (int potentialZ = (int) (new Location(world, x, y, z).getZ() - 2); potentialZ < new Location(world, x, y, z).getZ() + 2; potentialZ++) {
                                                if ((new Location(world, potentialX, potentialY, potentialZ).getBlock().getType() != AIR) || (isInsideBorder(new Location(world, potentialX, potentialY, potentialZ).getBlock())))
                                                    continue yLoop;
                                            }
                                        }
                                    }
                                    spawnLocation = (new Location(world, x + 0.5, y, z + 0.5));
                                    break mainLoop;
                                }
                            }
                        }
                        break;
                    }
                }
                if (spawnLocation == null) return;
                e.setRespawnLocation(spawnLocation);
            }

        }
    }

}
