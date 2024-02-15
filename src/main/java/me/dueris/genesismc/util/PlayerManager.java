package me.dueris.genesismc.util;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.OriginChangeEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.powers.Power;
import me.dueris.genesismc.factory.powers.apoli.AttributeHandler;
import me.dueris.genesismc.factory.powers.apoli.GravityPower;
import me.dueris.genesismc.registry.LayerContainer;
import me.dueris.genesismc.registry.OriginContainer;
import me.dueris.genesismc.registry.PowerContainer;
import me.dueris.genesismc.storage.OriginDataContainer;
import me.dueris.genesismc.storage.nbt.NBTFixerUpper;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import me.dueris.genesismc.util.legacy.LegacyOriginContainer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlayerManager implements Listener {

    public static void ReapplyEntityReachPowers(Player player) {
        for (OriginContainer origin : OriginPlayerAccessor.getOrigin(player).values()) {
            for (PowerContainer power : origin.getMultiPowerFileFromType("apoli:attribute")) {
                if (power == null) continue;
                for (HashMap<String, Object> modifier : power.getPossibleModifiers("modifier", "modifier")) {
                    if (modifier.get("attribute").toString().equalsIgnoreCase("reach-entity-attributes:reach")) {
                        Power.extra_reach.add(player);
                        return;
                    } else if (modifier.get("attribute").toString().equalsIgnoreCase("reach-entity-attributes:attack_range")) {
                        Power.extra_reach_attack.add(player);
                        return;
                    } else {
                        AttributeHandler.Reach.setFinalReach(player, AttributeHandler.Reach.getDefaultReach(player));
                    }
                }
            }
        }
    }

    public static void originValidCheck(Player p) {
        HashMap<LayerContainer, OriginContainer> origins = OriginPlayerAccessor.getOrigin(p);
        ArrayList<LayerContainer> deletedLayers = new ArrayList<>();
        for (LayerContainer layer : origins.keySet()) {
            //check if the player layer exists
            if (!CraftApoli.layerExists(layer)) {
                deletedLayers.add(layer);
                p.sendMessage(Component.text(LangConfig.getLocalizedString(p, "misc.layerRemoved").replace("%layerName%", layer.getName())).color(TextColor.fromHexString(ColorConstants.RED)));
                continue;
            }
            //origin check
            layer.getTag();
            CraftApoli.getLayerFromTag(layer.getTag()).getOrigins();
            origins.get(layer).getTag();
            CraftApoli.getLayerFromTag(layer.getTag()).getOrigins().contains(origins.get(layer).getTag());

            if (!CraftApoli.getLayerFromTag(layer.getTag()).getOrigins().contains(origins.get(layer).getTag())) {
                origins.replace(layer, CraftApoli.nullOrigin());
                p.sendMessage(Component.text(LangConfig.getLocalizedString(p, "misc.originRemoved").replace("%originName%", origins.get(layer).getName()).replace("%layerName%", layer.getName())).color(TextColor.fromHexString(ColorConstants.RED)));
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
        p.getPersistentDataContainer().set(GenesisMC.identifier("originLayer"), PersistentDataType.STRING, CraftApoli.toSaveFormat(origins, p));

        //removes deleted layer from the players data
        for (LayerContainer layer : deletedLayers) OriginPlayerAccessor.removeOrigin(p, layer);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        // 0.2.6 update
        if(p.getPersistentDataContainer().has(GenesisMC.identifier("originLayer"), PersistentDataType.STRING) && !p.getPersistentDataContainer().has(GenesisMC.identifier("updatedTo026"), PersistentDataType.BOOLEAN)){
            for(LayerContainer layerContainer : CraftApoli.getLayers()){
                if(!OriginPlayerAccessor.getOrigin(p, layerContainer).equals(CraftApoli.nullOrigin())){ // Valid origin
                    OriginPlayerAccessor.setOrigin(p, layerContainer, OriginPlayerAccessor.getOrigin(p, layerContainer)); // Update origin
                }
            }
            p.getPersistentDataContainer().set(GenesisMC.identifier("updatedTo026"), PersistentDataType.BOOLEAN, true);
        }
        //set origins to null if none present
        if (
                !p.getPersistentDataContainer().has(GenesisMC.identifier("originLayer"), PersistentDataType.STRING) ||
                        p.getPersistentDataContainer().get(GenesisMC.identifier("originLayer"), PersistentDataType.STRING) == null ||
                        p.getPersistentDataContainer().get(GenesisMC.identifier("originLayer"), PersistentDataType.STRING) == ""
        ) {
            HashMap<LayerContainer, OriginContainer> origins = new HashMap<>();
            for (LayerContainer layer : CraftApoli.getLayers()) origins.put(layer, CraftApoli.nullOrigin());
            p.getPersistentDataContainer().set(GenesisMC.identifier("originLayer"), PersistentDataType.STRING, CraftApoli.toOriginSetSaveFormat(origins));
        }

        if (p.getPersistentDataContainer().has(GenesisMC.identifier("originLayers"))) {
            p.getPersistentDataContainer().remove(GenesisMC.identifier("originLayers"));
        }

        // ---  translation system ---
        String originTag = p.getPersistentDataContainer().get(GenesisMC.identifier("originTag"), PersistentDataType.STRING);

        if (!(originTag == null || originTag.equals("null"))) {
            for (OriginContainer origin : CraftApoli.getOrigins()) {
                if (("origin-" + (origin.getTag().substring(8))).equals(originTag.substring(8)))
                    p.getPersistentDataContainer().set(GenesisMC.identifier("originLayer"), PersistentDataType.STRING, CraftApoli.toOriginSetSaveFormat(new HashMap<>(Map.of(CraftApoli.getLayerFromTag("origins:origin"), origin))));
            }
        }

        if (p.getPersistentDataContainer().has(GenesisMC.identifier("origins"))) {
            p.getPersistentDataContainer().remove(GenesisMC.identifier("originTag"));
        }

        if (p.getPersistentDataContainer().has(GenesisMC.identifier("origins"), PersistentDataType.BYTE_ARRAY)) {
            p.getPersistentDataContainer().remove(GenesisMC.identifier("origins"));
        }

        if (p.getPersistentDataContainer().has(GenesisMC.identifier("origin"), PersistentDataType.BYTE_ARRAY)) {
            ByteArrayInputStream bis = new ByteArrayInputStream(p.getPersistentDataContainer().get(GenesisMC.identifier("origin"), PersistentDataType.BYTE_ARRAY));
            try {
                ObjectInput oi = new ObjectInputStream(bis);
                LegacyOriginContainer legacyOrigin = (LegacyOriginContainer) oi.readObject();
                p.getPersistentDataContainer().set(GenesisMC.identifier("originLayer"), PersistentDataType.STRING, CraftApoli.toOriginSetSaveFormat(new HashMap<>(Map.of(CraftApoli.getLayerFromTag("origins:origin"), CraftApoli.getOrigin(legacyOrigin.getTag())))));
                p.getPersistentDataContainer().remove(GenesisMC.identifier("origins"));
                p.getPersistentDataContainer().remove(GenesisMC.identifier("origin"));
            } catch (Exception er) {
                for (LayerContainer layer : CraftApoli.getLayers()) {
                    OriginPlayerAccessor.setOrigin(p, layer, CraftApoli.nullOrigin());
                }
            }
        }
//        Bukkit.getLogger().warning("[GenesisMC] Reminder to devs - fix old origin container translation"); // yeah we fixed this already?

        if (!p.getPersistentDataContainer().has(GenesisMC.identifier("insideBlock"), PersistentDataType.BOOLEAN)) {
            p.getPersistentDataContainer().set(GenesisMC.identifier("insideBlock"), PersistentDataType.BOOLEAN, false);
        }

        //default playerdata values
        PersistentDataContainer data = p.getPersistentDataContainer();
        if (!data.has(GenesisMC.identifier("shulker-box"), PersistentDataType.STRING)) {
            data.set(GenesisMC.identifier("shulker-box"), PersistentDataType.STRING, "");
        }
        if (!p.getPersistentDataContainer().has(GenesisMC.identifier("can-explode"), PersistentDataType.INTEGER)) {
            p.getPersistentDataContainer().set(GenesisMC.identifier("can-explode"), PersistentDataType.INTEGER, 1);
        }
        if (!p.getPersistentDataContainer().has(GenesisMC.identifier("in-phantomform"), PersistentDataType.BOOLEAN)) {
            p.getPersistentDataContainer().set(GenesisMC.identifier("in-phantomform"), PersistentDataType.BOOLEAN, false);
        }
        if (!p.getPersistentDataContainer().has(GenesisMC.identifier("toggle"), PersistentDataType.INTEGER)) {
            p.getPersistentDataContainer().set(GenesisMC.identifier("toggle"), PersistentDataType.INTEGER, 1);
        }

        try {
            if (!p.getPersistentDataContainer().has(GenesisMC.identifier("modified-skin-url"), PersistentDataType.STRING) || p.getPersistentDataContainer().get(GenesisMC.identifier("modified-skin-url"), PersistentDataType.STRING) == null) {
                p.getPersistentDataContainer().set(GenesisMC.identifier("modified-skin-url"), PersistentDataType.STRING, p.getPlayerProfile().getTextures().getSkin().getFile());
            }
            if (!p.getPersistentDataContainer().has(GenesisMC.identifier("original-skin-url"), PersistentDataType.STRING) || p.getPersistentDataContainer().get(GenesisMC.identifier("original-skin-url"), PersistentDataType.STRING) == null) {
                p.getPersistentDataContainer().set(GenesisMC.identifier("original-skin-url"), PersistentDataType.STRING, p.getPlayerProfile().getTextures().getSkin().getFile());
            }
        } catch (Exception vv) {
            //silence code - offline mode fucks things
        }

        p.saveData();
        try {
            NBTFixerUpper.fixupFile(Path.of(GenesisMC.playerDataFolder.toPath() + File.separator + ((CraftPlayer) p).getHandle().getStringUUID() + ".dat").toFile());
        } catch (IOException ev) {
            ev.printStackTrace();
        }

        OriginDataContainer.loadData(p);
        OriginPlayerAccessor.setupPowers(p);
        originValidCheck(p);
        OriginPlayerAccessor.assignPowers(p);

        GravityPower g = new GravityPower();
        g.run(p);
        new BukkitRunnable() {
            @Override
            public void run() {
                ReapplyEntityReachPowers(p);
            }
        }.runTaskLater(GenesisMC.getPlugin(), 5L);
    }

    @EventHandler
    public void playerQuitHandler(PlayerQuitEvent e) {
        e.getPlayer().getPersistentDataContainer().set(GenesisMC.identifier("originLayer"), PersistentDataType.STRING, CraftApoli.toSaveFormat(OriginPlayerAccessor.getOrigin(e.getPlayer()), e.getPlayer()));
        OriginPlayerAccessor.unassignPowers(e.getPlayer());
        OriginDataContainer.unloadData(e.getPlayer());
    }

    @EventHandler
    public void newOrigin(OriginChangeEvent e) {
        OriginDataContainer.unloadData(e.getPlayer());
        OriginDataContainer.loadData(e.getPlayer());
    }
}
