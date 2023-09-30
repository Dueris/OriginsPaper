package me.dueris.genesismc.commands;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.utils.LayerContainer;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class TabAutoComplete implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        if (command.getName().equalsIgnoreCase("origin") && sender.hasPermission("genesismc.origins.cmd.main")) {
            if (args.length == 1) {
                List<String> arguments = new ArrayList<>();
                if (sender.hasPermission("genesismc.origins.cmd.info")) {
                    if (args[0].equals("i") || args[0].isEmpty() || args[0].equals("in") || args[0].equals("inf") || args[0].equals("info"))
                        arguments.add("info");
                } else {
                    arguments.remove("info");
                }
                if (sender.hasPermission("genesismc.origins.cmd.references")) {
                    if (args[0].equals("r") || args[0].isEmpty() || args[0].equals("re") || args[0].equals("ref") || args[0].equals("refe") || args[0].equals("refer") || args[0].equals("refere") || args[0].equals("referen") || args[0].equals("referenc") || args[0].equals("reference") || args[0].equals("references"))
                        arguments.add("references");
                } else {
                    arguments.remove("references");
                }
                if (sender.hasPermission("genesismc.origins.cmd.recipe")) {
                    if (args[0].equals("r") || args[0].isEmpty() || args[0].equals("re") || args[0].equals("rec") || args[0].equals("reci") || args[0].equals("recip") || args[0].equals("recipe"))
                        arguments.add("recipe");
                } else {
                    arguments.remove("recipe");
                }
                if (sender.hasPermission("genesismc.origins.cmd.get")) {
                    if (args[0].equals("g") || args[0].isEmpty() || args[0].equals("ge") || args[0].equals("get"))
                        arguments.add("get");
                } else {
                    arguments.remove("get");
                }
                if (sender.hasPermission("genesismc.origins.cmd.enchant")) {
                    if (args[0].equals("e") || args[0].isEmpty() || args[0].equals("en") || args[0].equals("enc") || args[0].equals("ench") || args[0].equals("encha") || args[0].equals("enchan") || args[0].equals("enchant"))
                        arguments.add("enchant");
                } else {
                    arguments.remove("enchant");
                }
                if (sender.hasPermission("genesis.origins.cmd.gui")) {
                    if (args[0].equals("g") || args[0].isEmpty() || args[0].equals("gu") || args[0].equals("gui"))
                        arguments.add("gui");
                } else {
                    arguments.remove("gui");
                }
                if (sender.hasPermission("genesis.origins.cmd.has")) {
                    if (args[0].equals("h") || args[0].isEmpty() || args[0].equals("ha") || args[0].equals("has"))
                        arguments.add("has");
                } else {
                    arguments.remove("has");
                }
                if (sender.hasPermission("genesis.origins.cmd.set")) {
                    if (args[0].equals("s") || args[0].isEmpty() || args[0].equals("se") || args[0].equals("set"))
                        arguments.add("set");
                } else {
                    arguments.remove("set");
                }
                if (sender.hasPermission("genesis.origins.cmd.orb")) {
                    if (args[0].equals("g") || args[0].isEmpty() || args[0].equals("gi") || args[0].equals("giv") || args[0].equals("give"))
                        arguments.add("give");
                } else {
                    arguments.remove("give");
                }
                if (sender.hasPermission("genesis.origins.cmd.bug")) {
                    if (args[0].equals("b") || args[0].isEmpty() || args[0].equals("bu") || args[0].equals("bug"))
                        arguments.add("bug");
                } else {
                    arguments.remove("bug");
                }

                return arguments;
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("get") || args[0].equalsIgnoreCase("has") || args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("gui") || args[0].equalsIgnoreCase("enchant")) {
                    Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().size()];
                    List<String> playernames = new ArrayList<>();
                    Bukkit.getServer().getOnlinePlayers().toArray(players);
                    for (Player player : players) playernames.add(player.getName());

                    for (int i = 0; i < playernames.size(); i++) {
                        String name = playernames.get(i);
                        if (name.length() < args[1].length()) {
                            playernames.remove(name);
                            continue;
                        }
                        if (!args[1].equals(name.substring(0, args[1].length()))) playernames.remove(name);
                    }

                    if (args[1].isBlank() || args[1].charAt(0) == '@')
                        playernames.addAll(Arrays.asList("@a", "@e", "@p", "@r", "@s"));
                    return playernames;

                }

                return new ArrayList<>();

            } else if (args.length == 3) {

                if (args[0].equalsIgnoreCase("get") || args[0].equalsIgnoreCase("has") || args[0].equalsIgnoreCase("set")) {
                    ArrayList<LayerContainer> layers = CraftApoli.getLayers();
                    for (int i = 0; i < layers.size(); i++) {
                        String tag = layers.get(i).getTag();
                        if (tag.length() < args[2].length()) {
                            layers.remove(layers.get(i));
                            continue;
                        }
                        if (!tag.equals(layers.get(i).getTag().substring(0, tag.length())))
                            layers.remove(layers.get(i));
                    }
                    ArrayList<String> layerTags = new ArrayList<>();
                    for (LayerContainer layer : layers) layerTags.add(layer.getTag());
                    return layerTags;
                }
                if (args[0].equalsIgnoreCase("give")) {
                    return List.of("origins:orb_of_origin");
                } else if (args[0].equalsIgnoreCase("enchant")) {
                    return List.of("origins:water_protection");
                } else if (args[0].equals("gui")) {
                    ArrayList<LayerContainer> layers = CraftApoli.getLayers();
                    for (int i = 0; i < layers.size(); i++) {
                        String tag = layers.get(i).getTag();
                        if (tag.length() < args[2].length()) {
                            layers.remove(layers.get(i));
                            continue;
                        }
                        if (!tag.equals(layers.get(i).getTag().substring(0, tag.length())))
                            layers.remove(layers.get(i));
                    }
                    ArrayList<String> layerTags = new ArrayList<>();
                    for (LayerContainer layer : layers) layerTags.add(layer.getTag());
                    return layerTags;
                } else {
                    return new ArrayList<>();
                }
            } else if (args.length == 4) {
                if (args[0].equalsIgnoreCase("has") || args[0].equalsIgnoreCase("set")) {
                    ArrayList<String> origins = CraftApoli.getOriginTags();
                    origins.removeIf(origin -> !origin.startsWith(args[3]));
                    return origins;
                }
                return new ArrayList<>();
            } else {
                return new ArrayList<>();
            }

        } else if (command.getName().equalsIgnoreCase("shulker")) {
            if (args.length == 1) {
                List<String> arguments = new ArrayList<>();
                arguments.add("open");
                return arguments;
            } else if (args.length == 2) {
                if (sender.hasPermission("genesism.origins.cmd.othershulk")) {
                    ArrayList<String> playerNames = new ArrayList<>();
                    for (Player player : Bukkit.getOnlinePlayers()) playerNames.add(player.getName());
                    return playerNames;
                } else return new ArrayList<>();
            }
        } else if (command.getName().equalsIgnoreCase("power")) {
            if (args.length == 1) {
                List<String> arguments = new ArrayList<>();
                arguments.add("clear");
                arguments.add("grant");
                arguments.add("has");
                arguments.add("list");
                arguments.add("remove");
                arguments.add("revoke");
                arguments.add("revokeall");
                arguments.add("sources");
                return arguments;
            } else if (args.length == 2) {
                Collection<? extends Player> players = Bukkit.getOnlinePlayers();
                List<String> playernames = new ArrayList<>();
                for (Player player : players) playernames.add(player.getName());
                playernames.add("@a");
                playernames.add("@s");
                playernames.add("@e");
                playernames.add("@p");
                playernames.add("@r");
                return playernames;
            } else if (args.length == 3) {
                List<String> argS = new ArrayList<>();
                for (PowerContainer c : CraftApoli.getPowers()) {
                    if (args[1].equalsIgnoreCase("has")) {
                        argS.add(c.getTag());
                    }
                    if (args[1].equalsIgnoreCase("grant")) {
                        argS.add(c.getTag());
                    }
                    if (args[1].equalsIgnoreCase("remove")) {
                        argS.add(c.getTag());
                    }
                    if (args[1].equalsIgnoreCase("revoke")) {
                        argS.add(c.getTag());
                    }
                }

                for (OriginContainer origin : CraftApoli.getOrigins()) {
                    if (args[1].equalsIgnoreCase("revokeall")) {
                        argS.add(origin.getTag());
                    }
                }

                return argS;

            } else if (args.length >= 4) {
                List<String> ba = new ArrayList<>();
                return ba;
            }
        }
        return null;
    }

}

