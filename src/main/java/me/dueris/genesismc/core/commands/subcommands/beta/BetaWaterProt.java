package me.dueris.genesismc.core.commands.subcommands.beta;

import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

import static me.dueris.genesismc.core.GenesisMC.waterProtectionEnchant;

public class BetaWaterProt extends SubCommand {
    @Override
    public String getName() {
        return "waterprot";
    }

    @Override
    public String getDescription() {
        return "spawns water prot book";
    }

    @Override
    public String getSyntax() {
        return "/beta waterprot";
    }

    @Override
    public void perform(Player p, String[] args) {
        if (p.hasPermission("genesismc.origins.cmd.beta") && p.hasPermission("genesismc.origins.beta.waterprot")) {
            ItemStack enchbook = new ItemStack(Material.ENCHANTED_BOOK);
            enchbook.addEnchantment(waterProtectionEnchant, 1);
            enchbook.setLore(Arrays.asList(ChatColor.GRAY + "Water Protection I"));
            p.getInventory().addItem(enchbook);
        }
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }
}
