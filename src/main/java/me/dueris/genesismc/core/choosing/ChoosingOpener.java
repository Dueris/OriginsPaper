package me.dueris.genesismc.core.choosing;


import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import static me.dueris.genesismc.api.choose.contents.MainMenuContents.GenesisMainMenuContents;

public class ChoosingOpener implements Listener {


    @EventHandler
    public static void ChooserJoin(PlayerJoinEvent e) {
        PersistentDataContainer data = e.getPlayer().getPersistentDataContainer();
        int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
        @NotNull Inventory mainmenu = Bukkit.createInventory(e.getPlayer(), 54, "Choosing Menu");
        if (originid == 0) {

            mainmenu.setContents(GenesisMainMenuContents());
            e.getPlayer().openInventory(mainmenu);
        }
    }

    @EventHandler
    public void OnInteractCancel(InventoryClickEvent e){

        if(e.getCurrentItem() != null){
            if(e.getView().getTitle().equalsIgnoreCase("Choosing Menu")){

                e.setCancelled(true);

            }else{
                if(e.getView().getTitle().equalsIgnoreCase("Custom Origins") || e.getView().getTitle().equalsIgnoreCase("Expanded Origins")){
                    e.setCancelled(true);
                    if(e.getCurrentItem().getType().equals(Material.SPECTRAL_ARROW)){
                        @NotNull Inventory mainmenu = Bukkit.createInventory(e.getWhoClicked(), 54, "Choosing Menu");
                            mainmenu.setContents(GenesisMainMenuContents());
                            e.getWhoClicked().openInventory(mainmenu);
                    }
                }
            }
        }


    }


}
