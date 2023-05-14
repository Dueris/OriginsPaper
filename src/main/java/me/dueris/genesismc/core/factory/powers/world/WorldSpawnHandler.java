package me.dueris.genesismc.core.factory.powers.world;

import me.dueris.genesismc.core.GenesisMC;
import net.minecraft.data.worldgen.DimensionTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.dimension.DimensionType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

import static me.dueris.genesismc.core.factory.powers.Powers.nether_spawn;
import static org.bukkit.Material.*;

public class WorldSpawnHandler implements Listener {
    public static boolean isInsideBorder(Block block){
        WorldBorder border = block.getWorld().getWorldBorder();
        double radius = border.getSize() / 2;
        Location location = block.getLocation(), center = border.getCenter();

        return center.distanceSquared(location) >= (radius * radius);
    }
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
        Player p = e.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (nether_spawn.contains(origintag)) {
            if (!(e.isBedSpawn() || e.isAnchorSpawn())) {
                Location spawnLocation = null;
                for (World world : Bukkit.getWorlds()) {
                    if (world.getEnvironment() == World.Environment.NETHER) {

                        Random random = new Random();
                        Location location = new Location(world, random.nextInt(-300, 300), 32, random.nextInt(-300, 300));

                        for (int x = (int) (location.getX()-100); x < location.getX()+100; x++) {
                            for (int z = (int) (location.getZ()-100); z < location.getZ()+100; z++) {
                                yLoop:
                                for (int y = (int) (location.getY()); y < location.getY()+68; y++) {
                                    if (new Location(world, x, y, z).getBlock().getType() != AIR) continue;
                                    if (new Location(world, x, y+1, z).getBlock().getType() != AIR) continue;
                                    Material blockBeneath = new Location(world, x, y-1, z).getBlock().getType();
                                    if (blockBeneath == AIR || blockBeneath == LAVA || blockBeneath == FIRE || blockBeneath == SOUL_FIRE) continue;

                                    for (int potentialX = (int) (new Location(world, x, y, z).getX()-2); potentialX < new Location(world, x, y, z).getX()+2; potentialX++) {
                                        for (int potentialY = (int) (new Location(world, x, y, z).getY()); potentialY < new Location(world, x, y, z).getY()+2; potentialY++) {
                                            for (int potentialZ = (int) (new Location(world, x, y, z).getZ()-2); potentialZ < new Location(world, x, y, z).getZ()+2; potentialZ++) {
                                                if ((new Location(world, potentialX, potentialY, potentialZ).getBlock().getType() != AIR) || (isInsideBorder(new Location(world, potentialX, potentialY, potentialZ).getBlock()))) continue yLoop;
                                            }
                                        }
                                    }
                                    spawnLocation = (new Location(world, x+0.5, y, z+0.5));
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
