package me.dueris.genesismc.factory.powers.entity;

import me.dueris.genesismc.events.OriginChangeEvent;
import org.bukkit.attribute.Attribute;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static me.dueris.genesismc.factory.powers.Power.fragile;

public class Fragile implements Listener {
    @EventHandler
    public void ChooseEventSetEvent(OriginChangeEvent e) {
        if (fragile.contains(e.getPlayer())) {
            e.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(14);
        } else {
            e.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        }
    }
}
