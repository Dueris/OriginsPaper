package me.dueris.genesismc.core.origins.enderian;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static java.util.stream.IntStream.range;

public class EnderReach implements Listener {

    @EventHandler
    public void OnClickREACH(PlayerArmSwingEvent e) {
        PersistentDataContainer data = e.getPlayer().getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (origintag.equalsIgnoreCase("genesis:origin-enderian")) {
            if (e.getAction().isLeftClick());

            Player p = e.getPlayer();
            Location eyeloc = p.getEyeLocation();
            @NotNull Vector direction = eyeloc.getDirection();
            Predicate<Entity> filter = (entity) -> !entity.equals(p);
            LivingEntity enttarg = p.getTargetEntity(6, false);
            if(enttarg != null){
               p.attack(enttarg);
            }
            /*
            RayTraceResult traceResult4_5F = p.getWorld().rayTrace(eyeloc, eyeloc.getDirection(), 6, FluidCollisionMode.NEVER, false, 0, filter);

            if (traceResult4_5F != null) {
                Entity entity = traceResult4_5F.getHitEntity();
                //entity code -- pvp
                if (entity == null) return;
                Player attacker = (Player) e.getPlayer();
                LivingEntity victim = (LivingEntity) traceResult4_5F.getHitEntity();
                if(attacker.getLocation().distance(victim.getLocation()) <=5){

                if (entity.getPassengers().contains(p)) return;
                if (!entity.isDead()) {
                    LivingEntity ent = (LivingEntity) entity;

                    if (e.getPlayer().getInventory().getItemInMainHand() != null) {
                        ItemStack itm = e.getPlayer().getInventory().getItemInMainHand();
                        p.attack(ent);
                    } else {
                        p.attack(ent);
                    }
                }
            }
            }
            */
        }
    }

}


