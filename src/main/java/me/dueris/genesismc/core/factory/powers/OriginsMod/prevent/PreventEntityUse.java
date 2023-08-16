package me.dueris.genesismc.core.factory.powers.OriginsMod.prevent;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.core.factory.powers.OriginsMod.player.attributes.AttributeHandler;
import me.dueris.genesismc.core.utils.Lang;
import me.dueris.genesismc.core.utils.OriginContainer;
import me.dueris.genesismc.core.utils.PowerContainer;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.RayTraceResult;

import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;

import static me.dueris.genesismc.core.factory.powers.OriginsMod.player.attributes.AttributeHandler.Reach.getDefaultReach;
import static me.dueris.genesismc.core.factory.powers.OriginsMod.player.attributes.AttributeHandler.Reach.getFinalReach;
import static me.dueris.genesismc.core.factory.powers.OriginsMod.player.attributes.AttributeHandler.getOperationMappingsDouble;
import static me.dueris.genesismc.core.factory.powers.OriginsMod.prevent.PreventSuperClass.prevent_entity_use;
import static me.dueris.genesismc.core.factory.powers.Powers.extra_reach_attack;

public class PreventEntityUse implements Listener {
    @EventHandler
    public void OnClickREACH(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (prevent_entity_use.contains(e.getPlayer())) {
            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {

                    Location eyeloc = p.getEyeLocation();
                    Predicate<Entity> filter = (entity) -> !entity.equals(p);
                    RayTraceResult traceResult4_5F = p.getWorld().rayTrace(eyeloc, eyeloc.getDirection(), getFinalReach(p), FluidCollisionMode.NEVER, false, 0, filter);

                    if (traceResult4_5F != null) {
                        Entity entity = traceResult4_5F.getHitEntity();
                        if (entity == null) return;
                        Player attacker = p;
                        if (entity.isDead() || !(entity instanceof LivingEntity)) return;
                        if (entity.isInvulnerable()) return;
                        LivingEntity victim = (LivingEntity) traceResult4_5F.getHitEntity();
                        if (attacker.getLocation().distance(victim.getLocation()) <= AttributeHandler.Reach.getFinalReach(p)) {
                            if (entity.getPassengers().contains(p)) return;
                            if (!entity.isDead()) {
                                LivingEntity ent = (LivingEntity) entity;
                                if(ConditionExecutor.check("bientity_condition", p, origin, "origins:prevent_entity_use", null, ent)){
                                    if(ConditionExecutor.check("item_condition", p, origin, "origins:prevent_entity_use", null, ent)){
                                        e.setCancelled(true);
                                    }
                                }
                            }
                        } else {
                            e.setCancelled(true);
                        }
                    }
            }
        }
    }
}
