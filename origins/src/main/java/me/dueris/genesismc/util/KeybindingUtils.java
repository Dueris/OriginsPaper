package me.dueris.genesismc.util;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.mojang.datafixers.util.Pair;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.KeybindTriggerEvent;
import me.dueris.genesismc.event.OriginChangeEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.powers.ApoliPower;
import me.dueris.genesismc.registry.registries.Power;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class KeybindingUtils implements Listener {
    public static KeybindingUtils INSTANCE = new KeybindingUtils();
    public static HashMap<Player, ArrayList<String>> activeKeys = new HashMap<>();

    public static Pair<Boolean, String> renderKeybind(Power power) {
        boolean should = false;
        String key = "[null]";
        if (power.isOriginMultipleParent()) {
            for (Power powerContainer : CraftApoli.getNestedPowers(power)) {
                for (String object : powerContainer.keySet()) {
                    if (object.equalsIgnoreCase("key")) {
                        if (powerContainer.getObject(object) instanceof String st) {
                            should = true;
                            key = "[%%]".replace("%%", st);
                        } else if (powerContainer.getObject(object) instanceof JSONObject obj) {
                            should = obj.containsKey("key");
                            if (should) {
                                key = "[%%]".replace("%%", obj.get("key").toString());
                            }
                        }
                    }
                }
            }
        } else {
            for (String object : power.keySet()) {
                if (object.equalsIgnoreCase("key")) {
                    if (power.getObject(object) instanceof String st) {
                        should = true;
                        key = "[%%]".replace("%%", st);
                    } else if (power.getObject(object) instanceof JSONObject obj) {
                        should = obj.containsKey("key");
                        if (should) {
                            key = "[%%]".replace("%%", obj.get("key").toString());
                        }
                    }
                }
            }
        }
        return new Pair<>(should, key);
    }

    // Keybind event triggers

    public static String translateOriginRawKey(String string) {
        if (string.contains("key.origins.primary_active"))
            return string.replace("key.origins.primary_active", "Primary");
        if (string.contains("key.origins.secondary_active"))
            return string.replace("key.origins.secondary_active", "Secondary");
        return string;
    }

    public static ItemStack getPrimaryTrigger(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            if (item.getItemMeta() == null) continue;
            if (item.getItemMeta().getPersistentDataContainer().has(GenesisMC.identifier("origin_item_data"))) {
                if (item.getItemMeta().getPersistentDataContainer().get(GenesisMC.identifier("origin_item_data"), PersistentDataType.STRING).equalsIgnoreCase("key.origins.primary_active")) {
                    return item;
                }
            }
        }
        return null;
    }

    public static ItemStack getSecondaryTrigger(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            if (item.getItemMeta().getPersistentDataContainer().has(GenesisMC.identifier("origin_item_data"))) {
                if (item.getItemMeta().getPersistentDataContainer().get(GenesisMC.identifier("origin_item_data"), PersistentDataType.STRING).equalsIgnoreCase("key.origins.secondary_active")) {
                    return item;
                }
            }
        }
        return null;
    }

    public static boolean isExecutableTrue(String neededKey, KeybindTriggerEvent triggerEvent) {
        return triggerEvent.getKey().equalsIgnoreCase(neededKey);
    }

    public static void triggerExecution(String key, Player player) {
        triggerActiveKey(player, key);
        Bukkit.getPluginManager().callEvent(new KeybindTriggerEvent(player, key));
    }

    private static void triggerActiveKey(Player player, String key) {
        activeKeys.putIfAbsent(player, new ArrayList<>());
        activeKeys.get(player).add(key);
        new BukkitRunnable() {
            @Override
            public void run() {
                activeKeys.get(player).remove(key);
            }
        }.runTaskLater(GenesisMC.getPlugin(), 1);
    }

    // Keybind triggers end

    public static void toggleKey(Player player, String key) {
//        if(key.startsWith("key.origins")){
//            ItemStack item = getKeybindItem(key, player.getInventory());
//            if (item == null) return;
//            if(item.getType().equals(Material.LIME_DYE)){ // currently active, turn off
//                item.setType(Material.GRAY_DYE);
//            }else{ // currently off, turn on
//                item.setType(Material.LIME_DYE);
//            }
//        }
    }

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

    public static boolean isKeyActive(String key, Player player) {
        activeKeys.putIfAbsent(player, new ArrayList<>());
        return activeKeys.get(player).contains(key);
    }

    public static ItemStack createKeybindItem(String keybindType, String valueKEY, int id) {
        ItemStack itemStack = new ItemStack(Material.GRAY_DYE);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        itemMeta.setCustomModelData(id);
        itemMeta.setDisplayName(ChatColor.GRAY + "Keybind : " + keybindType);
        itemMeta.getPersistentDataContainer().set(GenesisMC.identifier("origin_item_data"), PersistentDataType.STRING, valueKEY);
        itemMeta.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static void addPrimaryItem(HumanEntity p) {
        p.getInventory().addItem(createKeybindItem("Primary", "key.origins.primary_active", 0001));
    }

    public static void addSecondaryItem(HumanEntity p) {
        p.getInventory().addItem(createKeybindItem("Secondary", "key.origins.secondary_active", 0002));
    }

    public static void addItems(HumanEntity p) {
        addPrimaryItem(p);
        addSecondaryItem(p);
    }

    // @EventHandler
    // public void test(KeybindTriggerEvent e){
    //     System.out.println(e.getKey());
    // }

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

    @EventHandler
    public void jump(PlayerJumpEvent e) {
        triggerExecution("key.jump", e.getPlayer());
    }

    @EventHandler
    public void clickc(PlayerInteractEvent e) {
        if (e.getHand() == EquipmentSlot.OFF_HAND) return;
        if (e.getAction().isRightClick()) {
            triggerExecution("key.use", e.getPlayer());
            if (e.getItem() != null) {
                if (e.getItem().getItemMeta() == null) return;
                if (e.getItem().getItemMeta().getPersistentDataContainer().has(GenesisMC.identifier("origin_item_data"))) { // Is keybind
                    triggerExecution(e.getItem().getItemMeta().getPersistentDataContainer().get(GenesisMC.identifier("origin_item_data"), PersistentDataType.STRING), e.getPlayer());
                }
            }
        } else {
            triggerExecution("key.attack", e.getPlayer());
        }
    }

    private void resetKeybinds(Player p) {
        if (activeKeys.containsKey(p)) {
            activeKeys.get(p).clear();
        }
        if (ApoliPower.powers_active.containsKey(p)) {
            ApoliPower.powers_active.get(p).clear();
        }
        for (ItemStack item : p.getInventory()) {
            if (item == null) continue;
            if (item.equals(getPrimaryTrigger(p))) {
                item.setType(Material.GRAY_DYE);
            }
            if (item.equals(getSecondaryTrigger(p))) {
                item.setType(Material.GRAY_DYE);
            }
        }
    }

    @EventHandler
    public void shift(PlayerToggleSneakEvent e) {
        triggerExecution("key.sneak", e.getPlayer());
    }

    @EventHandler
    public void swap(PlayerSwapHandItemsEvent e) {
        triggerExecution("key.swapOffhand", e.getPlayer());
    }

    @EventHandler
    public void drop(PlayerDropItemEvent e) {
        triggerExecution("key.drop", e.getPlayer());
    }

    @EventHandler
    @SuppressWarnings("Index out of bounds")
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
    public void resetKeybinding(OriginChangeEvent e) {
        resetKeybinds(e.getPlayer());
    }

    @EventHandler
    public void resetKeybinding2(PlayerRespawnEvent e) {
        resetKeybinds(e.getPlayer());
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
        e.getDrops().removeIf(itemStack -> itemStack.getItemMeta().getPersistentDataContainer().has(GenesisMC.identifier("origin_item_data")));
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
        if (!event.getSource().equals(event.getDestination()) && event.getItem().getItemMeta().getPersistentDataContainer().has(GenesisMC.identifier("origin_item_data"))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void preventDrop(PlayerDropItemEvent e) {
        if (e.getItemDrop().getItemStack().getItemMeta().getPersistentDataContainer().has(GenesisMC.identifier("origin_item_data"))) {
            e.setCancelled(true);
        }
    }
}
