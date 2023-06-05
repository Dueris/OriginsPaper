package me.dueris.genesismc.core.factory.powers.entity;

import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import me.dueris.genesismc.core.entity.OriginPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static me.dueris.genesismc.core.factory.powers.Powers.hot_hands;

public class HotHands implements Listener {

    @EventHandler
    public void HotHands(PrePlayerAttackEntityEvent e) {
        if (hot_hands.contains(e.getPlayer())) {
            e.getAttacked().setFireTicks(60);
        }
    }

}
