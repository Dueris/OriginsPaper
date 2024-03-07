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
import org.bukkit.event.entity.EntityToggleGlideEvent;

import java.util.ArrayList;
import java.util.HashMap;

import static me.dueris.genesismc.factory.powers.apoli.superclass.PreventSuperClass.prevent_elytra_flight;

public class PreventElytraFlight extends CraftPower implements Listener {

    @Override
    public void setActive(Player p, String tag, Boolean bool) {
        if (powers_active.containsKey(p)) {
            if (powers_active.get(p).containsKey(tag)) {
                powers_active.get(p).replace(tag, bool);
            } else {
                powers_active.get(p).put(tag, bool);
            }
        } else {
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
        }
    }


    @EventHandler
    public void run(EntityToggleGlideEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (prevent_elytra_flight.contains(p)) {
                for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                    for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                        if (ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) p)) {
                            e.setCancelled(true);
                            setActive(p, power.getTag(), true);
                        } else {
                            setActive(p, power.getTag(), false);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void run(Player p) {

    }

    @Override
    public String getPowerFile() {
        return "apoli:prevent_elytra_flight";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return prevent_elytra_flight;
    }
}
