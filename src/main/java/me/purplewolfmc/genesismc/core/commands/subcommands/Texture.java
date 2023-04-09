package me.purplewolfmc.genesismc.core.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

public class Texture extends SubCommand {
    @Override
    public String getName() {
        return "texture";
    }

    @Override
    public String getDescription() {
        return "enables custom texture pack for the plugin";
    }

    @Override
    public String getSyntax() {
        return "/origins texture";
    }

    @Override
    public void perform(Player p, String[] args) {
        if (p.hasPermission("genesismc.origins.cmd.texture")) {
            p.setTexturePack("https://drive.google.com/uc?export=download&id=1mLpqQ233C7ZbMIjrdY13ZpFI8tcUTBH2");
            if (!p.getScoreboardTags().contains("texture_pack")) {
                p.getScoreboardTags().add("texture_pack");
                p.sendMessage("Texture pack enabled");
                p.sendMessage("Warning, this pack will probably not work, subcommand still in beta");
            } else {
                if (Bukkit.getServer().getPluginManager().isPluginEnabled("Geyser-Spigot")) {
                    if (FloodgateApi.getInstance().isFloodgatePlayer(p.getUniqueId())) {
                        p.sendMessage(ChatColor.RED + "Only java players can execute this command!");
                    }
                }
                if (p.getScoreboardTags().contains("texture_pack")) {
                    p.removeScoreboardTag("texture_pack");
                }
            }
        }
    }
}
