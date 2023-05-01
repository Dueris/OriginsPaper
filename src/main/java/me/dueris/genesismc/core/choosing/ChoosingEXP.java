package me.dueris.genesismc.core.choosing;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.choosing.contents.EXPMenuContents;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class ChoosingEXP implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void EXPCHOOSE_MENU(InventoryClickEvent e){
        if(e.getCurrentItem() != null){
            if(e.getView().getTitle().equalsIgnoreCase("Choosing Menu")) {
                PersistentDataContainer data = e.getWhoClicked().getPersistentDataContainer();
                @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
                @NotNull Inventory expmenu = Bukkit.createInventory(e.getWhoClicked(), 54, "Expanded Origins");
                if (e.getCurrentItem().getType().equals(Material.MUSIC_DISC_OTHERSIDE)) {
                    Player p = (Player) e.getWhoClicked();
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                    expmenu.setContents(EXPMenuContents.EXPContents());
                    e.getWhoClicked().openInventory(expmenu);

                }
            }
        }
    }

}
