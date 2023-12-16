package me.dueris.genesismc.factory.conditions.biEntity;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.conditions.Condition;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.Bukkit;
import org.bukkit.Fluid;
import org.bukkit.FluidCollisionMode;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Predicate;

import static me.dueris.genesismc.factory.conditions.ConditionExecutor.getResult;

public class BiEntityCondition implements Condition, Listener {

    @Override
    public String condition_type() {
        return "BIENTITY_CONDITION";
    }

    @Override
    public Optional<Boolean> check(HashMap<String, Object> condition, Player p, PowerContainer power, String powerfile, Entity actor, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent entityDamageEvent) {
        if (power == null) return Optional.empty();
        if (condition.isEmpty()) return Optional.empty();
        if (condition.get("type") == null) return Optional.empty();
        if (actor == null || target == null) return Optional.empty();
        boolean inverted = (boolean) condition.getOrDefault("inverted", false);
        String type = condition.get("type").toString().toLowerCase();
        switch (type) {
            case "origins:attack_target" -> {
                Bukkit.getLogger().warning("origins:attack_target is depreciated for the plugin, for more details msg Dueris");
            }
            case "origins:attacker" -> {
                if(!actor.getLastDamageCause().getEntity().isDead()){
                    return getResult(inverted, Optional.of(!in_countdown.contains(actor)));
                }
            }
            case "origins:can_see" -> {
                Predicate<Entity> filter = entity -> !entity.equals(p);

                RayTraceResult traceResult = p.getWorld().rayTrace(actor.getLocation(), actor.getLocation().getDirection(), 12, FluidCollisionMode.valueOf(condition.getOrDefault("fluid_handling", "none").toString()), false, 1, filter);

                if(traceResult != null){
                    if(traceResult.getHitEntity() != null){
                        Entity entity = traceResult.getHitEntity();
                        if (entity.isDead() || !(entity instanceof LivingEntity)) return getResult(inverted, Optional.of(false));
                        if (entity.isInvulnerable()) return getResult(inverted, Optional.of(false));
                        if (entity.getPassengers().contains(p)) return getResult(inverted, Optional.of(false));
                            return getResult(inverted, Optional.of(entity.equals(target)));
                    }
                }
            }
            case "origins:owner" -> {
                if(target instanceof Tameable tameable){
                        return getResult(inverted, Optional.of(tameable.getOwner().equals(actor)));
                }else{
                    return getResult(inverted, Optional.of(false));
                }
            }
            case "origins:riding_recursive" -> {
                if(actor.getPassengers().contains(target)){
                    return getResult(inverted, Optional.of(true));
                }else{
                    return getResult(inverted, Optional.of(false));
                }
            }
            case "origins:riding_root" -> {
                for(int i = 0; i < actor.getPassengers().toArray().length; i++){
                    if(actor.getPassengers().isEmpty()) return getResult(inverted, Optional.of(false));
                    if(actor.getPassengers().get(i) != null) {
                            return getResult(inverted, Optional.of(i == actor.getPassengers().toArray().length));
                    }else{
                        return getResult(inverted, Optional.of(false));
                    }
                }
            }
            case "origins:riding" -> {
                    return getResult(inverted, Optional.of(target.getPassengers().contains(actor)));
            }
            default -> {
                return getResult(inverted, Optional.empty());
            }
        }
        return getResult(inverted, Optional.empty());
    }

    ArrayList<Entity> in_countdown = new ArrayList<>();

    @EventHandler
    public void counter(EntityDamageByEntityEvent e){
        if(!e.getDamager().isDead()){
            in_countdown.add(e.getEntity());
            new BukkitRunnable() {
                @Override
                public void run() {
                    in_countdown.remove(e.getEntity());
                }
            }.runTaskLater(GenesisMC.getPlugin(), 100);
        }
    }
}
