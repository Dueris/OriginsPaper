package me.dueris.genesismc.core.factory.powers.entity;

import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import static me.dueris.genesismc.core.factory.powers.Powers.hot_hands;

public class HotHands implements Listener {

    @EventHandler
    public void HotHands(PrePlayerAttackEntityEvent e) {
        PersistentDataContainer data = e.getPlayer().getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (hot_hands.contains(origintag)) {
            e.getAttacked().setFireTicks(60);
        }
    }

}
