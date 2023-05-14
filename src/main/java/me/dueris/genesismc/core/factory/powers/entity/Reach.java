package me.dueris.genesismc.core.factory.powers.entity;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
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

public class Reach  implements Listener {

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
                if(entity instanceof Projectile) return;
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
}
