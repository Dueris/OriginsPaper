package me.dueris.genesismc.core.commands.subcommands.origin;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.CraftApoli;
import me.dueris.genesismc.core.choosing.contents.origins.ExpandedOriginContent;
import me.dueris.genesismc.core.choosing.contents.origins.OriginalOriginContent;
import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import me.dueris.genesismc.core.factory.CraftApoliRewriten;
import me.dueris.genesismc.core.utils.OriginContainer;
import me.dueris.genesismc.core.utils.PowerContainer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static me.dueris.genesismc.core.choosing.ChoosingCORE.itemProperties;
import static me.dueris.genesismc.core.choosing.ChoosingCUSTOM.cutStringIntoLists;
import static me.dueris.genesismc.core.items.OrbOfOrigins.orb;
import static org.bukkit.ChatColor.RED;

public class Info extends SubCommand implements Listener {

    @EventHandler
    public void onMenuExit(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) return;
        if (e.getView().getTitle().equalsIgnoreCase("Help")) {
            if (e.getCurrentItem().getType() == Material.BARRIER || e.getCurrentItem().getType() == Material.SPECTRAL_ARROW) {
                Player p = (Player) e.getWhoClicked();
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                e.getWhoClicked().getInventory().close();
            }
        }
    }

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
    public void perform(Player p, String[] args) {
        if (args.length == 1) {
            Inventory help = Bukkit.createInventory(p, 54, "Help");
            PersistentDataContainer data = p.getPersistentDataContainer();
            OriginContainer origin = CraftApoliRewriten.toOriginContainer(data.get(new NamespacedKey(GenesisMC.getPlugin(), "origin"), PersistentDataType.BYTE_ARRAY));
            NamespacedKey key = new NamespacedKey(GenesisMC.getPlugin(), "originTag");

            ArrayList<String> originPowerNames = new ArrayList<>();
            ArrayList<String> originPowerDescriptions = new ArrayList<>();

            for (PowerContainer powers : origin.getPowerContainers()) {
                if (!powers.getHidden()) {
                    originPowerNames.add(origin.getName());
                    originPowerDescriptions.add(origin.getDescription());
                }
            }

            String minecraftItem = origin.getIcon();
            String item = minecraftItem.split(":")[1];
            ItemStack originIcon = new ItemStack(Material.valueOf(item.toUpperCase()));

            ItemStack close = itemProperties(new ItemStack(Material.BARRIER), RED + "Close", null, null, RED + "Cancel Choosing");
            ItemStack back = itemProperties(new ItemStack(Material.SPECTRAL_ARROW), ChatColor.AQUA + "Return", ItemFlag.HIDE_ENCHANTS, null, null);
            ItemStack lowImpact = itemProperties(new ItemStack(Material.GREEN_STAINED_GLASS_PANE), ChatColor.WHITE + "Impact: " + ChatColor.GREEN + "Low", null, null, null);
            ItemStack mediumImpact = itemProperties(new ItemStack(Material.YELLOW_STAINED_GLASS_PANE), ChatColor.WHITE + "Impact: " + ChatColor.YELLOW + "Medium", null, null, null);
            ItemStack highImpact = itemProperties(new ItemStack(Material.RED_STAINED_GLASS_PANE), ChatColor.WHITE + "Impact: " + ChatColor.RED + "High", null, null, null);


            ItemMeta originIconmeta = originIcon.getItemMeta();
            originIconmeta.setDisplayName(origin.getName());
            originIconmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            originIconmeta.setLore(cutStringIntoLists(origin.getDescription()));
            originIcon.setItemMeta(originIconmeta);

            ArrayList<ItemStack> contents = new ArrayList<>();
            long impact = origin.getImpact();

            for (int i = 0; i <= 53; i++) {
                if (i == 0 || i == 8) {
                    contents.add(close);
                } else if (i == 1) {                                          //impact
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
                } else if (i == 5) {                                           //impact
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
                    contents.add(originIcon);
                } else if ((i >= 20 && i <= 24) || (i >= 29 && i <= 33) || (i >= 38 && i <= 42)) {

                    if (originPowerNames.size() > 0) {
                        String powerName = originPowerNames.get(0);
                        String powerDescription = originPowerDescriptions.get(0);

                        ItemStack originPower = new ItemStack(Material.FILLED_MAP);

                        ItemMeta meta = originPower.getItemMeta();
                        meta.setDisplayName(powerName);
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        meta.setLore(cutStringIntoLists(powerDescription));
                        originPower.setItemMeta(meta);

                        contents.add(originPower);
                        originPowerNames.remove(0);
                        originPowerDescriptions.remove(0);

                    } else {
                        if (i >= 38) {
                            contents.add(new ItemStack(Material.AIR));
                        } else {
                            contents.add(new ItemStack(Material.PAPER));
                        }
                    }

                } else if (i == 49) {
                    contents.add(back);
                } else {
                    contents.add(new ItemStack(Material.AIR));
                }
            }
            help.setContents(contents.toArray(new ItemStack[0]));
            p.openInventory(help);
        }

    }
}
