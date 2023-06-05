package me.dueris.genesismc.core.factory.powers.entity;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import static me.dueris.genesismc.core.factory.powers.Powers.hotblooded;

public class HotBlooded implements Listener {

    @EventHandler
    public void hotblooded(EntityPotionEffectEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        if (!hotblooded.contains(p.getUniqueId().toString())) return;
        if (e.getOldEffect() == null) return;
        if (e.getOldEffect().getType().getId() == PotionEffectType.HUNGER.getId() || e.getOldEffect().getType().getId() == PotionEffectType.HUNGER.getId())
            return;
        if (e.getModifiedType().getId() == PotionEffectType.HUNGER.getId()) e.setCancelled(true);
        if (e.getModifiedType().getId() == PotionEffectType.POISON.getId()) e.setCancelled(true);
        ((Player) e.getEntity()).getActivePotionEffects().remove(PotionEffectType.POISON);
    }

}
