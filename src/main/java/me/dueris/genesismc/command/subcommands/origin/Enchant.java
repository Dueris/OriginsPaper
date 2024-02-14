package me.dueris.genesismc.command.subcommands.origin;

import me.dueris.genesismc.command.PlayerSelector;
import me.dueris.genesismc.command.subcommands.SubCommand;
import me.dueris.genesismc.content.enchantment.AnvilHandler;
import me.dueris.genesismc.util.BukkitColour;
import me.dueris.genesismc.util.LangConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class Enchant extends SubCommand {

    public static EnumSet<Material> wearable;

    static {
        wearable = EnumSet.of(Material.ENCHANTED_BOOK, Material.BOOK, Material.PUMPKIN, Material.CARVED_PUMPKIN, Material.ELYTRA, Material.TURTLE_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_BOOTS, Material.LEATHER_LEGGINGS, Material.LEATHER_HELMET, Material.CHAINMAIL_BOOTS, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_HELMET, Material.CHAINMAIL_LEGGINGS, Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS, Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS, Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS);
    }

    @Override
    public String getName() {
        return "enchant";
    }

    @Override
    public String getDescription() {
        return LangConfig.getLocalizedString(Bukkit.getConsoleSender(), "command.origin.enchant.description");
    }

    @Override
    public String getSyntax() {
        return "/origin enchant <enchantid> <level-amount>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (sender.hasPermission("genesismc.origins.cmd.enchant")) {
            if (args.length == 1) {
                sender.sendMessage(Component.text(LangConfig.getLocalizedString(sender, "command.origin.enchant.noPlayer")).color(TextColor.fromHexString(BukkitColour.RED)));
                return;
            }
            if (args.length == 2) {
                sender.sendMessage(Component.text(LangConfig.getLocalizedString(sender, "command.origin.enchant.noEnchant")).color(TextColor.fromHexString(BukkitColour.RED)));
                return;
            }
            ArrayList<Player> players = PlayerSelector.playerSelector(sender, args[1]);

            if (players.size() == 0) return;

            for (Player p : players) {
                if (!wearable.contains(p.getInventory().getItemInMainHand().getType())) {
                    sender.sendMessage(Component.text(LangConfig.getLocalizedString(p, "command.origin.enchant.badItem").replace("%player%", p.getName())).color(TextColor.fromHexString(BukkitColour.RED)));
                    continue;
                }

                int level = 1;
                if (args.length == 4) {
                    try {
                        level = Integer.parseInt(args[3]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(Component.text(LangConfig.getLocalizedString(p, "command.origin.enchant.wrongNumber")).color(TextColor.fromHexString(BukkitColour.RED)));
                    }
                }

                if (args[2].equals("origins:water_protection")) {
                    if (level > 4 || level < 1) {
                        sender.sendMessage(Component.text(LangConfig.getLocalizedString(sender, "command.origin.enchant.waterProtLevelLimit")).color(TextColor.fromHexString(BukkitColour.RED)));
                        return;
                    }

                    String romanLevel = AnvilHandler.numberToRomanNum(level);
                    ItemMeta meta = p.getInventory().getItemInMainHand().getItemMeta();
                    meta.setCustomModelData(level);
                    p.getInventory().getItemInMainHand().setLore(List.of(ChatColor.GRAY + "Water Protection " + romanLevel));
                    p.getInventory().getItemInMainHand().addEnchantment(AnvilHandler.bukkitEnchantment, level);
                    sender.sendMessage("Applied enchantment " +
                            ChatColor.GRAY + "{water_prot}".replace("{water_prot}", "Water Protection " + AnvilHandler.numberToRomanNum(level)) +
                            ChatColor.WHITE + " to {target}'s item".replace("{target}", p.getName())
                    );
                }
            }
        }
    }
}
