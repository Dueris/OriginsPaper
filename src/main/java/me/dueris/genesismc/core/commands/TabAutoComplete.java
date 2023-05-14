package me.dueris.genesismc.core.commands;

import me.dueris.genesismc.core.api.OriginAPI;
import me.dueris.genesismc.core.api.factory.CustomOriginAPI;
import me.dueris.genesismc.core.files.GenesisDataFiles;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class TabAutoComplete implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        if (command.getName().equalsIgnoreCase("origin") && sender.hasPermission("genesismc.origin.cmd.main")) {
            if (args.length == 1) {
                List<String> arguments = new ArrayList<>();
                if(args[0].startsWith("i") || args[0].startsWith("in") || args[0].startsWith("inf") || args[0].startsWith("info")){
                    arguments.add("info");
                }
                if(args[0].startsWith("r") || args[0].startsWith("re") || args[0].startsWith("ref") || args[0].startsWith("refe") || args[0].startsWith("refer") || args[0].startsWith("refere") || args[0].startsWith("referen") || args[0].startsWith("referenc") || args[0].startsWith("reference") || args[0].startsWith("references")){
                    arguments.add("references");
                }


                if(sender.hasPermission("genesismc.origins.cmd.recipe")){
                    if(args[0].startsWith("r") || args[0].isEmpty() || args[0].startsWith("re") || args[0].startsWith("rec") || args[0].startsWith("reci") || args[0].startsWith("recip") || args[0].startsWith("recipe"));
                    arguments.add("recipe");
                }
                if (sender.hasPermission("genesismc.origins.cmd.choose")) {
                    if(args[0].startsWith("c") || args[0].isEmpty() || args[0].startsWith("ch") || args[0].startsWith("cho") || args[0].startsWith("choo") || args[0].startsWith("choos") || args[0].startsWith("choos") || args[0].startsWith("choose"));
                    arguments.add("choose");
                }
                if (sender.hasPermission("genesismc.origins.cmd.get")) {
                    if(args[0].startsWith("g") || args[0].isEmpty() || args[0].startsWith("ge") || args[0].startsWith("get"));
                    arguments.add("get");
                }
                if (sender.hasPermission("genesismc.origins.cmd.purge")) {
                    if(args[0].startsWith("p") || args[0].isEmpty() || args[0].startsWith("pu") || args[0].startsWith("pur") || args[0].startsWith("purg") || args[0].startsWith("purge"));
                    arguments.add("purge");
                }
                if (sender.hasPermission("genesismc.origins.cmd.enchant")) {
                    if(args[0].startsWith("e") || args[0].isEmpty() || args[0].startsWith("en") || args[0].startsWith("enc") || args[0].startsWith("ench") || args[0].startsWith("encha") || args[0].startsWith("enchan") || args[0].startsWith("enchant"));
                    arguments.add("enchant");
                }
                if(sender.hasPermission("genesis.origins.cmd.gui")){
                    if(args[0].startsWith("g") || args[0].isEmpty() || args[0].startsWith("gu") || args[0].startsWith("gui"));
                    arguments.add("gui");
                }
                if(sender.hasPermission("genesis.origins.cmd.has")){
                    if(args[0].startsWith("h") || args[0].isEmpty() || args[0].startsWith("ha") || args[0].startsWith("has"));
                    arguments.add("has");
                }
                if(sender.hasPermission("genesis.origins.cmd.set")){
                    if(args[0].startsWith("s") || args[0].isEmpty() || args[0].startsWith("se") || args[0].startsWith("set"));
                    arguments.add("set");
                }

                return arguments;
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("get") || args[0].equalsIgnoreCase("has") || args[0].equalsIgnoreCase("purge") || args[0].equalsIgnoreCase("set")) {
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

                List<String> arguments = new ArrayList();
                return arguments;

            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("has") || args[0].equalsIgnoreCase("set")) {
                    return OriginAPI.getLoadedOrigins();
                }else{
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

