package me.dueris.genesismc.core.commands;

import me.dueris.genesismc.core.files.GenesisDataFiles;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TabAutoComplete implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        if (command.getName().equalsIgnoreCase("origin") && sender.hasPermission("genesismc.origin.cmd.main")) {
            if (args.length == 1) {
                List<String> arguments = new ArrayList<>();
                arguments.add("info");
                if(sender.hasPermission("genesismc.origins.cmd.recipe")){
                    arguments.add("recipe");
                }
                if (sender.hasPermission("genesismc.origins.cmd.choose")) {
                    arguments.add("choose");
                }
                if (sender.hasPermission("genesismc.origins.cmd.get")) {
                    arguments.add("get");
                }
                if (sender.hasPermission("genesismc.origins.cmd.purge")) {
                    arguments.add("purge");
                }
                if (sender.hasPermission("genesismc.origins.cmd.enchant")) {
                    arguments.add("enchant");
                }
                if(sender.hasPermission("genesis.origins.cmd.gui")){
                    arguments.add("gui");
                }
                if(sender.hasPermission("genesis.origins.cmd.has")){
                    arguments.add("has");
                }
                if(sender.hasPermission("genesis.origins.cmd.set")){
                    arguments.add("set");
                }

                return arguments;
            } else if (args.length >= 3) {
                    List<String> nothing = new ArrayList<>();
                    return nothing;
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("purge")) {
                    Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().size()];
                    List<String> playernames = new ArrayList<>();
                    Bukkit.getServer().getOnlinePlayers().toArray(players);
                    for (int i = 0; i < players.length; i++) {
                        playernames.add(players[i].getName());
                    }
                    return playernames;
                } else if (args[0].equalsIgnoreCase("get")) {
                    Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().size()];
                    List<String> playernames = new ArrayList<>();
                    Bukkit.getServer().getOnlinePlayers().toArray(players);
                    for (int i = 0; i < players.length; i++) {
                        playernames.add(players[i].getName());
                    }
                    return playernames;

                } else if (args[0].equalsIgnoreCase("enchant")) {
                    List<String> enchantid = new ArrayList<>();
                    enchantid.add("genesis:water_protection");
                    return enchantid;

                }

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

        } else if (command.getName().equalsIgnoreCase("give")) {
            if(args.length == 2){
                List<String> arguments = new ArrayList<>();
                arguments.add("genesis:orb_of_origin");
                return arguments;
            }
        }
        return null;
        }

    }

