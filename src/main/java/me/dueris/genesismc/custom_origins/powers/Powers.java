package me.dueris.genesismc.custom_origins.powers;

import me.dueris.api.factory.CustomOriginAPI;
import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class Powers implements Listener {

    public static ArrayList<String> fall_immunity = new ArrayList<>();

    public static void loadPowers() {
        for (String originTag : CustomOriginAPI.getCustomOriginTags()) {
            for (String power : CustomOriginAPI.getCustomOriginPowers(originTag)) {
                if (power.equals("origins:fall_immunity")) {
                    fall_immunity.add(originTag);
                    continue;
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        PersistentDataContainer data = p.getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (fall_immunity.contains(origintag)) {
            if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                e.setCancelled(true);
            }
        }
    }
}
