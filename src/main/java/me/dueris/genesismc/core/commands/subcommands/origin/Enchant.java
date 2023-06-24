package me.dueris.genesismc.core.commands.subcommands.origin;

import me.dueris.genesismc.core.commands.PlayerSelector;
import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static me.dueris.genesismc.core.GenesisMC.waterProtectionEnchant;
import static me.dueris.genesismc.core.utils.BukkitColour.RED;

public class Enchant extends SubCommand {

    public static EnumSet<Material> wearable;

    static {
        wearable = EnumSet.of(Material.ENCHANTED_BOOK, Material.PUMPKIN, Material.CARVED_PUMPKIN, Material.ELYTRA, Material.TURTLE_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_BOOTS, Material.LEATHER_LEGGINGS, Material.LEATHER_HELMET, Material.CHAINMAIL_BOOTS, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_HELMET, Material.CHAINMAIL_LEGGINGS, Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS, Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS, Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS);
    }

    @Override
    public String getName() {
        return "enchant";
    }

    @Override
    public String getDescription() {
        return "enchants item with genesis enchantment";
    }

    @Override
    public String getSyntax() {
        return "/origin enchant <enchantid> <level-amount>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (sender.hasPermission("genesismc.origins.cmd.enchant")) {
            if (args.length == 1) {
                sender.sendMessage(Component.text("No player specified!").color(TextColor.fromHexString(RED)));
                return;
            }
            if (args.length == 2) {
                sender.sendMessage(Component.text("No enchantment specified!").color(TextColor.fromHexString(RED)));
                return;
            }
            ArrayList<Player> players = PlayerSelector.playerSelector(sender, args[1]);

            if (players.size() == 0) return;

            for (Player p : players) {
                if (!wearable.contains(p.getInventory().getItemInMainHand().getType())) {
                    sender.sendMessage(Component.text(p.getName() + " Is not holding an enchant-able item!").color(TextColor.fromHexString(RED)));
                    continue;
                }

                int level = 1;
                if (args.length == 4) {
                    try {
                        level = Integer.parseInt(args[3]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(Component.text("Please enter a valid number!").color(TextColor.fromHexString(RED)));
                    }
                }

                if (args[2].equals("genesis:water_protection")) {
                    if (level > 4 || level < 1) {
                        sender.sendMessage(Component.text("The level for water protection must be between 1 and 4!").color(TextColor.fromHexString(RED)));
                        return;
                    }

                    String romanLevel = "I";
                    if (level == 2) romanLevel = "II";
                    if (level == 3) romanLevel = "III";
                    if (level == 4) romanLevel = "IV";
                    ItemMeta meta = p.getInventory().getItemInMainHand().getItemMeta();
                    meta.setCustomModelData(level);
                    p.getInventory().getItemInMainHand().setLore(List.of(ChatColor.GRAY + "Water Protection " + romanLevel));
                    p.getInventory().getItemInMainHand().addUnsafeEnchantment(waterProtectionEnchant, level);
                }
            }

//            if (args.length != 1 && wearable.contains(p.getInventory().getItemInMainHand().getType())) {
//                if (args[1].equalsIgnoreCase("genesis:water_protection") && args.length == 2) {
//                    String level = "I";
//                    ItemMeta meta = p.getInventory().getItemInMainHand().getItemMeta();
//                    meta.setCustomModelData(1);
//                    p.getInventory().getItemInMainHand().setLore(List.of(ChatColor.GRAY + "Water Protection " + level));
//                    p.getInventory().getItemInMainHand().addUnsafeEnchantment(waterProtectionEnchant, 1);
//                } else if (args.length == 3) {
//                    if (args[2].equalsIgnoreCase("1")) {
//                        String level = "I";
//                        ItemMeta meta = p.getInventory().getItemInMainHand().getItemMeta();
//                        meta.setCustomModelData(1);
//                        p.getInventory().getItemInMainHand().setLore(List.of(ChatColor.GRAY + "Water Protection " + level));
//                        p.getInventory().getItemInMainHand().addUnsafeEnchantment(waterProtectionEnchant, 1);
//                    } else if (args[2].equalsIgnoreCase("2")) {
//                        String level = "II";
//                        ItemMeta meta = p.getInventory().getItemInMainHand().getItemMeta();
//                        meta.setCustomModelData(2);
//                        p.getInventory().getItemInMainHand().setLore(List.of(ChatColor.GRAY + "Water Protection " + level));
//                        p.getInventory().getItemInMainHand().addUnsafeEnchantment(waterProtectionEnchant, 2);
//                    } else if (args[2].equalsIgnoreCase("3")) {
//                        String level = "III";
//                        ItemMeta meta = p.getInventory().getItemInMainHand().getItemMeta();
//                        meta.setCustomModelData(3);
//                        p.getInventory().getItemInMainHand().setLore(List.of(ChatColor.GRAY + "Water Protection " + level));
//                        p.getInventory().getItemInMainHand().addUnsafeEnchantment(waterProtectionEnchant, 3);
//                    } else if (args[2].equalsIgnoreCase("4")) {
//                        String level = "IV";
//                        ItemMeta meta = p.getInventory().getItemInMainHand().getItemMeta();
//                        meta.setCustomModelData(4);
//                        p.getInventory().getItemInMainHand().setLore(List.of(ChatColor.GRAY + "Water Protection " + level));
//                        p.getInventory().getItemInMainHand().addUnsafeEnchantment(waterProtectionEnchant, 4);
//                    } else {
//                        p.sendMessage(ChatColor.RED + "Unable to add " + args[1] + " to item because the level is too big.");
//                    }
//                } else {
//                    p.sendMessage(ChatColor.RED + "Unable to add " + args[1] + " to item.");
//                }
//
//            } else {
//                p.sendMessage(ChatColor.RED + "Invalid Args! Please hold a wearable item");
//            }
        }
    }
}
