package me.dueris.genesismc.core.utils;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ShulkUtils {
    public static void storeItems(List<ItemStack> items, Player p) {
        PersistentDataContainer data = p.getPersistentDataContainer();

        if (items.size() == 0) {
            data.set(new NamespacedKey(GenesisMC.getPlugin(), "shulker-box"), PersistentDataType.STRING, "");
        } else {
            try {
                ByteArrayOutputStream io = new ByteArrayOutputStream();
                BukkitObjectOutputStream os = new BukkitObjectOutputStream(io);

                os.writeInt(items.size());

                for (int i = 0; i < items.size(); i++) {
                    ItemStack item = items.get(i);
                    if (item != null) {
                        os.writeObject(item.clone()); // Clone the item to keep the original item intact
                    } else {
                        os.writeObject(null); // Write null for empty slots
                    }
                }

                os.flush();

                byte[] rawData = io.toByteArray();

                String encodedData = Base64.getEncoder().encodeToString(rawData);

                data.set(new NamespacedKey(GenesisMC.getPlugin(), "shulker-box"), PersistentDataType.STRING, encodedData);

                os.close();
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }
    }

    public static ArrayList<ItemStack> getItems(Player p) {
        PersistentDataContainer data = p.getPersistentDataContainer();

        ArrayList<ItemStack> items = new ArrayList<>();

        String encodedItems = data.get(new NamespacedKey(GenesisMC.getPlugin(), "shulker-box"), PersistentDataType.STRING);

        if (!encodedItems.isEmpty()) {
            byte[] rawData = Base64.getDecoder().decode(encodedItems);

            try {
                ByteArrayInputStream io = new ByteArrayInputStream(rawData);
                BukkitObjectInputStream in = new BukkitObjectInputStream(io);

                int itemsCount = in.readInt();

                for (int i = 0; i < itemsCount; i++) {
                    ItemStack item = (ItemStack) in.readObject();
                    if (item != null) {
                        items.add(item);
                    } else {
                        items.add(new ItemStack(Material.AIR)); // Add AIR for empty slots
                    }
                }

                in.close();
            } catch (IOException | ClassNotFoundException ex) {
                System.out.println(ex);
            }
        }

        return items;
    }

}
