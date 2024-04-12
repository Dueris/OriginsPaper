package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.RayTraceResult;

import java.util.ArrayList;
import java.util.function.Predicate;

import static me.dueris.genesismc.factory.powers.apoli.AttributeHandler.ReachUtils.getFinalReach;
import static me.dueris.genesismc.factory.powers.apoli.superclass.PreventSuperClass.prevent_entity_use;

public class PreventEntityUse extends CraftPower implements Listener {


    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void OnClickREACH(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (prevent_entity_use.contains(e.getPlayer())) {
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {

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
                    if (attacker.getLocation().distance(victim.getLocation()) <= AttributeHandler.ReachUtils.getFinalReach(p)) {
                        if (entity.getPassengers().contains(p)) return;
                        if (!entity.isDead()) {
                            ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                            for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                                if (ConditionExecutor.testEntity(power.getJsonObjectOrNew("condition"), (CraftEntity) p) && ConditionExecutor.testBiEntity(power.getJsonObjectOrNew("bientity_condition"), (CraftEntity) p, (CraftEntity) entity) && ConditionExecutor.testItem(power.getJsonObjectOrNew("item_condition"), e.getItem())) {
                                    e.setCancelled(true);
                                    setActive(p, power.getTag(), true);
                                } else {
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
        return "apoli:prevent_entity_use";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return prevent_entity_use;
    }
}
