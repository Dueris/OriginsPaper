package me.dueris.genesismc.core.factory.powers.food;

import me.dueris.genesismc.core.api.entity.OriginPlayer;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.EnumSet;

import static me.dueris.genesismc.core.factory.powers.Powers.carnivore;
import static org.bukkit.Material.*;

public class Carnivore implements Listener {

    public static EnumSet<Material> meat;
    public static EnumSet<Material> excludable;

    static {
        meat = EnumSet.of(COOKED_BEEF, COOKED_CHICKEN, COOKED_MUTTON, COOKED_COD, COOKED_PORKCHOP, COOKED_RABBIT, COOKED_SALMON,
                BEEF, CHICKEN, MUTTON, COD, PORKCHOP, RABBIT, SALMON, TROPICAL_FISH, PUFFERFISH, RABBIT_STEW);
        excludable = EnumSet.of(GOLDEN_APPLE, POTION, SPLASH_POTION, LINGERING_POTION, ENCHANTED_GOLDEN_APPLE, SUSPICIOUS_STEW, CHORUS_FRUIT);
    }

    @EventHandler
    public void CarnivoreEat(PlayerInteractEvent e) {
        if (carnivore.contains(OriginPlayer.getOriginTag(e.getPlayer()))) {
            if (e.getItem() != null) {
                if (!meat.contains(e.getItem().getType()) && !excludable.contains(e.getItem().getType()) && e.getItem().getType().isEdible()) {
                    if (e.getAction().isRightClick()) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }
}
