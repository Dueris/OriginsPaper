package me.dueris.genesismc.core.factory.powers.entity;

import me.dueris.genesismc.core.api.entity.OriginPlayer;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

import static me.dueris.genesismc.core.factory.powers.Powers.extra_reach;

public class Reach implements Listener {

    /*
        @EventHandler
        public void SwingBlockBreakCreative(PlayerInteractEvent e){
            Player p = e.getPlayer();
            if(extra_reach.contains(OriginPlayer.getOriginTag(p))){
                if(p.getGameMode() != GameMode.CREATIVE) return;
                if(e.getAction().isLeftClick()) {
                    breakBlockInRange(p, 20);
                    e.setCancelled(true);
                }
            }

        }

        @EventHandler
        public void PlaceBlock(PlayerInteractEvent e){
            Player p = e.getPlayer();
            if(extra_reach.contains(OriginPlayer.getOriginTag(p))){
                if(p.getGameMode() == GameMode.SPECTATOR) return;
                if(e.getAction().isLeftClick()) {
                    placeClosestBlockInSight(p);
                    e.setCancelled(true);
                }
            }
        }

        @EventHandler
        public void SwingBlockBreakSurvival(PlayerArmSwingEvent e){
            Player p = e.getPlayer();
            if(getClosestBlockInSight(p, 6) == null) return;
        }
     */
    public static Block getClosestBlockInSight(Player player, double maxRange, double normalReach) {
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

    public static void placeClosestBlockInSight(Player player) {
        Block targetBlock = getClosestBlockInSight(player, 20, 5); // Set the desired maximum range

        if (targetBlock != null && targetBlock.getType() == Material.AIR) {
            ItemStack item = player.getInventory().getItemInMainHand();

            if (item.getType().isBlock()) {
                Block placedBlock = targetBlock.getRelative(player.getFacing());

                if (placedBlock.getType() == Material.AIR) {
                    placedBlock.setType(item.getType());

                    if (item.getAmount() > 1) {
                        item.setAmount(item.getAmount() - 1);
                    } else {
                        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                    }
                }
            }
        }
    }

    @EventHandler
    public void OnClickREACH(PlayerInteractEvent e) {
        if (extra_reach.contains(OriginPlayer.getOriginTag(e.getPlayer()))) {
            if (e.getAction().isLeftClick()) ;

            Player p = e.getPlayer();
            Location eyeloc = p.getEyeLocation();
            @NotNull Vector direction = eyeloc.getDirection();
            Predicate<Entity> filter = (entity) -> !entity.equals(p);

            RayTraceResult traceResult4_5F = p.getWorld().rayTrace(eyeloc, eyeloc.getDirection(), 6, FluidCollisionMode.NEVER, false, 0, filter);

            if (traceResult4_5F != null) {
                Entity entity = traceResult4_5F.getHitEntity();
                //entity code -- pvp
                if (entity == null) return;
                Player attacker = e.getPlayer();
                if (entity.isDead() || !(entity instanceof LivingEntity)) return;
                LivingEntity victim = (LivingEntity) traceResult4_5F.getHitEntity();
                if (attacker.getLocation().distance(victim.getLocation()) <= 6) {
                    if (entity.getPassengers().contains(p)) return;
                    if (!entity.isDead()) {
                        LivingEntity ent = (LivingEntity) entity;
                        p.attack(ent);
                    }
                }
            }
        }
    }

}
