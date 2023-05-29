package me.dueris.genesismc.core.commands.subcommands.origin;

import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Random;

import static me.dueris.genesismc.core.items.OrbOfOrigins.orb;
import static org.bukkit.ChatColor.RED;

public class Give extends SubCommand {
    @Override
    public String getName() {
        return "give";
    }

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
        if (!p.hasPermission("genesismc.origins.cmd.give")) return;
        ArrayList<Player> players = new ArrayList<>();

        if (args.length < 3) {
            p.sendMessage(RED + "Invalid Args!");
            return;
        }

        if (args[1].equals("@a")) {
            players.addAll(Bukkit.getOnlinePlayers());

        } else if (args[1].equals("@e")) {
            p.sendMessage(RED + "Only player may be affected by this command, but the provided selector includes entities.");
            return;

        } else if (args[1].equals("@p")) {
            for (int i = 0; players.size() < 1; i++) {
                if (p.getLocation().getNearbyPlayers(i).size() == 0) continue;
                CraftPlayer craftPlayer = (CraftPlayer) p.getLocation().getNearbyPlayers(i).toArray()[0];
                players.add(Bukkit.getPlayer(craftPlayer.getName()));
            }

        } else if (args[1].equals("@r")) {
            Random random = new Random();
            int randomInt = random.nextInt(Bukkit.getOnlinePlayers().size());
            players.add((Player) Bukkit.getOnlinePlayers().toArray()[randomInt]);

        } else if (args[1].equals("@s")) {
            players.add(p);
        } else {
            try {
                players.add(Bukkit.getPlayer(args[1]));
            } catch (Exception e) {
                p.sendMessage(RED + "Player not found!");
            }
        }

        if (players.size() == 0) return;

        ItemStack item;
        if (args[2].equals("genesis:orb_of_origin")) {
            item = orb.clone();
        } else return;

        if (args.length == 4) {
            try {
                item.setAmount(Integer.parseInt(args[3].strip()));
            } catch (Exception e) {
                p.sendMessage(RED + "Please enter a valid number!");
                return;
            }
        } else {
            item.setAmount(1);
        }

        for (Player player : players) {
            if (args[2].equals("genesis:orb_of_origin")) player.getInventory().addItem(item);
        }
    }
}
