package me.dueris.genesismc.core.choosing;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.items.OrbOfOrigins;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Random;

public class CustomOriginsMenu implements Listener {

    @EventHandler
    public static void OnTouchRandom(InventoryClickEvent e){

        if(e.getCurrentItem() != null){
            e.getWhoClicked().sendMessage("f");
            if(e.getView().getTitle().equalsIgnoreCase("Choosing Menu")){
                e.getWhoClicked().sendMessage("g");
                PersistentDataContainer data = e.getWhoClicked().getPersistentDataContainer();
                int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
                Random random = new Random();
                if(e.getCurrentItem().equals(OrbOfOrigins.orb)) {
                    e.getWhoClicked().sendMessage("i");
                    int r = random.nextInt(18);

                    if (r == 0) {
                        e.getWhoClicked().getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 0401065);
                        e.getWhoClicked().closeInventory();
                    } else
                    if (r == 1) {
                        e.getWhoClicked().getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 6503044);
                        e.getWhoClicked().closeInventory();
                    } else
                    if (r == 2) {
                        e.getWhoClicked().getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 0004013);
                        e.getWhoClicked().closeInventory();
                    } else
                    if (r == 3) {
                        e.getWhoClicked().getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 1709012);
                        e.getWhoClicked().closeInventory();
                    } else
                    if (r == 4) {
                        e.getWhoClicked().getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 2356555);
                        e.getWhoClicked().closeInventory();
                    } else
                    if (r == 5) {
                        e.getWhoClicked().getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 7300041);
                        e.getWhoClicked().closeInventory();
                    } else
                    if (r == 6) {
                        e.getWhoClicked().getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 2304045);
                        e.getWhoClicked().closeInventory();
                    } else
                    if (r == 7) {
                        e.getWhoClicked().getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 9602042);
                        e.getWhoClicked().closeInventory();
                    } else
                    if (r == 8) {
                        e.getWhoClicked().getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 9811027);
                        e.getWhoClicked().closeInventory();
                    } else
                    if (r == 9) {
                        e.getWhoClicked().getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 7303065);
                        e.getWhoClicked().closeInventory();
                    } else
                    if (r == 10) {
                        e.getWhoClicked().getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 1310018);
                        e.getWhoClicked().closeInventory();
                    } else
                    if (r == 11) {
                        e.getWhoClicked().getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 1205048);
                        e.getWhoClicked().closeInventory();
                    } else
                    if (r == 12) {
                        e.getWhoClicked().getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 5308033);
                        e.getWhoClicked().closeInventory();
                    } else
                    if (r == 13) {
                        e.getWhoClicked().getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 8906022);
                        e.getWhoClicked().closeInventory();
                    } else
                    if (r == 14) {
                        e.getWhoClicked().getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 6211006);
                        e.getWhoClicked().closeInventory();
                    } else
                    if (r == 15) {
                        e.getWhoClicked().getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 4501011);
                        e.getWhoClicked().closeInventory();
                    } else
                    if (r == 16) {
                        e.getWhoClicked().getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 6211021);
                        e.getWhoClicked().closeInventory();
                    } else
                    if (r == 17) {
                        e.getWhoClicked().getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 4307015);
                        e.getWhoClicked().closeInventory();
                    }else{
                        e.getWhoClicked().sendMessage(ChatColor.RED + "Error when executing RANDOM_ORIGIN on " + e.getWhoClicked().getName());
                        System.out.print(ChatColor.RED + "Error when executing RANDOM_ORIGIN on " + e.getWhoClicked().getName());
                        e.getWhoClicked().closeInventory();
                    }
                }
                

            }
        }
        
        
    }


}
