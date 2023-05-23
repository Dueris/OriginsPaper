package me.dueris.genesismc.core.commands;

import me.dueris.genesismc.core.api.OriginAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TabAutoComplete implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        if (command.getName().equalsIgnoreCase("origin") && sender.hasPermission("genesismc.origins.cmd.main")) {
            if (args.length == 1) {
                List<String> arguments = new ArrayList<>();
                if(sender.hasPermission("genesismc.origins.cmd.info")){
                    if(args[0].startsWith("i") || args[0].isEmpty() || args[0].startsWith("in") || args[0].startsWith("inf") || args[0].startsWith("info"))
                        arguments.add("info");
                }else{
                    arguments.remove("info");
                }
                if(sender.hasPermission("genesismc.origins.cmd.references")){
                    if(args[0].startsWith("r") || args[0].isEmpty() || args[0].startsWith("re") || args[0].startsWith("ref") || args[0].startsWith("refe") || args[0].startsWith("refer") || args[0].startsWith("refere") || args[0].startsWith("referen") || args[0].startsWith("referenc") || args[0].startsWith("reference") || args[0].startsWith("references"))
                        arguments.add("references");
                }else{
                    arguments.remove("references");
                }
                if(sender.hasPermission("genesismc.origins.cmd.recipe")){
                    if(args[0].startsWith("r") || args[0].isEmpty() || args[0].startsWith("re") || args[0].startsWith("rec") || args[0].startsWith("reci") || args[0].startsWith("recip") || args[0].startsWith("recipe"))
                        arguments.add("recipe");
                }else{
                    arguments.remove("recipe");
                }
                if (sender.hasPermission("genesismc.origins.cmd.get")) {
                    if(args[0].startsWith("g") || args[0].isEmpty() || args[0].startsWith("ge") || args[0].startsWith("get"))
                        arguments.add("get");
                }else{
                    arguments.remove("get");
                }
                if (sender.hasPermission("genesismc.origins.cmd.enchant")) {
                    if(args[0].startsWith("e") || args[0].isEmpty() || args[0].startsWith("en") || args[0].startsWith("enc") || args[0].startsWith("ench") || args[0].startsWith("encha") || args[0].startsWith("enchan") || args[0].startsWith("enchant"))
                        arguments.add("enchant");
                }else{
                    arguments.remove("enchant");
                }
                if(sender.hasPermission("genesis.origins.cmd.gui")){
                    if(args[0].startsWith("g") || args[0].isEmpty() || args[0].startsWith("gu") || args[0].startsWith("gui"))
                        arguments.add("gui");
                }else{
                    arguments.remove("gui");
                }
                if(sender.hasPermission("genesis.origins.cmd.has")){
                    if(args[0].startsWith("h") || args[0].isEmpty() || args[0].startsWith("ha") || args[0].startsWith("has"))
                        arguments.add("has");
                }else{
                    arguments.remove("has");
                }
                if(sender.hasPermission("genesis.origins.cmd.set")){
                    if(args[0].startsWith("s") || args[0].isEmpty() || args[0].startsWith("se") || args[0].startsWith("set"))
                        arguments.add("set");
                }else{
                    arguments.remove("set");
                }
                if (sender.hasPermission("genesis.origins.cmd.orb")) {
                    if (args[0].startsWith("g") || args[0].isEmpty() || args[0].startsWith("gi") || args[0].startsWith("giv") || args[0].startsWith("give"))
                        arguments.add("give");
                }else {
                    arguments.remove("give");
                }

                return arguments;
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("get") || args[0].equalsIgnoreCase("has") || args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("gui")) {
                    Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().size()];
                    List<String> playernames = new ArrayList<>();
                    Bukkit.getServer().getOnlinePlayers().toArray(players);
                    for (int i = 0; i < players.length; i++) {
                        playernames.add(players[i].getName());
                    }
                    if (args[0].equalsIgnoreCase("give")) playernames.addAll(Arrays.asList("@a", "@e", "@p", "@r", "@s"));
                    return playernames;

                } else if (args[0].equalsIgnoreCase("enchant")) {
                    List<String> enchantid = new ArrayList<>();
                    enchantid.add("genesis:water_protection");
                    return enchantid;

                }

                List<String> arguments = new ArrayList();
                return arguments;

            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("has") || args[0].equalsIgnoreCase("set")) {
                    return OriginAPI.getLoadedOrigins();
                } else if (args[0].equalsIgnoreCase("give")) {
                    return List.of("genesis:orb_of_origin");
                }
                else{
                    List<String> nothin = new ArrayList<>();
                    return nothin;
                }
            }else{
                List<String> nothin = new ArrayList<>();
                return nothin;
            }

        } else if (command.getName().equalsIgnoreCase("shulker")) {
            if (args.length == 1) {
                List<String> arguments = new ArrayList<>();
                arguments.add("open");
                return arguments;
            } else if (args.length >= 2) {
                List<String> nothing = new ArrayList<>();
                return nothing;
            }
        }
        return null;
    }

}

