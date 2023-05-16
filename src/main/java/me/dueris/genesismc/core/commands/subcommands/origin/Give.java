package me.dueris.genesismc.core.commands.subcommands.origin;

import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Give extends SubCommand {
    @Override
    public String getName() {return "give";}

    @Override
    public String getDescription() {
        return "gives players origins specific items";
    }

    @Override
    public String getSyntax() {
        return "/origin give <player> <item> <amount>";
    }

    @Override
    public void perform(Player p, String[] args) {
        ArrayList<Player> players = new ArrayList<>();
        if (args[1].equals("@a")) {
            players.addAll(Bukkit.getOnlinePlayers());
        } else if (args[1].equals("@e")) {
            p.sendMessage("not done yet, please come back later");
        } else if (args[1].equals("@p")) {
            //for (int i = 0; players.size() < 1; i++)
                //players.add(Objects.requireNonNull(Bukkit.getPlayer(p.getLocation().getNearbyPlayers(i).toString())).getName());
            p.sendMessage("not done yet, please come back later");
        } else if (args[1].equals("@r")) {
            Random random = new Random();
            int randomInt = random.nextInt(Bukkit.getOnlinePlayers().size());
            players.add((Player) Bukkit.getOnlinePlayers().toArray()[randomInt]);
        } else if (args[1].equals("@s")) {
            players.add(p);
        } else {
            try {
                players.add(Bukkit.getPlayer(args[1]));
            } catch (Exception e) {}
        }

        p.sendMessage(players.toString());
    }
}
