package me.dueris.genesismc.core.factory.powers.entity;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import static me.dueris.genesismc.core.factory.powers.Powers.aerial_combatant;
import static me.dueris.genesismc.core.factory.powers.armour.FlightElytra.glidingPlayers;

public class ArielCombat implements Listener {
    @EventHandler
    public void ArielCombat(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        if (aerial_combatant.contains(e.getDamager().getUniqueId())) {
            if ((glidingPlayers.contains(e.getDamager().getUniqueId())) || ((Player) e.getDamager()).isGliding()) {
                e.setDamage(e.getDamage() * 2);
            }
        }
    }
}
