package me.dueris.genesismc.core.commands.subcommands.origin;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class Get extends SubCommand {
    @Override
    public String getName() {
        return "get";
    }

    @Override
    public String getDescription() {
        return "gets origin of player";
    }

    @Override
    public String getSyntax() {
        return "/origin get <player_name>";
    }

    @Override
    public void perform(Player p, String[] args) {
        if (p.hasPermission("genesismc.origins.cmd.get")) {
            int originidp = p.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
            if(args.length > 1){
                Player target = Bukkit.getPlayer(args[1]);
                int originid = target.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
                if(originid == 0401065){
                    p.sendMessage(target.getName() + " has the Enderian origin");
                }
                if(originid == 6503044){
                    p.sendMessage(target.getName() + " has the Shulk origin");
                }
                if(originid == 0004013 || originid == 1 || originid == 0){
                    p.sendMessage(target.getName() + " has the Human origin");
                }
                if(originid == 1709012){
                    p.sendMessage(target.getName() + " has the Arachnid origin");
                }
                if(originid == 2356555){
                    p.sendMessage(target.getName() + " has the Creep origin");
                }
                if(originid == 7300041){
                    p.sendMessage(target.getName() + " has the Phantom origin");
                }
                if(originid == 2304045){
                    p.sendMessage(target.getName() + " has the Slimeling origin");
                }
                if(originid == 9602042){
                    p.sendMessage(target.getName() + " has the Vexian origin");
                }
                if(originid == 9811027){
                    p.sendMessage(target.getName() + " has the Blazeborn origin");
                }
                if(originid == 7303065){
                    p.sendMessage(target.getName() + " has the Starborne origin");
                }
                if(originid == 1310018){
                    p.sendMessage(target.getName() + " has the Merling origin");
                }
                if(originid == 1205048){
                    p.sendMessage(target.getName() + " has the Allay origin");
                }
                if(originid == 5308033){
                    p.sendMessage(target.getName() + " has the Rabbit origin");
                }
                if(originid == 8906022){
                    p.sendMessage(target.getName() + " has the Bumblebee origin");
                }
                if(originid == 6211006){
                    p.sendMessage(target.getName() + " has the Elytrian origin");
                }
                if(originid == 4501011){
                    p.sendMessage(target.getName() + " has the Avian origin");
                }
                if(originid == 6211021){
                    p.sendMessage(target.getName() + " has the Piglin origin");
                }
                if(originid == 4307015){
                    p.sendMessage(target.getName() + " has the Dragonborne origin");
                }

            }else{
                //self
                if(originidp == 0401065){
                    p.sendMessage(p.getName() + " has the Enderian origin");
                }
                if(originidp == 6503044){
                    p.sendMessage(p.getName() + " has the Shulk origin");
                }
                if(originidp == 0004013 || originidp == 1 || originidp == 0){
                    p.sendMessage(p.getName() + " has the Human origin");
                }
                if(originidp == 1709012){
                    p.sendMessage(p.getName() + " has the Arachnid origin");
                }
                if(originidp == 2356555){
                    p.sendMessage(p.getName() + " has the Creep origin");
                }
                if(originidp == 7300041){
                    p.sendMessage(p.getName() + " has the Phantom origin");
                }
                if(originidp == 2304045){
                    p.sendMessage(p.getName() + " has the Slimeling origin");
                }
                if(originidp == 9602042){
                    p.sendMessage(p.getName() + " has the Vexian origin");
                }
                if(originidp == 9811027){
                    p.sendMessage(p.getName() + " has the Blazeborn origin");
                }
                if(originidp == 7303065){
                    p.sendMessage(p.getName() + " has the Starborne origin");
                }
                if(originidp == 1310018){
                    p.sendMessage(p.getName() + " has the Merling origin");
                }
                if(originidp == 1205048){
                    p.sendMessage(p.getName() + " has the Allay origin");
                }
                if(originidp == 5308033){
                    p.sendMessage(p.getName() + " has the Rabbit origin");
                }
                if(originidp == 8906022){
                    p.sendMessage(p.getName() + " has the Bumblebee origin");
                }
                if(originidp == 6211006){
                    p.sendMessage(p.getName() + " has the Elytrian origin");
                }
                if(originidp == 4501011){
                    p.sendMessage(p.getName() + " has the Avian origin");
                }
                if(originidp == 6211021){
                    p.sendMessage(p.getName() + " has the Piglin origin");
                }
                if(originidp == 4307015){
                    p.sendMessage(p.getName() + " has the Dragonborne origin");
                }

            }

        }

    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }
}
