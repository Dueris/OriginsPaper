package me.dueris.genesismc.factory.powers.prevent;

import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.player.attributes.AttributeHandler;
import me.dueris.genesismc.utils.PowerContainer;
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
import java.util.HashMap;
import java.util.function.Predicate;

import static me.dueris.genesismc.factory.powers.player.attributes.AttributeHandler.Reach.getFinalReach;
import static me.dueris.genesismc.factory.powers.prevent.PreventSuperClass.prevent_entity_use;

public class PreventEntityUse extends CraftPower implements Listener {

    @Override
    public void setActive(Player p, String tag, Boolean bool) {
        if(powers_active.containsKey(p)){
            if(powers_active.get(p).containsKey(tag)){
                powers_active.get(p).replace(tag, bool);
            }else{
                powers_active.get(p).put(tag, bool);
            }
        }else{
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
        }
    }

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void OnClickREACH(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (prevent_entity_use.contains(e.getPlayer())) {
            for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {

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
                            ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                            for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                                if (conditionExecutor.check("bientity_condition", "bientity_condition", p, power, "origins:prevent_entity_use", p, entity, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
                                    if (conditionExecutor.check("item_condition", "item_condition", p, power, "origins:prevent_entity_use", p, entity, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
                                        e.setCancelled(true);
                                        if (power == null) {
                                            getPowerArray().remove(p);
                                            return;
                                        }
                                        if (!getPowerArray().contains(p)) return;
                                        setActive(p, power.getTag(), true);
                                    } else {
                                        if (power == null) {
                                            getPowerArray().remove(p);
                                            return;
                                        }
                                        if (!getPowerArray().contains(p)) return;
                                        setActive(p, power.getTag(), false);
                                    }
                                } else {
                                    if (power == null) {
                                        getPowerArray().remove(p);
                                        return;
                                    }
                                    if (!getPowerArray().contains(p)) return;
                                    setActive(p, power.getTag(), false);
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

    @Override
    public String getPowerFile() {
        return "origins:prevent_entity_use";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return prevent_entity_use;
    }
}
