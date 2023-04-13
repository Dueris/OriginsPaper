package me.dueris.genesismc.core.commands.beta;

import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;

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
        return "/beta enchant <enchantid> <level>";
    }

    @Override
    public void perform(Player p, String[] args) {
        if(args.length == 1){
            String arg1 = args[1];
            String arg2 = args[2];
            if(arg1.toString().equalsIgnoreCase("genesismc:waterprotection")){
                if(arg2.toString().equalsIgnoreCase("1")){
                    p.getInventory().getItemInMainHand().addEnchantment(waterProtectionEnchant, 1);
                    p.getInventory().getItemInMainHand().setLore(Arrays.asList(ChatColor.GRAY + "Water Protection I"));
                }
            }
        }
    }
}
