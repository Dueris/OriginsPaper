package me.dueris.genesismc.core;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.CraftApoli;
import me.dueris.genesismc.core.factory.powers.OriginsMod.player.attributes.AttributeHandler;
import me.dueris.genesismc.core.utils.Lang;
import me.dueris.genesismc.core.utils.LayerContainer;
import me.dueris.genesismc.core.utils.OriginContainer;
import me.dueris.genesismc.core.utils.PowerContainer;
import me.dueris.genesismc.core.utils.legacy.LegacyOriginContainer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.world.level.levelgen.feature.configurations.BlockColumnConfiguration;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.geyser.api.GeyserApi;

import java.io.ByteArrayInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.dueris.genesismc.core.factory.powers.Powers.extra_reach;
import static me.dueris.genesismc.core.factory.powers.Powers.extra_reach_attack;
import static me.dueris.genesismc.core.utils.BukkitColour.AQUA;
import static me.dueris.genesismc.core.utils.BukkitColour.RED;
import static org.bukkit.Bukkit.getServer;

public class PlayerHandler implements Listener {

    public static void ReapplyEntityReachPowers(Player player){
        for(OriginContainer origin : OriginPlayer.getOrigin(player).values()){
            PowerContainer power = origin.getPowerFileFromType("origins:attribute");
            if (power == null) continue;

            if(power.getModifier().get("attribute").toString().equalsIgnoreCase("reach-entity-attributes:reach")){
                extra_reach.add(player);
                return;
            } else if (power.getModifier().get("attribute").toString().equalsIgnoreCase("reach-entity-attributes:attack_range")) {
                extra_reach_attack.add(player);
                return;
            } else {
                AttributeHandler.Reach.setFinalReach(player, AttributeHandler.Reach.getDefaultReach(player));}
        }
    }

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
                LegacyOriginContainer legacyOrigin = (LegacyOriginContainer) oi.readObject(); //this errors because it tries to read it as the current origin container before casting it to the old one
                p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origins"), PersistentDataType.BYTE_ARRAY, CraftApoli.toByteArray(new HashMap<>(Map.of(CraftApoli.getLayerFromTag("origins:origin"), CraftApoli.getOrigin(legacyOrigin.getTag())))));
                p.getPersistentDataContainer().remove(new NamespacedKey(GenesisMC.getPlugin(), "origin"));
            } catch (Exception er) {
                er.printStackTrace();
                Bukkit.getLogger().warning(Lang.getLocalizedString("errors.oldContainerConversion"));
            }
        }
        Bukkit.getLogger().warning("[GenesisMC] Reminder to devs - fix old origin container translation");

        if(p.getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "insideBlock"), PersistentDataType.BOOLEAN)){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "insideBlock"), PersistentDataType.BOOLEAN, false);
        }

        if(p.getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "reach"), PersistentDataType.INTEGER)){
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "reach"), PersistentDataType.INTEGER, AttributeHandler.Reach.getDefaultReach(p));
        }

        //default playerdata values
        PersistentDataContainer data = p.getPersistentDataContainer();
        if (!data.has(new NamespacedKey(GenesisMC.getPlugin(), "shulker-box"), PersistentDataType.STRING)) {
            data.set(new NamespacedKey(GenesisMC.getPlugin(), "shulker-box"), PersistentDataType.STRING, "");
        }
        if (!p.getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "can-explode"), PersistentDataType.INTEGER)) {
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "can-explode"), PersistentDataType.INTEGER, 1);
        }
        if (!p.getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.BOOLEAN)) {
            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.BOOLEAN, false);
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

        originValidCheck(p);
        OriginPlayer.assignPowers(p);
        p.sendMessage(Component.text(Lang.getLocalizedString("misc.joinText")).color(TextColor.fromHexString(AQUA)));

        new BukkitRunnable() {
            @Override
            public void run() {
                ReapplyEntityReachPowers(p);
            }
        }.runTaskTimer(GenesisMC.getPlugin(), 3, 0);
    }

    @EventHandler
    public void playerQuitHandler(PlayerQuitEvent e) {
        OriginPlayer.unassignPowers(e.getPlayer());
    }

    public static void originValidCheck(Player p) {
        HashMap<LayerContainer, OriginContainer> origins = OriginPlayer.getOrigin(p);
        ArrayList<LayerContainer> deletedLayers = new ArrayList<>();
        for (LayerContainer layer : origins.keySet()) {
            //check if the player layer exists
            if (!CraftApoli.layerExists(layer)) {
                deletedLayers.add(layer);
                p.sendMessage(Component.text(Lang.getLocalizedString("misc.layerRemoved").replace("%layerName%", layer.getName())).color(TextColor.fromHexString(RED)));
                continue;
            }
            //origin check
            if (!CraftApoli.getLayerFromTag(layer.getTag()).getOrigins().contains(origins.get(layer).getTag())) {
                origins.replace(layer, CraftApoli.nullOrigin());
                p.sendMessage(Component.text(Lang.getLocalizedString("misc.originRemoved").replace("%originName", origins.get(layer).getName()).replace("%layerName%", layer.getName())).color(TextColor.fromHexString(RED)));
            }
        }

        //check if the player has all the existing layers
        layerLoop:
        for (LayerContainer layer : CraftApoli.getLayers()) {
            for (LayerContainer playerLayer : origins.keySet()) {
                if (layer.getTag().equals(playerLayer.getTag())) continue layerLoop;
            }
            origins.put(layer, CraftApoli.nullOrigin());
        }
        p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origins"), PersistentDataType.BYTE_ARRAY, CraftApoli.toByteArray(origins));

        //removes deleted layer from the players data
        for (LayerContainer layer : deletedLayers) OriginPlayer.removeOrigin(p, layer);
    }
}
