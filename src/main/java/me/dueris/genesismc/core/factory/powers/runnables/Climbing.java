package me.dueris.genesismc.core.factory.powers.runnables;

import me.dueris.genesismc.core.api.entity.OriginPlayer;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

import static me.dueris.genesismc.core.GenesisMC.getPlugin;
import static me.dueris.genesismc.core.factory.powers.Powers.climbing;
import static org.bukkit.Material.AIR;

public class Climbing extends BukkitRunnable implements Listener {

    @Override
    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            if (climbing.contains(OriginPlayer.getOriginTag(p))) {
                if (p.getLocation().getBlock().getRelative(BlockFace.EAST).getType().isSolid() ||
                        p.getLocation().getBlock().getRelative(BlockFace.WEST).getType().isSolid() ||
                        p.getLocation().getBlock().getRelative(BlockFace.NORTH).getType().isSolid() ||
                        p.getLocation().getBlock().getRelative(BlockFace.SOUTH).getType().isSolid() ||
                        p.getEyeLocation().add(0, 1, 0).getBlock().getType().isSolid() ||
                        p.getEyeLocation().getBlock().getRelative(BlockFace.EAST).getType().isSolid() ||
                        p.getEyeLocation().getBlock().getRelative(BlockFace.WEST).getType().isSolid() ||
                        p.getEyeLocation().getBlock().getRelative(BlockFace.NORTH).getType().isSolid() ||
                        p.getEyeLocation().getBlock().getRelative(BlockFace.SOUTH).getType().isSolid()) {
                    Block block = p.getTargetBlock(null, 2);

                    if (block.getType() != AIR && p.isSneaking() && !p.isInRain()) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 6, 2, false, false, false));
                    }
                }
            }
        }
    }
    private Set<Player> holdingPlayers;
/*
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction().isRightClick()) {
            if (event.getItem() != null && !event.getItem().getType().isEdible()) {
                // Add the player to the set of holding players
                holdingPlayers.add(player);

                // Schedule a task to check if the player is still holding down right-click
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!player.isOnline() || !holdingPlayers.contains(player)) {
                            // Player is no longer online or no longer holding down right-click
                            cancel();
                            return;
                        }

                        // Check if the player is still holding down right-click
                        if (!player.isHandRaised()) {
                            // Player is no longer holding down right-click
                            holdingPlayers.remove(player);
                        }else{
                            player.teleportAsync(player.getLocation());
                        }
                    }
                }.runTaskTimer(getPlugin(), 0, 1); // Check every tick (1/20th of a second)
            }
        }
    }

 */
}
