package me.dueris.genesismc.core.commands.subcommands;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;

public class Purge extends SubCommand implements Listener {
    @Override
    public String getName() {
        return "purge";
    }

    @Override
    public String getDescription() {
        return "removes player origin";
    }

    @Override
    public String getSyntax() {
        return "/origins purge <player>";
    }

    @Override
    public void perform(Player p, String[] args) {
        if (p.hasPermission("genesismc.origins.cmd.purge")) {
            if (args.length == 1) {
                Player target = Bukkit.getPlayer(args[1]);

                p.sendMessage("[GenesisMC] Removed origin of " + target.getDisplayName());
                target.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 0);
                target.getScoreboardTags().remove("chosen");

                target.sendMessage("Your origin has been removed by an operator");
            }
        }
    }
}
