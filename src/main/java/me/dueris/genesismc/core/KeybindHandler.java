package me.dueris.genesismc.core;

import me.dueris.genesismc.core.events.KeybindTriggerEvent;
import me.dueris.genesismc.core.events.OriginKeybindExecuteEvent;
import me.dueris.genesismc.core.factory.powers.OriginsMod.player.inventory.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
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
import org.bukkit.util.Vector;
import org.geysermc.geyser.api.GeyserApi;

import java.util.*;

import static me.dueris.genesismc.core.factory.powers.Powers.*;
import static me.dueris.genesismc.core.factory.powers.item.LaunchAir.*;
import static org.bukkit.Bukkit.getPlayer;
import static org.bukkit.Bukkit.getServer;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.Material.ENDER_PEARL;

public class KeybindHandler implements Listener {

    @EventHandler
    public void OnPressMainKey(OriginKeybindExecuteEvent e) {
        if(e.getKey().equals("key.origins.primary_active")){
            primaryTick.add(e.getPlayer());
            new BukkitRunnable(){
                @Override
                public void run() {
                    primaryTick.remove(e.getPlayer());
                    this.cancel();
                }
            }.runTaskTimer(GenesisMC.getPlugin(), 1, 1);
        }
        if(e.getKey().equals("key.origins.secondary_active")){
            secondaryTick.add(e.getPlayer());
            new BukkitRunnable(){
                @Override
                public void run() {
                    secondaryTick.remove(e.getPlayer());
                    this.cancel();
                }
            }.runTaskTimer(GenesisMC.getPlugin(), 1, 1);
        }
    }

    @EventHandler
    public void EXECUTE_KEYBIND_EVENT(PlayerInteractEvent e){
        if(e.getItem() != null){
            if(e.getItem().getItemMeta() != null){
                if(e.getItem().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "origin_item_data"))){
                    if(e.getItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "origin_item_data"), PersistentDataType.STRING).equalsIgnoreCase("key.origins.primary_active")){
                        if(!e.getItem().getType().equals(Material.LIME_DYE)){
                            OriginKeybindExecuteEvent originKeybindExecuteEvent = new OriginKeybindExecuteEvent(e.getPlayer(), "key.origins.primary_active", e.getItem());
                            Bukkit.getServer().getPluginManager().callEvent(originKeybindExecuteEvent);
                            KeybindTriggerEvent KeybindExecuteEvent = new KeybindTriggerEvent(e.getPlayer(), "key.origins.primary_active");
                            Bukkit.getServer().getPluginManager().callEvent(KeybindExecuteEvent);
                            e.setCancelled(true);
                        }
                    } else if (e.getItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "origin_item_data"), PersistentDataType.STRING).equalsIgnoreCase("key.origins.secondary_active")) {
                        if(!e.getItem().getType().equals(Material.LIME_DYE)){
                            OriginKeybindExecuteEvent originKeybindExecuteEvent = new OriginKeybindExecuteEvent(e.getPlayer(), "key.origins.secondary_active", e.getItem());
                            Bukkit.getServer().getPluginManager().callEvent(originKeybindExecuteEvent);
                            KeybindTriggerEvent KeybindExecuteEvent = new KeybindTriggerEvent(e.getPlayer(), "key.origins.secondary_active");
                            Bukkit.getServer().getPluginManager().callEvent(KeybindExecuteEvent);
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    public static ItemStack getKeybindItem(String type, Inventory inventory){
        for (ItemStack item : inventory.getContents()) {
            if (item != null) {
                if (item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "origin_item_data"))) {
                    if(item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "origin_item_data"), PersistentDataType.STRING).equalsIgnoreCase(type)){
                        return item;
                    }
                }
            }
        }
        return null;
    }

    @EventHandler
    public void OnCraftAttempt(PrepareItemCraftEvent e) {

        for (ItemStack ingredient : e.getInventory().getMatrix()) {
            if(ingredient.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "origins_item_data"))){
                e.getInventory().setResult(null);
            }
        }
    }

    public static boolean hasOriginDataTriggerPrimary(Inventory inventory) {
        for (ItemStack item : inventory.getContents()) {
            if (item != null) {
                if (item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "origin_item_data"))) {
                    if(item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "origin_item_data"), PersistentDataType.STRING).equalsIgnoreCase("key.origins.primary_active")){
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
                    if(item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "origin_item_data"), PersistentDataType.STRING).equalsIgnoreCase("key.origins.secondary_active")){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static ItemStack createKeybindItem(String keybindType, String valueKEY){
        ItemStack itemStack = new ItemStack(Material.GRAY_DYE);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        itemMeta.setDisplayName(ChatColor.GRAY + "Keybind : " + keybindType);
        itemMeta.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origin_item_data"), PersistentDataType.STRING, valueKEY);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static void addPrimaryItem(Player p){
        p.getInventory().addItem(createKeybindItem("Primary", "key.origins.primary_active"));
    }

    public static void addSecondaryItem(Player p){
        p.getInventory().addItem(createKeybindItem("Secondary", "key.origins.secondary_active"));
    }

    public static void addItems(Player p){
        addPrimaryItem(p);
        addSecondaryItem(p);
    }

    @EventHandler
    public void jointhing(PlayerJoinEvent e){
        if(e.getPlayer().getInventory().getContents() == null){
            addItems(e.getPlayer());
        }else{
            if(!hasOriginDataTriggerPrimary(e.getPlayer().getInventory())){
                addPrimaryItem(e.getPlayer());
            }
            if(!hasOriginDataTriggerSecondary(e.getPlayer().getInventory())){
                addSecondaryItem(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void deathDropCancel(PlayerDeathEvent e){
        e.getDrops().removeIf(itemStack -> itemStack.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "origin_item_data")));
    }

    @EventHandler
    public void respawnGIVE(PlayerRespawnEvent e){
        if(e.getPlayer().getInventory().getContents() == null){
            addItems(e.getPlayer());
        }else{
            if(!hasOriginDataTriggerPrimary(e.getPlayer().getInventory())){
                addPrimaryItem(e.getPlayer());
            }
            if(!hasOriginDataTriggerSecondary(e.getPlayer().getInventory())){
                addSecondaryItem(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void dropFix(PlayerDropItemEvent e){
        if(e.getItemDrop().getItemStack().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "origin_item_data"))){
            e.setCancelled(true);
        }
    }

    private static final Set<String> heldKeys = new HashSet<>();
    private static final Set<Player> spawnHandsTick = new HashSet<>();
    private static final Set<Player> primaryTick = new HashSet<>();
    private static final Set<Player> secondaryTick = new HashSet<>();
    private static final Map<Player, Integer> hotbarSlotTick = new HashMap();

    @EventHandler
    public void onPlayerToggleSprint(PlayerToggleSprintEvent event) {
        isKeyBeingPressed(event.getPlayer(), "key.sprint", event.isSprinting());
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        isKeyBeingPressed(event.getPlayer(), "key.sneak", event.isSneaking());
    }

    public static ItemStack getPrimaryTrigger(Player player){
        for(ItemStack item : player.getInventory().getContents()){
            if(item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "origin_item_data"))){
                if(item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "origin_item_data"), PersistentDataType.STRING).equalsIgnoreCase("key.origins.primary_active")){
                    return item;
                }
            }
        }
        return null;
    }

    public static ItemStack getSecondaryTrigger(Player player){
        for(ItemStack item : player.getInventory().getContents()){
            if(item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "origin_item_data"))){
                if(item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "origin_item_data"), PersistentDataType.STRING).equalsIgnoreCase("key.origins.secondary_active")){
                    return item;
                }
            }
        }
        return null;
    }

    public static ItemStack getTriggerFromOriginKey(Player player, String key){
        for(ItemStack item : player.getInventory().getContents()){
            if(item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "origin_item_data"))){
                if(item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "origin_item_data"), PersistentDataType.STRING).equalsIgnoreCase(key)){
                    return item;
                }
            }
        }
        return null;
    }

    public static void runKeyChangeTrigger(ItemStack item){
        item.setType(Material.LIME_DYE);
    }

    public static void runKeyChangeTriggerReturn(ItemStack item, Player player, String key){
        item.setType(Material.GRAY_DYE);
        CooldownStuff.cooldowns.remove(player, key);
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        spawnHandsTick.add(event.getPlayer());
        new BukkitRunnable(){
            @Override
            public void run() {
                spawnHandsTick.remove(event.getPlayer());
                this.cancel();
            }
        }.runTaskTimer(GenesisMC.getPlugin(), 1, 1);
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        int hotbarSlot = event.getNewSlot() + 1; // Hotbar slots are 1-indexed
        String keyName = "key.hotbar." + hotbarSlot;
        hotbarSlotTick.put(event.getPlayer(), hotbarSlot);
        new BukkitRunnable(){
            @Override
            public void run() {
                hotbarSlotTick.remove(event.getPlayer());
                this.cancel();
            }
        }.runTaskTimer(GenesisMC.getPlugin(), 1, 1);
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

            if(keyName.equals("key.origins.primary_active")){
                return primaryTick.contains(player);
            }
            if(keyName.equals("key.origins.secondary_active")){
                return secondaryTick.contains(player);
            }

            KeybindTriggerEvent keybindTriggerEvent = new KeybindTriggerEvent(player, keyName);

        } else {

            heldKeys.remove(keyName);

        }
        return false;
    }
}
