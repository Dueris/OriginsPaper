package me.dueris.genesismc;

import me.dueris.genesismc.events.KeybindTriggerEvent;
import me.dueris.genesismc.events.OriginChangeEvent;
import me.dueris.genesismc.events.OriginKeybindExecuteEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class KeybindHandler implements Listener {

    private static final Set<String> heldKeys = new HashSet<>();
    private static final Set<Player> spawnHandsTick = new HashSet<>();
    private static final Set<Player> primaryTick = new HashSet<>();
    private static final Set<Player> secondaryTick = new HashSet<>();
    private static final Map<Player, Integer> hotbarSlotTick = new HashMap();

    public static ItemStack getKeybindItem(String type, Inventory inventory) {
        for (ItemStack item : inventory.getContents()) {
            if (item != null) {
                if (item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "origin_item_data"))) {
                    if (item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "origin_item_data"), PersistentDataType.STRING).equalsIgnoreCase(type)) {
                        return item;
                    }
                }
            }
        }
        return null;
    }

    public static boolean hasOriginDataTriggerPrimary(Inventory inventory) {
        for (ItemStack item : inventory.getContents()) {
            if (item != null) {
                if (item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "origin_item_data"))) {
                    if (item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "origin_item_data"), PersistentDataType.STRING).equalsIgnoreCase("key.origins.primary_active")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean hasOriginDataTriggerSecondary(Inventory inventory) {
        for (ItemStack item : inventory.getContents()) {
            if (item != null) {
                if (item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "origin_item_data"))) {
                    if (item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "origin_item_data"), PersistentDataType.STRING).equalsIgnoreCase("key.origins.secondary_active")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static ItemStack createKeybindItem(String keybindType, String valueKEY) {
        ItemStack itemStack = new ItemStack(Material.GRAY_DYE);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        itemMeta.setDisplayName(ChatColor.GRAY + "Keybind : " + keybindType);
        itemMeta.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origin_item_data"), PersistentDataType.STRING, valueKEY);
        itemMeta.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static void addPrimaryItem(Player p) {
        p.getInventory().addItem(createKeybindItem("Primary", "key.origins.primary_active"));
    }

    public static void addSecondaryItem(Player p) {
        p.getInventory().addItem(createKeybindItem("Secondary", "key.origins.secondary_active"));
    }

    public static void addItems(Player p) {
        addPrimaryItem(p);
        addSecondaryItem(p);
    }

    @EventHandler
    public void resetKeybinding(OriginChangeEvent e) {
        for (ItemStack item : e.getPlayer().getInventory()) {
            if (item == null) continue;
            if (item.equals(getPrimaryTrigger(e.getPlayer()))) {
                runKeyChangeTriggerReturn(item, e.getPlayer(), "key.origins.primary_active");
            }
            if (item.equals(getSecondaryTrigger(e.getPlayer()))) {
                runKeyChangeTriggerReturn(item, e.getPlayer(), "key.origins.secondary_active");
            }
        }
    }

    public static ItemStack getPrimaryTrigger(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            if (item.getItemMeta() == null) continue;
            if (item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "origin_item_data"))) {
                if (item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "origin_item_data"), PersistentDataType.STRING).equalsIgnoreCase("key.origins.primary_active")) {
                    return item;
                }
            }
        }
        return null;
    }

    public static ItemStack getSecondaryTrigger(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            if (item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "origin_item_data"))) {
                if (item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "origin_item_data"), PersistentDataType.STRING).equalsIgnoreCase("key.origins.secondary_active")) {
                    return item;
                }
            }
        }
        return null;
    }

    public static ItemStack getTriggerFromOriginKey(Player player, String key) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            if (item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "origin_item_data"))) {
                if (item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "origin_item_data"), PersistentDataType.STRING).equalsIgnoreCase(key)) {
                    return item;
                }
            }
        }
        return null;
    }

    public static void runKeyChangeTrigger(ItemStack item) {
        item.setType(Material.LIME_DYE);
    }

    public static void runKeyChangeTriggerReturn(ItemStack item, Player player, String key) {
        item.setType(Material.GRAY_DYE);
        CooldownStuff.cooldowns.remove(player, key);
    }

    public static boolean isKeyBeingPressed(Player player, String keyName, boolean isKeyPressed) {
        if (isKeyPressed) {

            heldKeys.add(keyName);

            if (keyName.equals("key.sprint")) {
                return player.isSprinting();
            } else if (keyName.equals("key.sneak")) {
                return player.isSneaking();
            } else if (keyName.equals("key.swapOffhand")) {
                return spawnHandsTick.contains(player);
            } else if (keyName.startsWith("key.hotbar.")) {
                return hotbarSlotTick.containsValue(Integer.parseInt(keyName.split("hotbar.")[1])) && hotbarSlotTick.containsKey(player);
            }

            if (keyName.equals("key.origins.primary_active")) {
                return primaryTick.contains(player);
            }
            if (keyName.equals("key.origins.secondary_active")) {
                return secondaryTick.contains(player);
            }

            KeybindTriggerEvent keybindTriggerEvent = new KeybindTriggerEvent(player, keyName);

        } else {

            heldKeys.remove(keyName);

        }
        return false;
    }

    @EventHandler
    public void OnPressMainKey(OriginKeybindExecuteEvent e) {
        if (e.getKey().equals("key.origins.primary_active")) {
            primaryTick.add(e.getPlayer());
            new BukkitRunnable() {
                @Override
                public void run() {
                    primaryTick.remove(e.getPlayer());
                }
            }.runTaskLater(GenesisMC.getPlugin(), 1);
        }
        if (e.getKey().equals("key.origins.secondary_active")) {
            secondaryTick.add(e.getPlayer());
            new BukkitRunnable() {
                @Override
                public void run() {
                    secondaryTick.remove(e.getPlayer());
                }
            }.runTaskLater(GenesisMC.getPlugin(), 1);
        }
    }

    @EventHandler
    public void onTransfer(InventoryClickEvent e) {
        if (e.getClick().isKeyboardClick()) {
            if (e.getView().getTopInventory().getType() == InventoryType.CRAFTING) return;
            if (e.getView().getBottomInventory().getItem(e.getHotbarButton()) != null) {
                ItemStack transferred = e.getView().getBottomInventory().getItem(e.getHotbarButton());
                if (transferred == null) return;
                if (transferred.isSimilar(getPrimaryTrigger((Player) e.getWhoClicked()))) {
                    e.setCancelled(true);
                }
                if (transferred.isSimilar(getSecondaryTrigger((Player) e.getWhoClicked()))) {
                    e.setCancelled(true);
                }
            }

            return;
        }
        if (e.getView().getTopInventory().getType() != InventoryType.CRAFTING) {
            if (e.getView().getTopInventory().getHolder() != null && e.getView().getTopInventory().getHolder().equals(e.getWhoClicked()))
                return;
            if (e.getCurrentItem() == null) return;
            if (e.getCurrentItem().isSimilar(getPrimaryTrigger((Player) e.getWhoClicked()))) {
                e.setCancelled(true);
            }
            if (e.getCurrentItem().isSimilar(getSecondaryTrigger((Player) e.getWhoClicked()))) {
                e.setCancelled(true);
            }
        } else {
            if (e.getView().getTopInventory().getHolder() != null && e.getView().getTopInventory().getHolder().equals(e.getWhoClicked()))
                return;
            if (e.getCurrentItem() == null) return;
            if (e.getCurrentItem().isSimilar(getPrimaryTrigger((Player) e.getWhoClicked()))) {
                e.setCancelled(true);
            }
            if (e.getCurrentItem().isSimilar(getSecondaryTrigger((Player) e.getWhoClicked()))) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void EXECUTE_KEYBIND_EVENT(PlayerInteractEvent e) {
        ItemStack item = e.getItem();
        if (item != null && item.hasItemMeta()) {
            ItemMeta itemMeta = item.getItemMeta();
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();

            if (dataContainer.has(new NamespacedKey(GenesisMC.getPlugin(), "origin_item_data"), PersistentDataType.STRING)) {
                String originItemData = dataContainer.get(new NamespacedKey(GenesisMC.getPlugin(), "origin_item_data"), PersistentDataType.STRING);

                if (originItemData.equalsIgnoreCase("key.origins.primary_active") || originItemData.equalsIgnoreCase("key.origins.secondary_active")) {
                    if (!item.getType().equals(Material.LIME_DYE) || dataContainer.getOrDefault(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false)) {
                        String key = originItemData;

                        OriginKeybindExecuteEvent originKeybindExecuteEvent = new OriginKeybindExecuteEvent(e.getPlayer(), key, item);
                        Bukkit.getServer().getPluginManager().callEvent(originKeybindExecuteEvent);

                        KeybindTriggerEvent keybindExecuteEvent = new KeybindTriggerEvent(e.getPlayer(), key);
                        Bukkit.getServer().getPluginManager().callEvent(keybindExecuteEvent);

                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void OnCraftAttempt(PrepareItemCraftEvent e) {
        for (ItemStack ingredient : e.getInventory().getMatrix()) {
            if (ingredient == null) return;
            if (ingredient.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "origins_item_data"))) {
                e.getInventory().setResult(null);
            }
        }
    }

    @EventHandler
    public void jointhing(PlayerJoinEvent e) {
        if (e.getPlayer().getInventory().getContents() == null) {
            addItems(e.getPlayer());
        } else {
            if (!hasOriginDataTriggerPrimary(e.getPlayer().getInventory())) {
                addPrimaryItem(e.getPlayer());
            }
            if (!hasOriginDataTriggerSecondary(e.getPlayer().getInventory())) {
                addSecondaryItem(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void deathDropCancel(PlayerDeathEvent e) {
        e.getDrops().removeIf(itemStack -> itemStack.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "origin_item_data")));
    }

    @EventHandler
    public void respawnGIVE(PlayerRespawnEvent e) {
        if (e.getPlayer().getInventory().getContents() == null) {
            addItems(e.getPlayer());
        } else {
            if (!hasOriginDataTriggerPrimary(e.getPlayer().getInventory())) {
                addPrimaryItem(e.getPlayer());
            }
            if (!hasOriginDataTriggerSecondary(e.getPlayer().getInventory())) {
                addSecondaryItem(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        if (event.getSource().equals(event.getDestination())) {
        } else if (event.getItem().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "origin_item_data"))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage().toLowerCase();

        if (command.startsWith("/clear")) {
            addItems(player);
        }
    }

    @EventHandler
    public void dropFix(PlayerDropItemEvent e) {
        if (e.getItemDrop().getItemStack().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "origin_item_data"))) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerToggleSprint(PlayerToggleSprintEvent event) {
        isKeyBeingPressed(event.getPlayer(), "key.sprint", event.isSprinting());
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        isKeyBeingPressed(event.getPlayer(), "key.sneak", event.isSneaking());
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        spawnHandsTick.add(event.getPlayer());
        new BukkitRunnable() {
            @Override
            public void run() {
                spawnHandsTick.remove(event.getPlayer());
            }
        }.runTaskLater(GenesisMC.getPlugin(), 1);
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        int hotbarSlot = event.getNewSlot() + 1; // Hotbar slots are 1-indexed
        String keyName = "key.hotbar." + hotbarSlot;
        hotbarSlotTick.put(event.getPlayer(), hotbarSlot);
        new BukkitRunnable() {
            @Override
            public void run() {
                hotbarSlotTick.remove(event.getPlayer());
            }
        }.runTaskLater(GenesisMC.getPlugin(), 1);
    }
}
