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

import java.util.ArrayList;

public class FireImmunity extends CraftPower implements Listener {

    @EventHandler
    public void OnDamageFire(EntityDamageEvent e) {
        if (e.getEntity().isDead()) return;
        if (e.getEntity() instanceof Player p) {
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                if (fire_immunity.contains(p)) {
                    for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getType(), layer)) {
                        if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) {
                            setActive(p, power.getTag(), true);
                            if (e.getCause().equals(EntityDamageEvent.DamageCause.FIRE) || e.getCause().equals(EntityDamageEvent.DamageCause.HOT_FLOOR) || e.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK) || e.getCause().equals(EntityDamageEvent.DamageCause.LAVA)) {
                                e.setCancelled(true);
                                e.setDamage(0);
                            }
                        } else {
                            setActive(p, power.getTag(), false);
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getType() {
        return "apoli:fire_immunity";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
        return fire_immunity;
    }
}
