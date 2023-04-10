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


        if (command.getName().equalsIgnoreCase("origins")) {
            if (args.length == 1) {
                List<String> arguments = new ArrayList<>();
                arguments.add("texture");
                arguments.add("commands");
                arguments.add("choose");
                if (sender.isOp()) {
                    arguments.add("config");
                    arguments.add("reload");
                    arguments.add("purge");
                }
                return arguments;
            } else if (args.length == 2) {
                List<String> playerNames = new ArrayList<>();
                Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().size()];
                Bukkit.getServer().getOnlinePlayers().toArray(players);
                for (int i = 0; i < players.length; i++) {
                    playerNames.add(players[i].getName());
                }
                return playerNames;
            } else if (args.length >= 3) {
                List<String> nothing = new ArrayList<>();
                return nothing;
            }





        } else if (command.getName().equalsIgnoreCase("shulker")) {
            if (args.length == 1) {
                List<String> arguments = new ArrayList<>();
                arguments.add("open");
                return arguments;
            }else if (args.length >= 2) {
                List<String> nothing = new ArrayList<>();
                return nothing;
            }




        } else if (command.getName().equalsIgnoreCase("beta")) {
            if (args.length == 1) {
                List<String> arguments = new ArrayList<>();
                if(GenesisDataFiles.getBeta().getString("update-beta").equalsIgnoreCase("true")) {
                    arguments.add("orboforigin");
                    arguments.add("waterprot");
                }
                return arguments;
            }else if (args.length >= 2) {
                List<String> nothing = new ArrayList<>();
                return nothing;
            }

        }
        return null;
        }
    }
