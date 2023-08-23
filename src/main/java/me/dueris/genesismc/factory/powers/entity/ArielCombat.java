package me.dueris.genesismc.factory.powers.entity;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import static me.dueris.genesismc.factory.powers.OriginsMod.player.FlightElytra.glidingPlayers;
import static me.dueris.genesismc.factory.powers.Power.aerial_combatant;

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
