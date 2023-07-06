package me.dueris.genesismc.core;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.CraftApoli;
import me.dueris.genesismc.core.utils.LayerContainer;
import me.dueris.genesismc.core.utils.OriginContainer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.geyser.api.GeyserApi;

import java.io.ByteArrayInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.dueris.genesismc.core.utils.BukkitColour.AQUA;
import static me.dueris.genesismc.core.utils.BukkitColour.RED;
import static org.bukkit.Bukkit.getServer;

public class PlayerHandler implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        p.setMaximumAir(300);

        //set origins to null if none present
        if (p.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "origins"), PersistentDataType.BYTE_ARRAY) == null) {
            HashMap<LayerContainer, OriginContainer> origins = new HashMap<>();
            for (LayerContainer layer : CraftApoli.getLayers()) origins.put(layer, CraftApoli.nullOrigin());
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origins"), PersistentDataType.BYTE_ARRAY, CraftApoli.toByteArray(origins));
        }

        // ---  translation system ---
        String originTag = p.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "originTag"), PersistentDataType.STRING);
        if (originTag != null) {
            for (OriginContainer origin : CraftApoli.getOrigins()) {
                if (("origin-" + (origin.getTag().substring(8))).equals(originTag.substring(8)))
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origins"), PersistentDataType.BYTE_ARRAY, CraftApoli.toByteArray(new HashMap<>(Map.of(CraftApoli.getLayerFromTag("origins:origin"), origin))));
            }
        } else if (!p.getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "origins"), PersistentDataType.BYTE_ARRAY)) {
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origins"), PersistentDataType.BYTE_ARRAY, CraftApoli.toByteArray(new HashMap<>(Map.of(CraftApoli.getLayerFromTag("origins:origin"), CraftApoli.nullOrigin()))));
        }

        if (p.getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "origin"), PersistentDataType.BYTE_ARRAY)) {
            ByteArrayInputStream bis = new ByteArrayInputStream(p.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "origin"), PersistentDataType.BYTE_ARRAY));
            try {
                ObjectInput oi = new ObjectInputStream(bis);
                OriginContainer origin = (OriginContainer) oi.readObject();
                p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origins"), PersistentDataType.BYTE_ARRAY, CraftApoli.toByteArray(new HashMap<>(Map.of(CraftApoli.getLayerFromTag("origins:origin"), origin))));
            } catch (Exception er) {
                Bukkit.getLogger().warning("[GenesisMC] Error converting old origin container");
            }
        }
        // --- end ---

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
        if (!p.getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "can-explode"), PersistentDataType.INTEGER)) {
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "can-explode"), PersistentDataType.INTEGER, 1);
        }
        if (!p.getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER)) {
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER, 1);
        }
        if (!p.getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "toggle"), PersistentDataType.INTEGER)) {
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "toggle"), PersistentDataType.INTEGER, 1);
        }

        if (!p.getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "modified-skin-url"), PersistentDataType.STRING) || p.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "modified-skin-url"), PersistentDataType.STRING) == null) {
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "modified-skin-url"), PersistentDataType.STRING, p.getPlayerProfile().getTextures().getSkin().getFile());
        }
        if (!p.getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "original-skin-url"), PersistentDataType.STRING) || p.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "original-skin-url"), PersistentDataType.STRING) == null) {
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "original-skin-url"), PersistentDataType.STRING, p.getPlayerProfile().getTextures().getSkin().getFile());
        }


        if (getServer().getPluginManager().isPluginEnabled("Geyser-Spigot")) {
            if (getServer().getPluginManager().isPluginEnabled("floodgate")) {
                FloodgateApi FloodgateAPI = FloodgateApi.getInstance();
                UUID uuid = p.getUniqueId();
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

        layerChecks(p);
        //customOriginExistCheck(p);
        OriginPlayer.assignPowers(p);

        p.sendMessage(Component.text("GenesisMC is in beta and still has lots of bugs, use /origin bug and notify us about any issues!").color(TextColor.fromHexString(AQUA)));

    }

    @EventHandler
    public void playerQuitHandler(PlayerQuitEvent e) {
        OriginPlayer.unassignPowers(e.getPlayer());
    }


    public static void customOriginExistCheck(Player p) {
        HashMap<LayerContainer, OriginContainer> origins = OriginPlayer.getOrigin(p);
        for (OriginContainer origin : origins.values()) {
            if (origin.getTag().equals(CraftApoli.nullOrigin().getTag())) continue;
            if (CraftApoli.getOriginTags().contains(origin.getTag())) continue;
            NamespacedKey key = new NamespacedKey(GenesisMC.getPlugin(), "origins");
            HashMap<LayerContainer, OriginContainer> playerOrigins = CraftApoli.toOriginContainer(p.getPersistentDataContainer().get(key, PersistentDataType.BYTE_ARRAY));
            playerOrigins.replace(OriginPlayer.getLayer(p, origin), CraftApoli.nullOrigin());
            p.getPersistentDataContainer().set(key, PersistentDataType.BYTE_ARRAY, CraftApoli.toByteArray(playerOrigins));
            p.sendMessage(Component.text("Your origin has been removed! Please select a new one.").color(TextColor.fromHexString(RED)));
            p.sendMessage(Component.text("If you believe this is a mistake please contact your server admin(s).").color(TextColor.fromHexString(RED)));
        }
    }

    public static void layerChecks(Player p) {
        HashMap<LayerContainer, OriginContainer> origins = OriginPlayer.getOrigin(p);
        for (LayerContainer layer : origins.keySet()) {
            if (!CraftApoli.layerExists(layer)) {
                OriginPlayer.removeOrigin(p, layer);
                p.sendMessage(Component.text("The layer \""+layer.getName()+"\" has been removed!\nIf you believe this is a mistake please contact your server admin(s).").color(TextColor.fromHexString(RED)));
                continue;
            }
            if (!CraftApoli.getLayerFromTag(layer.getTag()).getOrigins().contains(origins.get(layer).getTag())) {
                origins.replace(layer, CraftApoli.nullOrigin());
                p.sendMessage(Component.text("Your selected origin has been removed from the \""+layer.getName()+"\" layer!\nIf you believe this is a mistake please contact your server admin(s).").color(TextColor.fromHexString(RED)));
            }
        }
        p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origins"), PersistentDataType.BYTE_ARRAY, CraftApoli.toByteArray(origins));
    }
}
