package me.dueris.genesismc.util.entity;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.util.ColorConstants;
import me.dueris.genesismc.util.LangConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class InventorySerializer implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {

        Player p = (Player) e.getPlayer();

        if (matchesAny(p, e.getView())) {
            ArrayList<ItemStack> prunedItems = new ArrayList<>();

            Arrays.stream(e.getInventory().getContents())
                .filter(itemStack -> {
                    return itemStack != null;
                })
                .forEach(itemStack -> prunedItems.add(itemStack));

            Player target = (Player) e.getPlayer();
            if (target == null) {
                p.sendMessage(Component.text(LangConfig.getLocalizedString(p, "errors.inventorySaveFail").replace("%player%", e.getView().getTitle().split(":")[1].substring(1))).color(TextColor.fromHexString(ColorConstants.RED)));
                return;
            }
            storeItems(prunedItems, target);
        }

    }

    private boolean matchesAny(Player player, InventoryView inventory) {
        AtomicBoolean matches = new AtomicBoolean(false);
        for(Layer layer : CraftApoli.getLayersFromRegistry()) {
            OriginPlayerAccessor.getMultiPowerFileFromType(player, "apoli:inventory", layer).forEach(power -> {
                String title = power.getStringOrDefault("title", "container.inventory");
                if(inventory.getTitle().equalsIgnoreCase(title)){
                    matches.set(true);
                }
            });
        }
        return matches.get();
    }

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
                ex.printStackTrace();
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
                ex.printStackTrace();
            }
        }

        return items;
    }

}