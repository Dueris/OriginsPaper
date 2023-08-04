package me.dueris.genesismc.core.factory.powers.OriginsMod.player.inventory;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static me.dueris.genesismc.core.factory.powers.Powers.keep_inventory;

public class KeepInventory implements Listener {

    @EventHandler
    public void keepinv(PlayerDeathEvent e) {
        Player player = e.getEntity();
        for (OriginContainer origin : OriginPlayer.getOrigin(player).values()) {
            if (keep_inventory.contains(player)) {
                if (ConditionExecutor.check("item_condition", player, origin, "origins:keep_inventory", null, player)) {
                    ArrayList<Long> slots = new ArrayList<>();
                    if (origin.getPowerFileFromType("origins:keep_inventory").getSlots() != null) {
                        for (long slot : origin.getPowerFileFromType("origins:keep_inventory").getSlots()) {
                            slots.add(slot);
                        }
                    }

                    if (!slots.isEmpty()) {
                        for (int i = 0; i < player.getInventory().getSize(); i++) {
                            if (slots.contains((long) i)) {
                                e.getItemsToKeep().add(player.getInventory().getItem(i));
                            }
                        }
                    } else {
                        e.setKeepInventory(true);
                    }
                }
            }
        }
    }
}
