package me.dueris.genesismc.commands;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.utils.LayerContainer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TabAutoComplete implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        if (command.getName().equalsIgnoreCase("origin") && sender.hasPermission("genesismc.origins.cmd.main")) {
            if (args.length == 1) {
                return Stream.of("enchant", "get", "give", "gui", "has", "info", "recipe", "set")
                        .filter(arg -> arg.startsWith(args[0].toLowerCase()))
                        .collect(Collectors.toList());
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

                    if (args[1].isBlank() || args[1].charAt(0) == '@') {
                        return Stream.of("@a", "@s", "@e", "@p", "@r")
                                .filter(arg -> arg.startsWith(args[1].toLowerCase()))
                                .collect(Collectors.toList());
                    }
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
                    return origins.stream()
                            .filter(arg -> (arg.startsWith(args[3].toLowerCase()) || arg.split(":")[1].startsWith(args[3].toLowerCase())))
                            .collect(Collectors.toList());
                }
                return new ArrayList<>();
            } else {
                return new ArrayList<>();
            }

        } else if (command.getName().equalsIgnoreCase("power") && sender.isOp()) { // /power<arg0> grant<arg1> Dueris<arg2> <powerFile><arg3>
            if (args.length == 1) {
                return Stream.of("dump", "grant", "has", "list", "remove")
                        .filter(arg -> arg.startsWith(args[0].toLowerCase()))
                        .collect(Collectors.toList());
            } else if (args.length == 2) {
                if (!args[0].equalsIgnoreCase("dump")) {
                    Collection<? extends Player> players = Bukkit.getOnlinePlayers();
                    List<String> playernames = new ArrayList<>();
                    for (Player player : players) playernames.add(player.getName());
                    playernames.addAll(Stream.of("@a", "@s", "@e", "@p", "@r")
                            .filter(arg -> arg.startsWith(args[1].toLowerCase()))
                            .collect(Collectors.toList()));
                    return playernames;
                } else {
                    List<String> pows = new ArrayList<>();
                    for (String string : CraftApoli.keyedPowerContainers.keySet()) {
                        pows.add(string);
                    }
                    return pows.stream()
                            .filter(arg -> (arg.startsWith(args[1].toLowerCase()) || arg.split(":")[1].startsWith(args[1].toLowerCase())))
                            .collect(Collectors.toList());
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("grant")
                        || args[0].equalsIgnoreCase("remove")
                        || args[0].equalsIgnoreCase("has")) {
                    List<String> pows = new ArrayList<>();
                    for (String string : CraftApoli.keyedPowerContainers.keySet()) {
                        if (!CraftApoli.keyedPowerContainers.get(string).isOriginMultipleSubPower()) {
                            pows.add(string);
                        }
                    }
                    return pows.stream()
                            .filter(arg -> (arg.startsWith(args[2].toLowerCase()) || arg.split(":")[1].startsWith(args[2].toLowerCase())))
                            .collect(Collectors.toList());
                }
            } else if (args.length == 4) {
                if (args[0].equalsIgnoreCase("grant")
                        || args[0].equalsIgnoreCase("remove")
                        || args[0].equalsIgnoreCase("has")) {
                    List<String> pows = new ArrayList<>();
                    for (LayerContainer layer : CraftApoli.getLayers()) {
                        pows.add(layer.getTag());
                    }
                    return pows.stream()
                            .filter(arg -> (arg.startsWith(args[3].toLowerCase()) || arg.split(":")[1].startsWith(args[3].toLowerCase())))
                            .collect(Collectors.toList());
                } else {
                    List<String> ba = new ArrayList<>();
                    return ba;
                }
            } else if (args.length >= 5) {
                List<String> ba = new ArrayList<>();
                return ba;
            }
        } else if (command.getName().equals("resource")) {
            if (args.length == 1) {
                Stream.of("change", "get", "has", "set")
                        .filter(arg -> arg.startsWith(args[0].toLowerCase()))
                        .collect(Collectors.toList());
            }
            if (args.length == 2) {
                Collection<? extends Player> players = Bukkit.getOnlinePlayers();
                List<String> playernames = new ArrayList<>();
                for (Player player : players) playernames.add(player.getName());
                playernames.addAll(Stream.of("@a", "@s", "@e", "@p", "@r")
                        .filter(arg -> arg.startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList()));
                return playernames;
            }
            if (args.length == 3) {
                List<String> pows = new ArrayList<>();
                for (String string : CraftApoli.keyedPowerContainers.keySet()) {
                    if (!CraftApoli.keyedPowerContainers.get(string).isOriginMultipleSubPower()) {
                        pows.add(string);
                    }
                }
                return pows.stream()
                        .filter(arg -> (arg.startsWith(args[3].toLowerCase()) || arg.split(":")[1].startsWith(args[3].toLowerCase())))
                        .collect(Collectors.toList());
            }
            if (args.length >= 4) {
                List<String> ba = new ArrayList<>();
                return ba;
            }
        }
        return null;
    }

}

