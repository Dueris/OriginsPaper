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
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static org.bukkit.Bukkit.getServer;
import static org.bukkit.ChatColor.*;

public class JoiningHandler implements Listener {

    @EventHandler
    public void onJoinFirst(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if(!p.getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING)){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-null");
        }
        //translation system
        @Nullable Integer originid;
        originid = p.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
        if(originid == 0401065){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-enderian");
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 30000000);
        }
        if(originid == 6503044){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-shulk");
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 30000000);
        }
        if(originid == 0004013 || originid == 1 || originid == 0){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-human");
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 30000000);
        }
        if(originid == 1709012){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-arachnid");
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 30000000);
        }
        if(originid == 2356555){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-creep");
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 30000000);
        }
        if(originid == 7300041){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-phantom");
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 30000000);
        }
        if(originid == 2304045){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-slimeling");
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 30000000);
        }
        if(originid == 9602042){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-vexian");
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 30000000);
        }
        if(originid == 9811027){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-blazeborn");
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 30000000);
        }
        if(originid == 7303065){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-starborne");
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 30000000);
        }
        if(originid == 1310018){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-merling");
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 30000000);
        }
        if(originid == 1205048){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-allay");
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 30000000);
        }
        if(originid == 5308033){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-rabbit");
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 30000000);
        }
        if(originid == 8906022){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-bee");
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 30000000);
        }
        if(originid == 6211006){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-elytrian");
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 30000000);
        }
        if(originid == 4501011){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-avian");
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 30000000);
        }
        if(originid == 6211021){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-piglin");
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 30000000);
        }
        if(originid == 4307015){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-sculk");
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 30000000);
        }

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
