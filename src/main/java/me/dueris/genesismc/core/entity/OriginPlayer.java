package me.dueris.genesismc.core.entity;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.enums.OriginDataType;
import me.dueris.genesismc.core.enums.OriginMenu;
import me.dueris.genesismc.core.events.OriginChooseEvent;
import me.dueris.genesismc.core.utils.SendCharts;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

import static me.dueris.genesismc.core.choosing.ChoosingCORE.*;
import static me.dueris.genesismc.core.choosing.contents.ChooseMenuContents.ChooseMenuContent;
import static me.dueris.genesismc.core.choosing.contents.MainMenuContents.GenesisMainMenuContents;
import static org.bukkit.Bukkit.getServer;
import static org.bukkit.ChatColor.GRAY;

public class OriginPlayer {

    public static boolean hasChosenOrigin(Player player) {
        return !OriginPlayer.getOriginTag(player).equalsIgnoreCase("");
    }

    public static void removeArmor(Player player, EquipmentSlot slot) {
        ItemStack armor = player.getInventory().getItem(slot);

        if (armor != null && armor.getType() != Material.AIR) {
            // Remove the armor from the player's equipped slot
            player.getInventory().setItem(slot, new ItemStack(Material.AIR));

            // Add the armor to the player's inventory
            HashMap<Integer, ItemStack> excess = player.getInventory().addItem(armor);

            // If there is excess armor that couldn't fit in the inventory, drop it
            for (ItemStack item : excess.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
            }
        }
    }

    public static void moveEquipmentInventory(Player player, EquipmentSlot equipmentSlot) {
        ItemStack item = player.getInventory().getItem(equipmentSlot);

        if (item != null && item.getType() != Material.AIR) {
            // Find an empty slot in the player's inventory
            int emptySlot = player.getInventory().firstEmpty();

            if (emptySlot != -1) {
                // Set the equipment slot to empty
                player.getInventory().setItem(equipmentSlot, null);

                // Move the item to the empty slot
                player.getInventory().setItem(emptySlot, item);
            }
        }
    }

    public static void launchElytra(Player player) {
        Location location = player.getEyeLocation();
        double speed = 2.0;
        @NotNull Vector direction = location.getDirection().normalize();
        Vector velocity = direction.multiply(speed);
        player.setVelocity(velocity);
    }

    public static boolean hasOrigin(Player player, String origintag) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        @Nullable String origin = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (origin.equalsIgnoreCase("")) return false;
        return origin.contains(origintag);
    }

    public static String getOriginTag(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        if (data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING) == null)
            return null;
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        return origintag;
    }

    public static void removeOrigin(Player player) {
        if (player.getPersistentDataContainer() != null) {
            PersistentDataContainer data = player.getPersistentDataContainer();
            data.set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-null");
        }
    }

    public static boolean hasCoreOrigin(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        @Nullable String origintagPlayer = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (origintagPlayer.contains("genesis:origin-human")) {
            return true;
        } else if (origintagPlayer.contains("genesis:origin-enderian")) {
            return true;
        } else if (origintagPlayer.contains("genesis:origin-merling")) {
            return true;
        } else if (origintagPlayer.contains("genesis:origin-phantom")) {
            return true;
        } else if (origintagPlayer.contains("genesis:origin-elytrian")) {
            return true;
        } else if (origintagPlayer.contains("genesis:origin-blazeborn")) {
            return true;
        } else if (origintagPlayer.contains("genesis:origin-avian")) {
            return true;
        } else if (origintagPlayer.contains("genesis:origin-arachnid")) {
            return true;
        } else if (origintagPlayer.contains("genesis:origin-shulk")) {
            return true;
        } else if (origintagPlayer.contains("genesis:origin-feline")) {
            return true;
        } else if (origintagPlayer.contains("genesis:origin-starborne")) {
            return true;
        } else if (origintagPlayer.contains("genesis:origin-allay")) {
            return true;
        } else if (origintagPlayer.contains("genesis:origin-rabbit")) {
            return true;
        } else if (origintagPlayer.contains("genesis:origin-bee")) {
            return true;
        } else if (origintagPlayer.contains("genesis:origin-sculkling")) {
            return true;
        } else if (origintagPlayer.contains("genesis:origin-creep")) {
            return true;
        } else if (origintagPlayer.contains("genesis:origin-slimeling")) {
            return true;
        } else return origintagPlayer.contains("genesis:origin-piglin");
    }

    public static void setOrigin(Player player, String origin) {
        player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, origin);
        if (origin.contains("genesis:origin-human")) {
            setAttributesToDefault(player);
            removeItemPhantom(player);
            removeItemEnder(player);
        } else if (origin.contains("genesis:origin-enderian")) {
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                ItemStack infinpearl = new ItemStack(Material.ENDER_PEARL);
                ItemMeta pearl_meta = infinpearl.getItemMeta();
                pearl_meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Teleport");
                ArrayList<String> pearl_lore = new ArrayList();
                pearl_meta.setUnbreakable(true);
                pearl_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                pearl_meta.setLore(pearl_lore);
                infinpearl.setItemMeta(pearl_meta);
                player.getInventory().addItem(infinpearl);
                removeItemPhantom(player);
            }, 1);
        } else if (origin.contains("genesis:origin-shulk")) {
            float walk = 0.185F;
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                player.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(8.0);
                player.setWalkSpeed(walk);
                player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.45F);
                player.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(2.2);
                removeItemPhantom(player);
                removeItemEnder(player);
            }, 1);
        } else if (origin.contains("genesis:origin-arachnid")) {
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(14);
                removeItemPhantom(player);
                removeItemEnder(player);
            }, 1);
        } else if (origin.contains("genesis:origin-creep")) {
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(18);
                removeItemPhantom(player);
                removeItemEnder(player);
            }, 1);
        } else if (origin.contains("genesis:origin-phantom")) {
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(14);
                player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.11);
                ItemStack spectatorswitch = new ItemStack(Material.PHANTOM_MEMBRANE);
                ItemMeta switch_meta = spectatorswitch.getItemMeta();
                switch_meta.setDisplayName(GRAY + "Phantom Form");
                ArrayList<String> pearl_lore = new ArrayList();
                switch_meta.setUnbreakable(true);
                switch_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                switch_meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
                switch_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                switch_meta.setLore(pearl_lore);
                spectatorswitch.setItemMeta(switch_meta);
                player.getInventory().addItem(spectatorswitch);
                removeItemEnder(player);
            }, 1);
        } else if (origin.contains("genesis:origin-slimeling")) {
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                removeItemPhantom(player);
                removeItemEnder(player);
            }, 1);
        } else if (origin.contains("genesis:origin-feline")) {
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(18);
                removeItemPhantom(player);
                removeItemEnder(player);
            }, 1);
        } else if (origin.contains("genesis:origin-blaze")) {
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                removeItemPhantom(player);
                removeItemEnder(player);
            }, 1);
        } else if (origin.contains("genesis:origin-starborne")) {
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                removeItemPhantom(player);
                removeItemEnder(player);
            }, 1);
        } else if (origin.contains("geneis:origin-merling")) {
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                removeItemPhantom(player);
                removeItemEnder(player);
            }, 1);
        } else if (origin.contains("genesis:origin-allay")) {
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                removeItemPhantom(player);
                removeItemEnder(player);
            }, 1);
        } else if (origin.contains("genesis:origin-rabbit")) {
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(14);
                removeItemPhantom(player);
                removeItemEnder(player);
            }, 1);
        } else if (origin.contains("genesis:origin-bee")) {
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(14);
                removeItemPhantom(player);
                removeItemEnder(player);
            }, 1);
        } else if (origin.contains("genesis:origin-elytrian")) {
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                removeItemPhantom(player);
                removeItemEnder(player);
                ItemStack launchitem = new ItemStack(Material.FEATHER);
                ItemMeta launchmeta = launchitem.getItemMeta();
                launchmeta.setDisplayName(GRAY + "Launch");
                launchmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                launchitem.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                launchitem.setItemMeta(launchmeta);
                player.getInventory().addItem(launchitem);
            }, 1);
        } else if (origin.contains("genesis:origin-avian")) {
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.17);
                removeItemPhantom(player);
                removeItemEnder(player);
            }, 1);
        } else if (origin.contains("genesis:origin-piglin")) {
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                removeItemPhantom(player);
                removeItemEnder(player);
            }, 1);
        } else if (origin.contains("genesis:origin-sculkling")) {
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(24);
                removeItemPhantom(player);
                removeItemEnder(player);
            }, 1);
        } else {
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                removeItemPhantom(player);
                removeItemEnder(player);
            }, 1);
        }
        SendCharts.originPopularity(player);
    }

    public static void resetOriginData(Player player, OriginDataType type) {
        if (type.equals(OriginDataType.CAN_EXPLODE)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "can-explode"), PersistentDataType.INTEGER, 1);
        } else if (type.equals(OriginDataType.PHANTOMIZED_ID)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "phantomid"), PersistentDataType.INTEGER, 1);
        } else if (type.equals(OriginDataType.ORIGINTAG)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-null");
        } else if (type.equals(OriginDataType.SHULKER_BOX_DATA)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "shulker-box"), PersistentDataType.STRING, "");
        } else if (type.equals(OriginDataType.TOGGLE)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "toggle"), PersistentDataType.INTEGER, 1);
        } else if (type.equals(OriginDataType.IN_PHANTOMIZED_FORM)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER, 1);
        }

    }

    public static void setOriginData(Player player, OriginDataType type, int value) {
        if (type.equals(OriginDataType.CAN_EXPLODE)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "can-explode"), PersistentDataType.INTEGER, value);
        } else if (type.equals(OriginDataType.PHANTOMIZED_ID)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "phantomid"), PersistentDataType.INTEGER, value);
        } else if (type.equals(OriginDataType.TOGGLE)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "toggle"), PersistentDataType.INTEGER, value);
        } else if (type.equals(OriginDataType.IN_PHANTOMIZED_FORM)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER, value);
        }
    }

    public static void setOriginData(Player player, OriginDataType type, String value) {
        if (type.equals(OriginDataType.ORIGINTAG)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, value);
        } else if (type.equals(OriginDataType.SHULKER_BOX_DATA)) {
            player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "shulker-box"), PersistentDataType.STRING, value);
        }
    }

    public static boolean hasChosen(Player player) {
        return player.getScoreboardTags().contains("chosen");
    }

    public static void triggerChooseEvent(Player player) {
        OriginChooseEvent chooseEvent = new OriginChooseEvent(player);
        getServer().getPluginManager().callEvent(chooseEvent);
    }

    public static void openOriginGUI(Player player, OriginMenu menu) {
        if (menu.equals(OriginMenu.CHOOSE_MAIN)) {
            @NotNull Inventory custommenu = Bukkit.createInventory(player, 54, "Choosing Menu");
            custommenu.setContents(GenesisMainMenuContents(player));
            player.openInventory(custommenu);
        } else if (menu.equals(OriginMenu.CUSTOM_MAIN)) {
            @NotNull Inventory custommenu = Bukkit.createInventory(player, 54, "Custom Origins");
            custommenu.setContents(ChooseMenuContent());
            player.openInventory(custommenu);
        }
    }

}