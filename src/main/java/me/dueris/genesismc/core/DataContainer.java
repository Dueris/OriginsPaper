package me.dueris.genesismc.core;

import me.dueris.genesismc.core.utils.ShulkUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public class DataContainer implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {

        Player p = (Player) e.getPlayer();

        if (e.getView().getTitle().equalsIgnoreCase("Shulker Inventory")) {

            ArrayList<ItemStack> prunedItems = new ArrayList<>();

            Arrays.stream(e.getInventory().getContents())
                    .filter(itemStack -> {
                        return itemStack != null;
                    })
                    .forEach(itemStack -> prunedItems.add(itemStack));

            ShulkUtils.storeItems(prunedItems, p);

        }

    }

}