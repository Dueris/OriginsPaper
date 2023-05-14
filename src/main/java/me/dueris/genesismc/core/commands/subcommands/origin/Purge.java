package me.dueris.genesismc.core.commands.subcommands.origin;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

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
            PersistentDataContainer data = p.getPersistentDataContainer();
            if (args.length > 1) {
                Player target = Bukkit.getPlayer(args[1]);

                p.sendMessage("[GenesisMC] Removed origin of " + target.getDisplayName());
                target.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "");
                target.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "phantomid"), PersistentDataType.INTEGER, 1);
                target.getScoreboardTags().remove("chosen");
                target.removeScoreboardTag("chosen");
                target.sendMessage("Your origin has been removed by an operator");
            }else{
                p.removeScoreboardTag("chosen");
                p.sendMessage("Your origin has been removed by an operator");
                p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-null");
                p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "phantomid"), PersistentDataType.INTEGER, 1);
            }
        }
    }
}
