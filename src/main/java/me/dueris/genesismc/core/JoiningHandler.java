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
        }
        if(originid == 6503044){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-shulk");
        }
        if(originid == 0004013 || originid == 1 || originid == 0){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-human");;
        }
        if(originid == 1709012){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-arachnid");
        }
        if(originid == 2356555){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-creep");
        }
        if(originid == 7300041){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-phantom");
        }
        if(originid == 2304045){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-slimeling");
        }
        if(originid == 9602042){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-vexian");
        }
        if(originid == 9811027){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-blazeborn");
        }
        if(originid == 7303065){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-starborne");
        }
        if(originid == 1310018){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-merling");
        }
        if(originid == 1205048){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-allay");
        }
        if(originid == 5308033){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-rabbit");
        }
        if(originid == 8906022){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-bee");
        }
        if(originid == 6211006){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-elytrian");
        }
        if(originid == 4501011){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-avian");
        }
        if(originid == 6211021){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-piglin");
        }
        if(originid == 4307015){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-sculk");
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
