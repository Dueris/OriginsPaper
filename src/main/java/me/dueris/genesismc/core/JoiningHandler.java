package me.dueris.genesismc.core;

import me.dueris.genesismc.core.factory.CraftApoli;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.geyser.api.GeyserApi;
import org.geysermc.geyser.api.connection.GeyserConnection;

import java.util.UUID;

import static org.bukkit.Bukkit.getServer;
import static org.bukkit.ChatColor.AQUA;

public class JoiningHandler implements Listener {

    @EventHandler
    public void onJoinFirst(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        p.setMaximumAir(300);

        //translation system
        String originTag = p.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "originTag"), PersistentDataType.STRING);
        if (originTag != null) {
            for (OriginContainer origin : CraftApoli.getOrigins()) {
                if (("origin-"+(origin.getTag().substring(8))).equals(originTag.substring(8)))
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origin"), PersistentDataType.BYTE_ARRAY, CraftApoli.toByteArray(origin));
            }
        }

        if (p.getClientBrandName() != null && p.getClientBrandName().equalsIgnoreCase("Immersions")) {
            p.setDisplayName(AQUA + p.getName());
            p.setPlayerListName(AQUA + p.getName());

        }

        if (p.getScoreboardTags().contains("texture_pack")) {
            p.setTexturePack("https://drive.google.com/uc?export=download&id=1mLpqQ233C7ZbMIjrdY13ZpFI8tcUTBH2");
        }


        //default playerdata values
        PersistentDataContainer data = p.getPersistentDataContainer();
        if (!data.has(new NamespacedKey(GenesisMC.getPlugin(), "shulker-box"), PersistentDataType.STRING)) {
            data.set(new NamespacedKey(GenesisMC.getPlugin(), "shulker-box"), PersistentDataType.STRING, "");
        }
        if (!p.getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "origin"), PersistentDataType.BYTE_ARRAY)) {
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origin"), PersistentDataType.BYTE_ARRAY, CraftApoli.toByteArray(CraftApoli.nullOrigin()));
        }
        if (!p.getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "can-explode"), PersistentDataType.INTEGER)) {
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "can-explode"), PersistentDataType.INTEGER, 1);
        }
        if (!p.getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER)) {
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER, 1);
        }
        if (!p.getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "toggle"), PersistentDataType.INTEGER)) {
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "toggle"), PersistentDataType.INTEGER, 1);
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
