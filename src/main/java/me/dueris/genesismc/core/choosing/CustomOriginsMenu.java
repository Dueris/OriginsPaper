package me.dueris.genesismc.core.choosing;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.items.OrbOfOrigins;
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
            if(e.getView().getTitle().equalsIgnoreCase("Choosing Menu")){
                PersistentDataContainer data = e.getWhoClicked().getPersistentDataContainer();
                int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
                Random random = new Random();
                if(e.getCurrentItem().equals(OrbOfOrigins.orb)) {
                    int r = random.nextInt(18);

                    if (r == 0) {
                        e.getWhoClicked().getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 0401065);
                    }
                    if (r == 1) {
                        e.getWhoClicked().getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 6503044);

                    }
                    if (r == 2) {
                        e.getWhoClicked().getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 0004013);

                    }
                    if (r == 3) {
                        e.getWhoClicked().getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 1709012);

                    }
                    if (r == 4) {
                        e.getWhoClicked().getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 2356555);

                    }
                    if (r == 5) {
                        e.getWhoClicked().getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 7300041);

                    }
                    if (r == 6) {
                        e.getWhoClicked().getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 2304045);

                    }
                    if (r == 7) {
                        e.getWhoClicked().getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 9602042);

                    }
                    if (r == 8) {
                        e.getWhoClicked().getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 9811027);

                    }
                    if (r == 9) {
                        e.getWhoClicked().getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 7303065);

                    }
                    if (r == 10) {
                        e.getWhoClicked().getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 1310018);

                    }
                    if (r == 11) {
                        e.getWhoClicked().getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 1205048);

                    }
                    if (r == 12) {
                        e.getWhoClicked().getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 5308033);

                    }
                    if (r == 13) {
                        e.getWhoClicked().getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 8906022);

                    }
                    if (r == 14) {
                        e.getWhoClicked().getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 6211006);

                    }
                    if (r == 15) {
                        e.getWhoClicked().getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 4501011);

                    }
                    if (r == 16) {
                        e.getWhoClicked().getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 6211021);

                    }
                    if (r == 17) {
                        e.getWhoClicked().getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 4307015);

                    }
                }
                

            }
        }
        
        
    }


}
