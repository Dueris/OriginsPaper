package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static me.dueris.genesismc.factory.powers.apoli.superclass.ValueModifyingSuperClass.modify_falling;

public class ModifyFallingPower extends CraftPower implements Listener {

    @EventHandler
    public void runE(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (modify_falling.contains(p)) {
            if (e.getTo().getY() == e.getFrom().getY()) return;
            @NotNull Vector velocity = p.getVelocity();
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) {
                        if (power.getNumber("velocity").getFloat() < 0) {
                            velocity.setY(power.getNumber("velocity").getFloat());
                            p.setVelocity(velocity);
                        } else {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 5, 1, false, false, false));
                        }
                    }
                }

            }
        }
    }

    @EventHandler
    public void runR(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (modify_falling.contains(p)) {
                for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                    for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                        if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) {
                            if (!power.getBooleanOrDefault("take_fall_damage", true)) {
                                if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                                    e.setDamage(0);
                                    e.setCancelled(true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:modify_falling";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_falling;
    }
}
