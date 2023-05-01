package me.dueris.genesismc.core;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.geyser.api.GeyserApi;
import org.geysermc.geyser.api.connection.GeyserConnection;

import java.util.UUID;

import static org.bukkit.Bukkit.getServer;
import static org.bukkit.ChatColor.*;

public class JoiningHandler implements Listener {

    @EventHandler
    public void onJoinFirst(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if(p.getClientBrandName() != null && p.getClientBrandName().equalsIgnoreCase("Immersions")){
            p.setDisplayName(AQUA + p.getName());
            p.setPlayerListName(AQUA + p.getName());

        }

        if (p.getScoreboardTags().contains("texture_pack")) {
            p.setTexturePack("https://drive.google.com/uc?export=download&id=1mLpqQ233C7ZbMIjrdY13ZpFI8tcUTBH2");
        }

        if(!p.getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER)){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 0);
        }

        if (getServer().getPluginManager().isPluginEnabled("Geyser-Spigot")) {
            if(getServer().getPluginManager().isPluginEnabled("floodgate")){
                FloodgateApi FloodgateAPI = FloodgateApi.getInstance();
                UUID uuid = p.getUniqueId();
                GeyserConnection connection = GeyserApi.api().connectionByUuid(p.getUniqueId());
                if (GeyserApi.api().isBedrockPlayer(p.getUniqueId()) || FloodgateAPI.isFloodgatePlayer(uuid)) {
                    if (!p.getScoreboardTags().contains("geyser_player")) {
                        p.getScoreboardTags().add("geyser_player");
                    }
                }
            }
            if (GeyserApi.api().isBedrockPlayer(p.getUniqueId())) {
                if (!p.getScoreboardTags().contains("geyser_player")) {
                    p.getScoreboardTags().add("geyser_player");
                }
            } else {
                if (p.getScoreboardTags().contains("texture_pack")) {
                    p.setTexturePack("https://drive.google.com/uc?export=download&id=1mLpqQ233C7ZbMIjrdY13ZpFI8tcUTBH2");
                }
            }
        }

    }


}
