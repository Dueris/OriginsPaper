package me.dueris.genesismc.commands;

import me.dueris.genesismc.GenesisMC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import static me.dueris.genesismc.utils.BukkitColour.RED;

public class PlayerSelector {

    public static ArrayList<Player> playerSelector(CommandSender sender, String playerArg) {
        ArrayList<Player> players = new ArrayList<>();
        if(playerArg == "@a"){
            players.addAll(Bukkit.getOnlinePlayers());
        }else if(playerArg == "@e"){
            sender.sendMessage(Component.text("Only player may be affected by this command, but the provided selector includes entities.").color(TextColor.fromHexString(RED)));
            return players;
        }else if(playerArg == "@p"){
            if (sender instanceof Player p) {
                for (int i = 0; players.size() < 1; i++) {
                    if (p.getLocation().getNearbyPlayers(i).size() == 0) continue;
                    CraftPlayer craftPlayer = (CraftPlayer) p.getLocation().getNearbyPlayers(i).toArray()[0];
                    players.add(Bukkit.getPlayer(craftPlayer.getName()));
                }
            } else if (sender instanceof ConsoleCommandSender) {
                for (int i = 0; players.size() < 1; i++) {
                    if (GenesisMC.getPlugin().getServer().getWorlds().get(0).getSpawnLocation().getNearbyPlayers(i).size() == 0)
                        continue;
                    CraftPlayer craftPlayer = (CraftPlayer) GenesisMC.getPlugin().getServer().getWorlds().get(0).getSpawnLocation().getNearbyPlayers(i).toArray()[0];
                    players.add(Bukkit.getPlayer(craftPlayer.getName()));
                }
            }
        }else if(playerArg == "@r"){
            Random random = new Random();
            int randomInt = random.nextInt(Bukkit.getOnlinePlayers().size());
            players.add((Player) Bukkit.getOnlinePlayers().toArray()[randomInt]);
        }else if(playerArg == "@s"){
            if (sender instanceof Player p) players.add(p);
            else sender.sendMessage(Component.text("No player was found").color(TextColor.fromHexString(RED)));
        }else if(Bukkit.getPlayer(UUID.fromString(playerArg)) != null){
            players.add(Bukkit.getPlayer(UUID.fromString(playerArg)));
        }else{
            Player player = Bukkit.getPlayer(playerArg);
            if (player == null) {
                sender.sendMessage(Component.text("No player was found").color(TextColor.fromHexString(RED)));
                return new ArrayList<>();
            }
            players.add(player);
        }

        return players;
    }
}
