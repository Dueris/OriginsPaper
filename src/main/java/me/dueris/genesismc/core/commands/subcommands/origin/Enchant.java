package me.dueris.genesismc.core.commands.subcommands.origin;

import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

import static me.dueris.genesismc.core.GenesisMC.waterProtectionEnchant;

public class Enchant extends SubCommand {
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
    public void perform(Player p, String[] args) {

        if(args[1].equalsIgnoreCase("genesis:water_protection") && args.length <= 2
        ){
            String level = "I";
            ItemMeta meta = p.getInventory().getItemInMainHand().getItemMeta();
            meta.setCustomModelData(1);
            p.getInventory().getItemInMainHand().setLore(Arrays.asList(ChatColor.GRAY + "Water Protection " + level));
            p.getInventory().getItemInMainHand().addUnsafeEnchantment(waterProtectionEnchant, 1);
        }else{
            p.sendMessage(ChatColor.RED + "Unable to add " + args[1].toString() +" to item.");
            p.sendMessage(args[0].toString());
            p.sendMessage(args[1].toString());
        }

    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }
}
