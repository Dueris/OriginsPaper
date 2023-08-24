package me.dueris.genesismc.factory.powers.OriginsMod.prevent;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.OriginsMod.player.attributes.AttributeHandler;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.RayTraceResult;

import java.util.ArrayList;
import java.util.function.Predicate;

import static me.dueris.genesismc.factory.powers.OriginsMod.player.attributes.AttributeHandler.Reach.getFinalReach;
import static me.dueris.genesismc.factory.powers.OriginsMod.prevent.PreventSuperClass.prevent_entity_use;

public class PreventEntityUse extends CraftPower implements Listener {

    @Override
    public void setActive(Boolean bool){
        if(powers_active.containsKey(getPowerFile())){
            powers_active.replace(getPowerFile(), bool);
        }else{
            powers_active.put(getPowerFile(), bool);
        }
    }

    @Override
    public Boolean getActive(){
        return powers_active.get(getPowerFile());
    }

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
                                ConditionExecutor conditionExecutor = new ConditionExecutor();
                                if(conditionExecutor.check("bientity_condition", "bientity_condition", p, origin, "origins:prevent_entity_use", null, ent)){
                                    if(conditionExecutor.check("item_condition", "item_condition", p, origin, "origins:prevent_entity_use", null, ent)){
                                        e.setCancelled(true);
                                        setActive(true);
                                    }else{
                                        setActive(false);
                                    }
                                }else{
                                    setActive(false);
                                }
                            }
                        } else {
                            e.setCancelled(true);
                        }
                    }
            }
        }
    }

    @Override
    public void run() {

    }

    @Override
    public String getPowerFile() {
        return "origins:prevent_entity_use";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return prevent_entity_use;
    }
}
