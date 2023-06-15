package me.dueris.genesismc.core;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.CraftApoli;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.geyser.api.GeyserApi;
import org.geysermc.geyser.api.connection.GeyserConnection;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static org.bukkit.Bukkit.getServer;
import static org.bukkit.ChatColor.AQUA;

public class JoiningHandler implements Listener {

    @EventHandler
    public void onJoinFirst(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        p.setMaximumAir(300);

        //translation system
        String originTag = p.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "originiTag"), PersistentDataType.STRING);
        if (originTag != null) {
            for (OriginContainer origin : CraftApoli.getOrigins())
                if (origin.getTag().equals(originTag))
                    OriginPlayer.setOrigin(p, origin);
        }


        if (p.getClientBrandName() != null && p.getClientBrandName().equalsIgnoreCase("Immersions")) {
            p.setDisplayName(AQUA + p.getName());
            p.setPlayerListName(AQUA + p.getName());

        }

        if (!p.getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING)) {

            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-null");

        }

        if (p.getScoreboardTags().contains("texture_pack")) {
            p.setTexturePack("https://drive.google.com/uc?export=download&id=1mLpqQ233C7ZbMIjrdY13ZpFI8tcUTBH2");
        }

        if (!p.getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER)) {
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 0);
        }


        if (getServer().getPluginManager().isPluginEnabled("Geyser-Spigot")) {
            if (getServer().getPluginManager().isPluginEnabled("floodgate")) {
                FloodgateApi FloodgateAPI = FloodgateApi.getInstance();
                UUID uuid = p.getUniqueId();
                GeyserConnection connection = GeyserApi.api().connectionByUuid(p.getUniqueId());
                if (GeyserApi.api().isBedrockPlayer(p.getUniqueId()) || FloodgateAPI.isFloodgatePlayer(uuid)) {
                    p.getScoreboardTags().add("geyser_player");
                }
            }
            if (GeyserApi.api().isBedrockPlayer(p.getUniqueId())) {
                p.getScoreboardTags().add("geyser_player");
            } else {
                if (p.getScoreboardTags().contains("texture_pack")) {
                    p.setTexturePack("https://drive.google.com/uc?export=download&id=1mLpqQ233C7ZbMIjrdY13ZpFI8tcUTBH2");
                }
            }
        }

    }


}
