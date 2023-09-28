package me.dueris.genesismc.commands.subcommands.origin.Info;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.commands.subcommands.SubCommand;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.utils.LayerContainer;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import static me.dueris.genesismc.choosing.ChoosingCORE.itemProperties;
import static me.dueris.genesismc.choosing.ChoosingCUSTOM.cutStringIntoLists;
import static me.dueris.genesismc.items.OrbOfOrigins.orb;
import static me.dueris.genesismc.utils.BukkitColour.RED;

public class Info extends SubCommand implements Listener {

    public static final HashMap<Player, Integer> playerPage = new HashMap<>();
    @SuppressWarnings("FieldMayBeFinal")
    public static HashMap<Player, ArrayList<OriginContainer>> playerOrigins = new HashMap<>();

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "opens menu";
    }

    @Override
    public String getSyntax() {
        return "/origin info";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (sender instanceof Player p) {
            if (args.length == 1) {
                HashMap<LayerContainer, OriginContainer> origins = CraftApoli.toOrigin(p.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "origins"), PersistentDataType.STRING));
                assert origins != null;
                playerOrigins.put(p, new ArrayList<>(origins.values()));
                if (!playerPage.containsKey(p)) playerPage.put(p, 0);

                @NotNull Inventory help = Bukkit.createInventory(p, 54, "Info - " + playerOrigins.get(p).get(playerPage.get(p)).getLayerName());
                help.setContents(infoMenu(p, playerPage.get(p)));
                p.openInventory(help);
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 2);
            }
        } else
            sender.sendMessage(Component.text("You must be a player to use this command!").color(TextColor.fromHexString(RED)));
    }

    public ItemStack[] infoMenu(Player p, Integer page) {
        OriginContainer origin = playerOrigins.get(p).get(page);

        ArrayList<PowerContainer> powerContainers = new ArrayList<>();
        for(PowerContainer powerContainer : origin.getPowerContainers()){
            if(powerContainer.getHidden()) continue;
            powerContainers.add(powerContainer);
        }

        String minecraftItem = origin.getIcon();
        String item = null;
        if(minecraftItem.contains(":")){
            item = minecraftItem.split(":")[1];
        }else{
            item = minecraftItem;
        }
        ItemStack originIcon = new ItemStack(Material.valueOf(item.toUpperCase()));

        ItemStack close = itemProperties(new ItemStack(Material.BARRIER), ChatColor.RED + "Close", null, null, null);
        ItemStack exit = itemProperties(new ItemStack(Material.SPECTRAL_ARROW), ChatColor.AQUA + "Close", ItemFlag.HIDE_ENCHANTS, null, null);
        ItemStack lowImpact = itemProperties(new ItemStack(Material.GREEN_STAINED_GLASS_PANE), ChatColor.WHITE + "Impact: " + ChatColor.GREEN + "Low", null, null, null);
        ItemStack mediumImpact = itemProperties(new ItemStack(Material.YELLOW_STAINED_GLASS_PANE), ChatColor.WHITE + "Impact: " + ChatColor.YELLOW + "Medium", null, null, null);
        ItemStack highImpact = itemProperties(new ItemStack(Material.RED_STAINED_GLASS_PANE), ChatColor.WHITE + "Impact: " + ChatColor.RED + "High", null, null, null);
        ItemStack back = itemProperties(new ItemStack(Material.ARROW), "Back", ItemFlag.HIDE_ENCHANTS, null, null);
        ItemStack next = itemProperties(new ItemStack(Material.ARROW), "Next", ItemFlag.HIDE_ENCHANTS, null, null);


        ItemMeta originIconmeta = originIcon.getItemMeta();
        originIconmeta.setDisplayName(origin.getName());
        originIconmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        originIconmeta.setLore(cutStringIntoLists(origin.getDescription()));
        originIcon.setItemMeta(originIconmeta);

        NamespacedKey pageKey = new NamespacedKey(GenesisMC.getPlugin(), "page");
        ItemMeta backMeta = back.getItemMeta();
        if (page == 0) backMeta.getPersistentDataContainer().set(pageKey, PersistentDataType.INTEGER, 0);
        else backMeta.getPersistentDataContainer().set(pageKey, PersistentDataType.INTEGER, page - 1);
        back.setItemMeta(backMeta);


        ItemMeta nextMeta = next.getItemMeta();
        if (playerOrigins.get(p).size() - 1 == page)
            nextMeta.getPersistentDataContainer().set(pageKey, PersistentDataType.INTEGER, page);
        else nextMeta.getPersistentDataContainer().set(pageKey, PersistentDataType.INTEGER, page + 1);
        next.setItemMeta(nextMeta);


        ArrayList<ItemStack> contents = new ArrayList<>();
        long impact = origin.getImpact();

        for (int i = 0; i <= 53; i++) {
            if (i == 0 || i == 8) {
                contents.add(close);
            } else if (i == 1) {
                if (impact == 1) contents.add(lowImpact);
                else if (impact == 2) contents.add(mediumImpact);
                else if (impact == 3) contents.add(highImpact);
                else contents.add(new ItemStack(Material.AIR));
            } else if (i == 2) {
                if (impact == 2) contents.add(mediumImpact);
                else if (impact == 3) contents.add(highImpact);
                else contents.add(new ItemStack(Material.AIR));
            } else if (i == 3) {
                if (impact == 3) contents.add(highImpact);
                else contents.add(new ItemStack(Material.AIR));
            } else if (i == 4) {
                contents.add(orb);
            } else if (i == 5) {
                if (impact == 3) contents.add(highImpact);
                else contents.add(new ItemStack(Material.AIR));
            } else if (i == 6) {
                if (impact == 2) contents.add(mediumImpact);
                else if (impact == 3) contents.add(highImpact);
                else contents.add(new ItemStack(Material.AIR));
            } else if (i == 7) {
                if (impact == 1) contents.add(lowImpact);
                else if (impact == 2) contents.add(mediumImpact);
                else if (impact == 3) contents.add(highImpact);
                else contents.add(new ItemStack(Material.AIR));
            } else if (i == 13) {
                if (origin.getTag().equals("origins:human")) {
                    SkullMeta skull_p = (SkullMeta) originIcon.getItemMeta();
                    skull_p.setOwningPlayer(p);
                    skull_p.setOwner(p.getName());
                    skull_p.setPlayerProfile(p.getPlayerProfile());
                    skull_p.setOwnerProfile(p.getPlayerProfile());
                    originIcon.setItemMeta(skull_p);
                }
                contents.add(originIcon);
            } else if ((i >= 20 && i <= 24) || (i >= 29 && i <= 33) || (i >= 38 && i <= 42)) {
                while (powerContainers.size() > 0 && powerContainers.get(0).getHidden()) {
                    powerContainers.remove(0);
                }
                if (powerContainers.size() > 0) {

                    ItemStack originPower = new ItemStack(Material.FILLED_MAP);

                    ItemMeta meta = originPower.getItemMeta();
                    meta.setDisplayName(powerContainers.get(0).getName());
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    meta.setLore(cutStringIntoLists(powerContainers.get(0).getDescription()));
                    originPower.setItemMeta(meta);

                    contents.add(originPower);

                    powerContainers.remove(0);

                } else {
                    if (i >= 38) {
                        contents.add(new ItemStack(Material.AIR));
                    } else {
                        contents.add(new ItemStack(Material.PAPER));
                    }
                }


            } else if (i == 46) {
                contents.add(back);
            } else if (i == 49) {
                contents.add(exit);
            } else if (i == 52) {
                contents.add(next);
            } else {
                contents.add(new ItemStack(Material.AIR));
            }
        }
        return contents.toArray(new ItemStack[0]);
    }

    @EventHandler
    public void stopStealingInfo(InventoryClickEvent e) {
        if (e.getView().getTitle().startsWith("Info")) e.setCancelled(true);
    }

    @EventHandler
    public void onMenuExitInfo(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) return;
        if (e.getView().getTitle().startsWith("Info")) {
            if (e.getCurrentItem().getType() == Material.BARRIER || e.getCurrentItem().getType() == Material.SPECTRAL_ARROW) {
                Player p = (Player) e.getWhoClicked();
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                e.getWhoClicked().getInventory().close();
            }
        }
    }

    @EventHandler
    public void onMenuScroll(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        Player player = (Player) e.getWhoClicked();
        if (item == null) return;
        if (!e.getView().getTitle().startsWith("Info")) return;
        if (item.getType() == Material.ARROW && (item.getItemMeta().getDisplayName().equals("Back") || item.getItemMeta().getDisplayName().equals("Next"))) {

            if (item.getItemMeta().getDisplayName().equals("Back") && playerPage.get(player) > 0)
                playerPage.put(player, playerPage.get(player) - 1);
            if (item.getItemMeta().getDisplayName().equals("Next") && playerPage.get(player) < playerOrigins.get(player).size() - 1)
                playerPage.put(player, playerPage.get(player) + 1);

            @NotNull Inventory info = Bukkit.createInventory(player, 54, "Info - " + playerOrigins.get(player).get(playerPage.get(player)).getLayerName());
            info.setContents(infoMenu(player, item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "page"), PersistentDataType.INTEGER)));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 2);
            player.closeInventory();
            player.openInventory(info);
        }


    }
}
