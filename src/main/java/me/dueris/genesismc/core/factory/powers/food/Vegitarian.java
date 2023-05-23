package me.dueris.genesismc.core.factory.powers.food;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.api.entity.OriginPlayer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

import static me.dueris.genesismc.core.factory.powers.Powers.vegetarian;
import static org.bukkit.Material.*;

public class Vegitarian implements Listener {
    public static EnumSet<Material> notVeggies;
    static {
        notVeggies = EnumSet.of(COOKED_BEEF, COOKED_CHICKEN, COOKED_MUTTON, COOKED_COD, COOKED_PORKCHOP, COOKED_RABBIT, COOKED_SALMON,
                BEEF, CHICKEN, MUTTON, COD, PORKCHOP, RABBIT, SALMON, TROPICAL_FISH, PUFFERFISH, RABBIT_STEW);
    }

    @EventHandler
    public void onItemConsume(PlayerInteractEvent e) {
        if (vegetarian.contains(OriginPlayer.getOriginTag(e.getPlayer()))) {
            ItemStack item = e.getItem();
            if (item == null) return;
            for (Material food : notVeggies) {
                if (item.getType() == food) {
                    e.setCancelled(true);
                }
            }
        }
    }

}
