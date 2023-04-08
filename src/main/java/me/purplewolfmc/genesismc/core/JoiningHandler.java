package me.purplewolfmc.genesismc.core;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.geyser.api.GeyserApi;
import org.geysermc.geyser.api.connection.GeyserConnection;

import java.util.UUID;

public class JoiningHandler implements Listener {

    @EventHandler
    public void onJoinFirst(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        if(!p.getScoreboardTags().contains("chosen")){
            p.addScoreboardTag("choosing");
            e.setJoinMessage("");
        }

        if (Bukkit.getServer().getPluginManager().isPluginEnabled("Geyser-Spigot")) {
            FloodgateApi FloodgateAPI = FloodgateApi.getInstance();
            UUID uuid = p.getUniqueId();
            GeyserConnection connection = GeyserApi.api().connectionByUuid(p.getUniqueId());
            if (GeyserApi.api().isBedrockPlayer(p.getUniqueId()) || FloodgateAPI.isFloodgatePlayer(uuid)) {
                if (!p.getScoreboardTags().contains("geyser_player")) {
                    p.getScoreboardTags().add("geyser_player");
                }
            } else {
                if (p.getScoreboardTags().contains("texture_pack")) {
                    p.setResourcePack("https://drive.google.com/u/0/uc?id=13SyLJBJ5KWgSSbwmSpRYHKUR0r3I0rw7&export=download");
                }
            }
        }

    }


}
