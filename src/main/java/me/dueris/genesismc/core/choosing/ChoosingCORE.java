package me.dueris.genesismc.core.choosing;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.events.OrbInteractEvent;
import me.dueris.genesismc.core.factory.CraftApoli;
import me.dueris.genesismc.core.files.GenesisDataFiles;
import me.dueris.genesismc.core.utils.OriginContainer;
import me.dueris.genesismc.core.utils.SendCharts;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static me.dueris.genesismc.core.choosing.contents.MainMenuContents.GenesisMainMenuContents;
import static me.dueris.genesismc.core.utils.BukkitColour.AQUA;
import static org.bukkit.Bukkit.getServer;
import static org.bukkit.ChatColor.GRAY;

public class ChoosingCORE implements Listener {

    public static void setAttributesToDefault(Player p) {
        p.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(0);
        p.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(0);
        p.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(1);
        p.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4);
        p.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0);
        p.getAttribute(Attribute.GENERIC_LUCK).setBaseValue(0);
        p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.10000000149011612F);
    }

    public static ItemStack itemProperties(ItemStack item, String displayName, ItemFlag itemFlag, Enchantment enchantment, String lore) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(displayName);
        if (itemFlag != null) itemMeta.addItemFlags(itemFlag);
        if (enchantment != null) itemMeta.addEnchant(enchantment, 1, true);
        if (lore != null) {
            ArrayList<String> itemLore = new ArrayList<>();
            itemLore.add(lore);
            itemMeta.setLore(itemLore);
        }
        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack itemPropertiesMultipleLore(ItemStack item, String displayName, ItemFlag itemFlag, Enchantment enchantment, List lore) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(displayName);
        if (itemFlag != null) itemMeta.addItemFlags(itemFlag);
        if (enchantment != null) itemMeta.addEnchant(enchantment, 1, true);
        if (lore != null) itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }

    public static void removeItemEnder(Player player) {
        ItemStack infinpearl = new ItemStack(Material.ENDER_PEARL);
        ItemMeta pearl_meta = infinpearl.getItemMeta();
        pearl_meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Teleport");
        ArrayList<String> pearl_lore = new ArrayList();
        pearl_meta.setUnbreakable(true);
        pearl_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        pearl_meta.setLore(pearl_lore);
        infinpearl.setItemMeta(pearl_meta);
        player.getInventory().remove(infinpearl);
    }

    public static void removeItemPhantom(Player player) {
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
        player.getInventory().remove(spectatorswitch);
    }

    //open the menus

    public static void removeItemElytrian(Player player) {
        ItemStack launchitem = new ItemStack(Material.FEATHER);
        ItemMeta launchmeta = launchitem.getItemMeta();
        launchmeta.setDisplayName(GRAY + "Launch");
        launchmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        launchitem.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        launchitem.setItemMeta(launchmeta);
        player.getInventory().remove(launchitem);
    }

    @EventHandler
    public void onOrbClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (GenesisDataFiles.getMainConfig().getString("orb-of-origins").equalsIgnoreCase("true")) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                ItemStack item = new ItemStack(Material.MAGMA_CREAM);
                ItemMeta meta = item.getItemMeta();
                meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
                meta.setCustomModelData(00002);
                meta.setDisplayName(GenesisDataFiles.getOrbCon().getString("name"));
                meta.setUnbreakable(true);
                meta.getCustomTagContainer().setCustomTag(new NamespacedKey(GenesisMC.getPlugin(), "origins"), ItemTagType.STRING, "orb_of_origin");
                meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                item.setItemMeta(meta);
                PersistentDataContainer data = p.getPersistentDataContainer();
                int phantomid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER);
                if (phantomid == 1) ;
                if (e.getItem() != null && e.getItem().getType() != null) {
                    if (e.getItem().isSimilar(item)) {

                        @NotNull Inventory mainmenu = Bukkit.createInventory(e.getPlayer(), 54, "Choosing Menu");
                        mainmenu.setContents(GenesisMainMenuContents(e.getPlayer()));
                        e.getPlayer().openInventory(mainmenu);

                        OrbInteractEvent event = new OrbInteractEvent(p);
                        getServer().getPluginManager().callEvent(event);

                    }
                }
            }
        }
    }

    @EventHandler
    public void OnInteractCancel(InventoryClickEvent e) {

        if (e.getCurrentItem() != null) {
            if (e.getView().getTitle().equalsIgnoreCase("Choosing Menu")) {
                if (e.getCurrentItem().getType().equals(Material.SPECTRAL_ARROW)) {
                    Player p = (Player) e.getWhoClicked();
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 2);
                    e.getClickedInventory().setContents(GenesisMainMenuContents((Player) e.getWhoClicked()));
                    e.setCancelled(true);
                } else {
                    e.setCancelled(true);
                }


            } else {
                if (e.getView().getTitle().equalsIgnoreCase("Custom Origins")) {
                    if (e.getCurrentItem().getType().equals(Material.SPECTRAL_ARROW)) {
                        Player p = (Player) e.getWhoClicked();
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 2);
                        @NotNull Inventory mainmenu = Bukkit.createInventory(e.getWhoClicked(), 54, "Choosing Menu");
                        mainmenu.setContents(GenesisMainMenuContents((Player) e.getWhoClicked()));
                        e.getWhoClicked().openInventory(mainmenu);
                    }
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onMenuClose(InventoryClickEvent e) {
        if (e.getCurrentItem() != null) {
            if (e.getView().getTitle().equalsIgnoreCase("Choosing Menu")) {
                if (e.getCurrentItem().getType().equals(Material.BARRIER)) {
                    Player p = (Player) e.getWhoClicked();
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 2);
                    if (OriginPlayer.hasOrigin(p, "genesis:origin-null"))
                        p.kick(Component.text("You are forced to choose an origin!"));
                    e.getWhoClicked().closeInventory();
                } else {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onOrbRandom(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) return;
        if (e.getCurrentItem().getItemMeta() == null) return;
        if (e.getView().getTitle().equalsIgnoreCase("Choosing Menu")) {
            NamespacedKey key = new NamespacedKey(GenesisMC.getPlugin(), "orb");
            if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING) == null)
                return;
            if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING) != "orb")
                return;

            Player p = (Player) e.getWhoClicked();
            ArrayList<OriginContainer> origins = CraftApoli.getOrigins();
            ArrayList<String> layers = CraftApoli.getLayers();
            Random random = new Random();
            for (String layer : layers) {
                OriginContainer origin = origins.get(random.nextInt(origins.size()));
                OriginPlayer.setOrigin(p, layer, origin);
                p.sendMessage(Component.text("Your random origin(s) are " + layer + " : " + origin.getName() + "!").color(TextColor.fromHexString(AQUA)));
            }

            e.setCancelled(true);
            p.closeInventory();

            DefaultChoose.DefaultChoose(p);

            SendCharts.originPopularity(p);
        }
    }

}
