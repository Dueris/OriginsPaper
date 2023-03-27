package me.purplewolfmc.genesismc.commands.subcommands;

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
        p.setResourcePack("https://drive.google.com/u/0/uc?id=13SyLJBJ5KWgSSbwmSpRYHKUR0r3I0rw7&export=download");
        if(!p.getScoreboardTags().contains("texture_pack")){
            p.getScoreboardTags().add("texture_pack");
            p.sendMessage("Texture pack enabled");
            p.sendMessage("Warning, this pack will probably not work, subcommand still in beta");
        }else{
            if(Bukkit.getServer().getPluginManager().isPluginEnabled("Geyser-Spigot")) {
                if (FloodgateApi.getInstance().isFloodgatePlayer(p.getUniqueId())) {
                    p.sendMessage(ChatColor.RED + "Only java players can execute this command!");
                }
            }
        }
    }
}
