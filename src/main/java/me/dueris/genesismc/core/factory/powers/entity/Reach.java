package me.dueris.genesismc.core.factory.powers.entity;

import io.papermc.paper.event.player.PlayerArmSwingEvent;
import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

import static me.dueris.genesismc.core.factory.powers.Powers.extra_reach;

public class Reach implements Listener {

    @EventHandler
    public void OnClickREACH(PlayerInteractEvent e) {
        PersistentDataContainer data = e.getPlayer().getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (extra_reach.contains(origintag)) {
            if (e.getAction().isLeftClick());

            Player p = e.getPlayer();
            Location eyeloc = p.getEyeLocation();
            @NotNull Vector direction = eyeloc.getDirection();
            Predicate<Entity> filter = (entity) -> !entity.equals(p);

            RayTraceResult traceResult4_5F = p.getWorld().rayTrace(eyeloc, eyeloc.getDirection(), 6, FluidCollisionMode.NEVER, false, 0, filter);

            if (traceResult4_5F != null) {
                Entity entity = traceResult4_5F.getHitEntity();
                //entity code -- pvp
                if (entity == null) return;
                Player attacker = (Player) e.getPlayer();
                if(entity.isDead() || !(entity instanceof LivingEntity)) return;
                LivingEntity victim = (LivingEntity) traceResult4_5F.getHitEntity();
                if(attacker.getLocation().distance(victim.getLocation()) <=6){
                    if (entity.getPassengers().contains(p)) return;
                    if (!entity.isDead()) {
                        LivingEntity ent = (LivingEntity) entity;
                        p.attack(ent);
                    }
                }
            }
        }
    }

    @EventHandler
    public void SwingBlockBreakCreative(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if(p.getGameMode() != GameMode.CREATIVE) return;
        if(e.getAction().isLeftClick()) {
            if (getClosestBlockInSight(p, 6) == null) return;
            getClosestBlockInSight(p, 6).breakNaturally(false, false);
        }
    }

    @EventHandler
    public void SwingBlockBreakSurvival(PlayerArmSwingEvent e){
        Player p = e.getPlayer();
        if(getClosestBlockInSight(p, 6) == null) return;
    }

    public Block getClosestBlockInSight(Player player, int range) {
        Location playerLocation = player.getEyeLocation();
        Location targetLocation = playerLocation.clone();

        for (int i = 0; i < range; i++) {
            targetLocation.add(playerLocation.getDirection());
            Block block = targetLocation.getBlock();
            if (!block.isEmpty()) {
                return block;
            }
        }

        return null;
    }

}
