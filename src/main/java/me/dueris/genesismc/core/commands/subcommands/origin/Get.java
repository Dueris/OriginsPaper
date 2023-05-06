package me.dueris.genesismc.core.commands.subcommands.origin;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

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
            @Nullable String origintag = p.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
            if(args.length > 1){
                Player target = Bukkit.getPlayer(args[1]);
                //@Nullable String origintag = p.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
                if(origintag.equalsIgnoreCase("genesis:origin-enderian")){
                    p.sendMessage(target.getName() + " has the Enderian origin");
                }
                if(origintag.equalsIgnoreCase("genesis:origin-shulk")){
                    p.sendMessage(target.getName() + " has the Shulk origin");
                }
                if(origintag.equalsIgnoreCase("genesis:origin-human") || origintag.equalsIgnoreCase("genesis:origin-null")){
                    p.sendMessage(target.getName() + " has the Human origin");
                }
                if(origintag.equalsIgnoreCase("genesis:origin-arachnid")){
                    p.sendMessage(target.getName() + " has the Arachnid origin");
                }
                if(origintag.equalsIgnoreCase("genesis:origin-creep")){
                    p.sendMessage(target.getName() + " has the Creep origin");
                }
                if(origintag.equalsIgnoreCase("genesis:origin-phantom")){
                    p.sendMessage(target.getName() + " has the Phantom origin");
                }
                if(origintag.equalsIgnoreCase("genesis:origin-slimeling")){
                    p.sendMessage(target.getName() + " has the Slimeling origin");
                }
                if(origintag.equalsIgnoreCase("genesis:origin-feline")){
                    p.sendMessage(target.getName() + " has the Feline origin");
                }
                if(origintag.equalsIgnoreCase("genesis:origin-blazeborn")){
                    p.sendMessage(target.getName() + " has the Blazeborn origin");
                }
                if(origintag.equalsIgnoreCase("genesis:origin-starborn")){
                    p.sendMessage(target.getName() + " has the Starborne origin");
                }
                if(origintag.equalsIgnoreCase("genesis:origin-merling")){
                    p.sendMessage(target.getName() + " has the Merling origin");
                }
                if(origintag.equalsIgnoreCase("genesis:origin-allay")){
                    p.sendMessage(target.getName() + " has the Allay origin");
                }
                if(origintag.equalsIgnoreCase("genesis:origin-rabbit")){
                    p.sendMessage(target.getName() + " has the Rabbit origin");
                }
                if(origintag.equalsIgnoreCase("genesis:origin-bee")){
                    p.sendMessage(target.getName() + " has the Bumblebee origin");
                }
                if(origintag.equalsIgnoreCase("genesis:origin-elytrian")){
                    p.sendMessage(target.getName() + " has the Elytrian origin");
                }
                if(origintag.equalsIgnoreCase("genesis:origin-avian")){
                    p.sendMessage(target.getName() + " has the Avian origin");
                }
                if(origintag.equalsIgnoreCase("genesis:origin-piglin")){
                    p.sendMessage(target.getName() + " has the Piglin origin");
                }
                if(origintag.equalsIgnoreCase("genesis:origin-sculkling")){
                    p.sendMessage(target.getName() + " has the Sculkling origin");
                }

            }else{
                //self
                if(origintag.equalsIgnoreCase("genesis:origin-enderian")){
                    p.sendMessage(p.getName() + " has the Enderian origin");
                }
                if(origintag.equalsIgnoreCase("genesis:origin-shulk")){
                    p.sendMessage(p.getName() + " has the Shulk origin");
                }
                if(origintag.equalsIgnoreCase("genesis:origin-human") || origintag.equalsIgnoreCase("genesis:origin-null")){
                    p.sendMessage(p.getName() + " has the Human origin");
                }
                if(origintag.equalsIgnoreCase("genesis:origin-arachnid")){
                    p.sendMessage(p.getName() + " has the Arachnid origin");
                }
                if(origintag.equalsIgnoreCase("genesis:origin-creep")){
                    p.sendMessage(p.getName() + " has the Creep origin");
                }
                if(origintag.equalsIgnoreCase("genesis:origin-phantom")){
                    p.sendMessage(p.getName() + " has the Phantom origin");
                }
                if(origintag.equalsIgnoreCase("genesis:origin-slimeling")){
                    p.sendMessage(p.getName() + " has the Slimeling origin");
                }
                if(origintag.equalsIgnoreCase("genesis:origin-feline")){
                    p.sendMessage(p.getName() + " has the Feline origin");
                }
                if(origintag.equalsIgnoreCase("genesis:origin-blazeborn")){
                    p.sendMessage(p.getName() + " has the Blazeborn origin");
                }
                if(origintag.equalsIgnoreCase("genesis:origin-starborne")){
                    p.sendMessage(p.getName() + " has the Starborne origin");
                }
                if(origintag.equalsIgnoreCase("genesis:origin-merling")){
                    p.sendMessage(p.getName() + " has the Merling origin");
                }
                if(origintag.equalsIgnoreCase("genesis:origin-allay")){
                    p.sendMessage(p.getName() + " has the Allay origin");
                }
                if(origintag.equalsIgnoreCase("genesis:origin-rabbit")){
                    p.sendMessage(p.getName() + " has the Rabbit origin");
                }
                if(origintag.equalsIgnoreCase("genesis:origin-bee")){
                    p.sendMessage(p.getName() + " has the Bumblebee origin");
                }
                if(origintag.equalsIgnoreCase("genesis:origin-elytrian")){
                    p.sendMessage(p.getName() + " has the Elytrian origin");
                }
                if(origintag.equalsIgnoreCase("genesis:origin-avian")){
                    p.sendMessage(p.getName() + " has the Avian origin");
                }
                if(origintag.equalsIgnoreCase("genesis:origin-piglin")){
                    p.sendMessage(p.getName() + " has the Piglin origin");
                }
                if(origintag.equalsIgnoreCase("genesis:origin-sculkling")){
                    p.sendMessage(p.getName() + " has the Sculkling origin");
                }

            }

        }

    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }
}
