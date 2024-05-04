package me.dueris.genesismc.util.entity;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class InventorySerializer implements Listener {

    public static void saveInNbtIO(String tag, String data, Player player) {
//        ServerPlayer p = ((CraftPlayer) player).getHandle();
//        CompoundTag compoundRoot = p.saveWithoutId(new CompoundTag());
//        if (!compoundRoot.contains("OriginData")) {
//            compoundRoot.put("OriginData", new CompoundTag());
//        }
//        CompoundTag originData = compoundRoot.getCompound("OriginData");
//        if (!originData.contains("InventoryData")) {
//            originData.put("InventoryData", new CompoundTag());
//        }
//        CompoundTag inventoryData = originData.getCompound("InventoryData");
//        inventoryData.putString(tag, data);
//        p.load(compoundRoot);
        PersistentDataContainer container = player.getPersistentDataContainer();
        container.set(GenesisMC.apoliIdentifier("inventorydata_" + format(tag)), PersistentDataType.STRING, data);
        player.saveData();
    }

    private static String getInNbtIO(String tag, Player player) {
//        ServerPlayer p = ((CraftPlayer) player).getHandle();
//        CompoundTag compoundRoot = p.saveWithoutId(new CompoundTag());
//        if (!compoundRoot.contains("OriginData")) {
//            compoundRoot.put("OriginData", new CompoundTag());
//        }
//        CompoundTag originData = compoundRoot.getCompound("OriginData");
//        if (!originData.contains("InventoryData")) {
//            originData.put("InventoryData", new CompoundTag());
//            return "";
//        } else {
//            return originData.getCompound("InventoryData").getString(tag);
//        }
        PersistentDataContainer container = player.getPersistentDataContainer();
        if (!container.has(GenesisMC.apoliIdentifier("inventorydata_" + format(tag)), PersistentDataType.STRING)) {
            return "";
        }
        return container.get(GenesisMC.apoliIdentifier("inventorydata_" + format(tag)), PersistentDataType.STRING);
    }

    private static String format(String tag) {
        return tag.replace(" ", "_").replace(":", "_").replace("/", "_").replace("\\", "_");
    }

    public static void storeItems(List<ItemStack> items, Player p, String tag) {
        PersistentDataContainer data = p.getPersistentDataContainer();

        if (items.size() == 0) {
            saveInNbtIO(tag, "", p);
        } else {
            try {
                ByteArrayOutputStream io = new ByteArrayOutputStream();
                BukkitObjectOutputStream os = new BukkitObjectOutputStream(io);

                os.writeInt(items.size());

                for (ItemStack item : items) {
                    if (item != null) {
                        os.writeObject(item.clone()); // Clone the item to keep the original item intact
                    } else {
                        os.writeObject(null); // Write null for empty slots
                    }
                }

                os.flush();

                byte[] rawData = io.toByteArray();

                String encodedData = Base64.getEncoder().encodeToString(rawData);

                saveInNbtIO(tag, encodedData, p);

                os.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static ArrayList<ItemStack> getItems(Player p, String tag) {
        ArrayList<ItemStack> items = new ArrayList<>();

        String encodedItems = getInNbtIO(tag, p);

        if (!encodedItems.isEmpty()) {
            byte[] rawData = Base64.getDecoder().decode(encodedItems);

            try {
                ByteArrayInputStream io = new ByteArrayInputStream(rawData);
                BukkitObjectInputStream in = new BukkitObjectInputStream(io);

                int itemsCount = in.readInt();

                for (int i = 0; i < itemsCount; i++) {
                    ItemStack item = (ItemStack) in.readObject();
                    // Add AIR for empty slots
                    items.add(Objects.requireNonNullElseGet(item, () -> new ItemStack(Material.AIR)));
                }

                in.close();
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }

        return items;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();

        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            for (Power power : OriginPlayerAccessor.getPowers(p, "apoli:inventory", layer)) {
                if (matches(e.getView(), power)) {
                    ArrayList<ItemStack> prunedItems = new ArrayList<>();

                    Arrays.stream(e.getInventory().getContents())
                            .filter(Objects::nonNull)
                            .forEach(prunedItems::add);
                    storeItems(prunedItems, p, power.getTag());
                }
            }
        }

    }

    private boolean matches(InventoryView inventory, Power power) {
        String title = power.getStringOrDefault("title", "container.inventory");
        return inventory.getTitle().equalsIgnoreCase(title);
    }

}