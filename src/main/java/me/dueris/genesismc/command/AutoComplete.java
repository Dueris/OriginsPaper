package me.dueris.genesismc.command;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.powers.apoli.RecipePower;
import me.dueris.genesismc.registry.LayerContainer;
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

public class AutoComplete implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        if (command.getName().equals("resource")) {
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

