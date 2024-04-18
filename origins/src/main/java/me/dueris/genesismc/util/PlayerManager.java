package me.dueris.genesismc.util;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.OriginChangeEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.data.types.Modifier;
import me.dueris.genesismc.factory.powers.ApoliPower;
import me.dueris.genesismc.factory.powers.apoli.AttributeHandler;
import me.dueris.genesismc.factory.powers.apoli.GravityPower;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Origin;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.screen.GuiTicker;
import me.dueris.genesismc.storage.GenesisConfigs;
import me.dueris.genesismc.storage.OriginDataContainer;
import me.dueris.genesismc.storage.nbt.NBTFixerUpper;
import me.dueris.genesismc.util.entity.InventorySerializer;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.NamespacedKey;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

public class PlayerManager implements Listener {

    public static ArrayList<Player> playersLeaving = new ArrayList<>();

    public static void ReapplyEntityReachPowers(Player player) {
        for (Origin origin : OriginPlayerAccessor.getOrigin(player).values()) {
            for (Power power : origin.getMultiPowerFileFromType("apoli:attribute")) {
                if (power == null) continue;
                for (Modifier modifier : power.getModifiers()) {
                    if (modifier.handle.getString("attribute").equalsIgnoreCase("reach-entity-attributes:reach")) {
                        ApoliPower.extra_reach.add(player);
                        return;
                    } else if (modifier.handle.getString("attribute").equalsIgnoreCase("reach-entity-attributes:attack_range")) {
                        ApoliPower.extra_reach_attack.add(player);
                        return;
                    } else {
                        AttributeHandler.ReachUtils.setFinalReach(player, AttributeHandler.ReachUtils.getDefaultReach(player));
                    }
                }
            }
        }
    }

    public static void originValidCheck(Player p) {
        HashMap<Layer, Origin> origins = OriginPlayerAccessor.getOrigin(p);
        for (Layer layer : origins.keySet()) {
            if (layer == null) continue; // Layer was removed
            for (String tag : layer.getOriginIdentifiers()) {
                NamespacedKey fixedKey = NamespacedKey.fromString(tag);
                if (GenesisMC.getPlugin().registry.retrieve(Registries.ORIGIN).get(fixedKey) == null) {
                    // Layer not in registry, cry.
                    origins.replace(layer, CraftApoli.nullOrigin());
                    p.sendMessage(Component.text("Your origin, \"%originName%\" was not found on the registry in the layer, \"%layerName%\".".replace("%originName%", fixedKey.asString()).replace("%layerName%", layer.getName())).color(TextColor.fromHexString(ColorConstants.RED)));
                }
            }
        }

        //check if the player has all the existing layers
        layerLoop:
        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            for (Layer playerLayer : origins.keySet()) {
                if (playerLayer == null) continue layerLoop; // Layer was removed
                if (layer.getTag().equals(playerLayer.getTag())) continue layerLoop;
            }
            origins.put(layer, CraftApoli.nullOrigin());
        }
        p.getPersistentDataContainer().set(GenesisMC.identifier("originLayer"), PersistentDataType.STRING, CraftApoli.toSaveFormat(origins, p));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        //set origins to null if none present
        if (
            !p.getPersistentDataContainer().has(GenesisMC.identifier("originLayer"), PersistentDataType.STRING) ||
                p.getPersistentDataContainer().get(GenesisMC.identifier("originLayer"), PersistentDataType.STRING) == null ||
                p.getPersistentDataContainer().get(GenesisMC.identifier("originLayer"), PersistentDataType.STRING) == ""
        ) {
            HashMap<Layer, Origin> origins = new HashMap<>();
            for (Layer layer : CraftApoli.getLayersFromRegistry()) origins.put(layer, CraftApoli.nullOrigin());
            p.getPersistentDataContainer().set(GenesisMC.identifier("originLayer"), PersistentDataType.STRING, CraftApoli.toOriginSetSaveFormat(origins));
        }

        // ---  translation system ---

        if (!p.getPersistentDataContainer().has(GenesisMC.identifier("insideBlock"), PersistentDataType.BOOLEAN)) {
            p.getPersistentDataContainer().set(GenesisMC.identifier("insideBlock"), PersistentDataType.BOOLEAN, false);
        }

        //default playerdata values
        PersistentDataContainer data = p.getPersistentDataContainer();
        if (data.has(GenesisMC.identifier("shulker-box"), PersistentDataType.STRING)) {
            String save = data.get(GenesisMC.identifier("shulker-box"), PersistentDataType.STRING);
            InventorySerializer.saveInNbtIO("origins:inventory", save, p);
            data.remove(GenesisMC.identifier("shulker-box"));
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

        // Add delay config
        if (GenesisConfigs.getMainConfig().contains("choosing_delay")) {
            int delay = (int) GenesisConfigs.getMainConfig().get("choosing_delay");
            GuiTicker.delayedPlayers.add(p);
            new BukkitRunnable() {
                @Override
                public void run() {
                    GuiTicker.delayedPlayers.remove(p);
                }
            }.runTaskLater(GenesisMC.getPlugin(), delay);
        }
    }

    @EventHandler
    public void playerQuitHandler(PlayerQuitEvent e) {
        playersLeaving.add(e.getPlayer());
        e.getPlayer().getPersistentDataContainer().set(GenesisMC.identifier("originLayer"), PersistentDataType.STRING, CraftApoli.toSaveFormat(OriginPlayerAccessor.getOrigin(e.getPlayer()), e.getPlayer()));
        OriginPlayerAccessor.unassignPowers(e.getPlayer());
        OriginDataContainer.unloadData(e.getPlayer());
        playersLeaving.remove(e.getPlayer());
    }

    @EventHandler
    public void newOrigin(OriginChangeEvent e) {
        OriginDataContainer.unloadData(e.getPlayer());
        OriginDataContainer.loadData(e.getPlayer());
    }
}
