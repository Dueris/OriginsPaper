package me.dueris.genesismc.core.commands.subcommands.beta;

import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

import static me.dueris.genesismc.core.GenesisMC.waterProtectionEnchant;

public class EnchantCMD extends SubCommand {
    @Override
    public String getName() {
        return "enchant";
    }

    @Override
    public String getDescription() {
        return "the current only way to enchant something with a client with the current enchantments";
    }

    @Override
    public String getSyntax() {
        return "/origins enchant <enchantid> <level>";
    }

    @Override
    public void perform(Player p, String[] args) {
        if(args.length >= 1){
            if(args[1].equalsIgnoreCase("genesismc:waterprotection")){
                if(args[2].equalsIgnoreCase("1")){
                    p.getInventory().getItemInMainHand().addEnchantment(waterProtectionEnchant, 1);
                    p.getInventory().getItemInMainHand().setLore(Arrays.asList(ChatColor.GRAY + "Water Protection I"));
                }
                if(args[2].equalsIgnoreCase("2")){
                    p.getInventory().getItemInMainHand().addEnchantment(waterProtectionEnchant, 1);
                    p.getInventory().getItemInMainHand().setLore(Arrays.asList(ChatColor.GRAY + "Water Protection II"));
                }
                if(args[2].equalsIgnoreCase("3")){
                    p.getInventory().getItemInMainHand().addEnchantment(waterProtectionEnchant, 1);
                    p.getInventory().getItemInMainHand().setLore(Arrays.asList(ChatColor.GRAY + "Water Protection III"));
                }
                if(args[2].equalsIgnoreCase("4")){
                    p.getInventory().getItemInMainHand().addEnchantment(waterProtectionEnchant, 4);
                    p.getInventory().getItemInMainHand().setLore(Arrays.asList(ChatColor.GRAY + "Water Protection IV"));
                }
            }
        }
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }
}
