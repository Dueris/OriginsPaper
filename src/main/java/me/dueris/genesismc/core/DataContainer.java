package me.dueris.genesismc.core;

import me.dueris.genesismc.core.factory.CraftApoli;
import me.dueris.genesismc.core.utils.ShulkUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;

public class DataContainer implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();

        PersistentDataContainer data = p.getPersistentDataContainer();

        if (!data.has(new NamespacedKey(GenesisMC.getPlugin(), "shulker-box"), PersistentDataType.STRING)) {
            data.set(new NamespacedKey(GenesisMC.getPlugin(), "shulker-box"), PersistentDataType.STRING, "");
        }
        if (!p.getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER)) {
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 0);
        }
        if (!p.getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "origin"), PersistentDataType.BYTE_ARRAY)) {
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origin"), PersistentDataType.BYTE_ARRAY, CraftApoli.toByteArray(CraftApoli.choosingOrigin()));
        }
        if (!p.getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "can-explode"), PersistentDataType.INTEGER)) {
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "can-explode"), PersistentDataType.INTEGER, 1);
        }
        if (!p.getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER)) {
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER, 1);
        }
        if (!p.getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "toggle"), PersistentDataType.INTEGER)) {
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "toggle"), PersistentDataType.INTEGER, 1);
        }

    }

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