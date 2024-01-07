package me.dueris.genesismc;

import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.events.OriginChangeEvent;
import me.dueris.genesismc.events.OriginChooseEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.player.Gravity;
import me.dueris.genesismc.factory.powers.player.attributes.AttributeHandler;
import me.dueris.genesismc.files.nbt.FixerUpper;
import me.dueris.genesismc.utils.LayerContainer;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import me.dueris.genesismc.utils.legacy.LegacyOriginContainer;
import me.dueris.genesismc.utils.translation.LangConfig;
import me.dueris.genesismc.utils.translation.Translation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.geysermc.geyser.api.GeyserApi;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.dueris.genesismc.factory.powers.Power.extra_reach;
import static me.dueris.genesismc.factory.powers.Power.extra_reach_attack;
import static me.dueris.genesismc.factory.powers.simple.BounceSlimeBlock.bouncePlayers;
import static me.dueris.genesismc.factory.powers.simple.MimicWarden.mimicWardenPlayers;
import static me.dueris.genesismc.factory.powers.simple.PiglinNoAttack.piglinPlayers;
import static me.dueris.genesismc.factory.powers.simple.ScareCreepers.scaryPlayers;
import static me.dueris.genesismc.utils.BukkitColour.AQUA;
import static me.dueris.genesismc.utils.BukkitColour.RED;
import static org.bukkit.Bukkit.getServer;

public class PlayerHandler implements Listener {

    public static void ReapplyEntityReachPowers(Player player) {
        for (OriginContainer origin : OriginPlayerUtils.getOrigin(player).values()) {
            for (PowerContainer power : origin.getMultiPowerFileFromType("origins:attribute")) {
                if (power == null) continue;
                for (HashMap<String, Object> modifier : power.getPossibleModifiers("modifier", "modifier")) {
                    if (modifier.get("attribute").toString().equalsIgnoreCase("reach-entity-attributes:reach")) {
                        extra_reach.add(player);
                        return;
                    } else if (modifier.get("attribute").toString().equalsIgnoreCase("reach-entity-attributes:attack_range")) {
                        extra_reach_attack.add(player);
                        return;
                    } else {
                        AttributeHandler.Reach.setFinalReach(player, AttributeHandler.Reach.getDefaultReach(player));
                    }
                }
            }
        }
    }

    public static void originValidCheck(Player p) {
        HashMap<LayerContainer, OriginContainer> origins = OriginPlayerUtils.getOrigin(p);
        ArrayList<LayerContainer> deletedLayers = new ArrayList<>();
        for (LayerContainer layer : origins.keySet()) {
            //check if the player layer exists
            if (!CraftApoli.layerExists(layer)) {
                deletedLayers.add(layer);
                p.sendMessage(Component.text(LangConfig.getLocalizedString(p, "misc.layerRemoved").replace("%layerName%", layer.getName())).color(TextColor.fromHexString(RED)));
                continue;
            }
            //origin check
            layer.getTag();
            CraftApoli.getLayerFromTag(layer.getTag()).getOrigins();
            origins.get(layer).getTag();
            CraftApoli.getLayerFromTag(layer.getTag()).getOrigins().contains(origins.get(layer).getTag());

            if (!CraftApoli.getLayerFromTag(layer.getTag()).getOrigins().contains(origins.get(layer).getTag())) {
                origins.replace(layer, CraftApoli.nullOrigin());
                p.sendMessage(Component.text(LangConfig.getLocalizedString(p, "misc.originRemoved").replace("%originName%", origins.get(layer).getName()).replace("%layerName%", layer.getName())).color(TextColor.fromHexString(RED)));
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
        for (LayerContainer layer : deletedLayers) OriginPlayerUtils.removeOrigin(p, layer);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Bukkit.getLogger().info("PlayerLocale saved as[" + Translation.getPlayerLocale(p) + "] for player[%player%]".replace("%player%", p.getName()));
        //set origins to null if none present
        if (
            !p.getPersistentDataContainer().has(GenesisMC.identifier("originLayer"), PersistentDataType.STRING) ||
            p.getPersistentDataContainer().get(GenesisMC.identifier("originLayer"), PersistentDataType.STRING) == null ||
            p.getPersistentDataContainer().get(GenesisMC.identifier("originLayer"), PersistentDataType.STRING) == ""
        ){
            HashMap<LayerContainer, OriginContainer> origins = new HashMap<>();
            for (LayerContainer layer : CraftApoli.getLayers()) origins.put(layer, CraftApoli.nullOrigin());
            p.getPersistentDataContainer().set(GenesisMC.identifier("originLayer"), PersistentDataType.STRING, CraftApoli.toOriginSetSaveFormat(origins));
        }

        if(p.getPersistentDataContainer().has(GenesisMC.identifier("originLayers"))){
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

        if(p.getPersistentDataContainer().has(GenesisMC.identifier("origins"))){
            p.getPersistentDataContainer().remove(GenesisMC.identifier("originTag"));
        }

        if (p.getPersistentDataContainer().has(GenesisMC.identifier("origins"), PersistentDataType.BYTE_ARRAY)){
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
                    OriginPlayerUtils.setOrigin(p, layer, CraftApoli.nullOrigin());
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

        try{
            if (!p.getPersistentDataContainer().has(GenesisMC.identifier("modified-skin-url"), PersistentDataType.STRING) || p.getPersistentDataContainer().get(GenesisMC.identifier("modified-skin-url"), PersistentDataType.STRING) == null) {
                p.getPersistentDataContainer().set(GenesisMC.identifier("modified-skin-url"), PersistentDataType.STRING, p.getPlayerProfile().getTextures().getSkin().getFile());
            }
            if (!p.getPersistentDataContainer().has(GenesisMC.identifier("original-skin-url"), PersistentDataType.STRING) || p.getPersistentDataContainer().get(GenesisMC.identifier("original-skin-url"), PersistentDataType.STRING) == null) {
                p.getPersistentDataContainer().set(GenesisMC.identifier("original-skin-url"), PersistentDataType.STRING, p.getPlayerProfile().getTextures().getSkin().getFile());
            }
        } catch (Exception vv){
            //silence code - offline mode fucks things
        }

        p.saveData();
        try {
            FixerUpper.fixupFile(Path.of(GenesisMC.playerDataFolder.toPath().toString() + File.separator + ((CraftPlayer)p).getHandle().getStringUUID() + ".dat").toFile());
        } catch (IOException ev){
            ev.printStackTrace();
        }

        OriginDataContainer.loadData(p);
        OriginPlayerUtils.setupPowers(p);
        originValidCheck(p);
        OriginPlayerUtils.assignPowers(p);
        try {
            for (Class<? extends CraftPower> c : CraftPower.findCraftPowerClasses()) {
                if (CraftPower.getRegistered().contains(c)) continue;
                if (CraftPower.class.isAssignableFrom(c)) {
                    Constructor<? extends CraftPower> constructor = c.getConstructor(Player.class);
                    CraftPower instance = constructor.newInstance(p);
                    CraftPower.getRegistered().add(instance.getClass());
                    Bukkit.getLogger().info("new CraftPower registered with POWER_TYPE " + instance.getPowerFile() + " with POWER_ARRAY of " + instance.getPowerArray().toString());

                    if (instance instanceof Listener) {
                        Bukkit.getServer().getPluginManager().registerEvents((Listener) instance, GenesisMC.getPlugin());
                    } else {
                        Listener.class.isAssignableFrom(instance.getClass());
                    }
                }
            }
        } catch (IOException | ReflectiveOperationException el) {
            throw new RuntimeException(el);
        }

        // origins:simple powers
        Gravity g = new Gravity();
        g.run(p);
        new BukkitRunnable() {
            @Override
            public void run() {
                ReapplyEntityReachPowers(p);
            }
        }.runTaskLater(GenesisMC.getPlugin(), 5L);
        boolean hasMimicWardenPower = false;

        for (OriginContainer origin : OriginPlayerUtils.getOrigin(p).values()) {
            for (PowerContainer power : origin.getPowerContainers()) {
                if (power == null) continue;
                if (power.getTag().equals("origins:mimic_warden")) {
                    hasMimicWardenPower = true;
                    break;
                }
            }
        }
        if (hasMimicWardenPower && !mimicWardenPlayers.contains(p)) {
            mimicWardenPlayers.add(p);
        } else if (!hasMimicWardenPower) {
            mimicWardenPlayers.remove(p);
        }

        boolean hasPower = false;

        for (OriginContainer origin : OriginPlayerUtils.getOrigin(p).values()) {
            for (String power : origin.getPowers()) {
                if (power.equals("origins:slime_block_bounce")) {
                    hasPower = true;
                    break;
                }
            }
        }

        if (hasPower && !bouncePlayers.contains(p)) {
            bouncePlayers.add(p);
        } else if (!hasPower) {
            bouncePlayers.remove(p);
        }

        boolean hasPiglinPower = false;

        for (OriginContainer origin : OriginPlayerUtils.getOrigin(p).values()) {
            for (PowerContainer power : origin.getPowerContainers()) {
                if (power.getTag().equals("origins:piglin_brothers")) {
                    hasPiglinPower = true;
                    break;
                }
            }
        }
        if (hasPiglinPower && !piglinPlayers.contains(p)) {
            piglinPlayers.add(p);
        } else if (!hasPiglinPower) {
            piglinPlayers.remove(p);
        }

        boolean hasScaryPower = false;

        for (OriginContainer origin : OriginPlayerUtils.getOrigin(p).values()) {
            for (PowerContainer power : origin.getPowerContainers()) {
                if (power.getTag().equals("origins:scare_creepers")) {
                    hasPiglinPower = true;
                    break;
                }
            }
        }
        if (hasScaryPower && !scaryPlayers.contains(p)) {
            scaryPlayers.add(p);
        } else if (!hasScaryPower) {
            scaryPlayers.remove(p);
        }
    }

    @EventHandler
    public void playerQuitHandler(PlayerQuitEvent e) {
        e.getPlayer().getPersistentDataContainer().set(GenesisMC.identifier("originLayer"), PersistentDataType.STRING, CraftApoli.toSaveFormat(OriginPlayerUtils.getOrigin(e.getPlayer()), e.getPlayer()));
        OriginPlayerUtils.unassignPowers(e.getPlayer());
        OriginDataContainer.unloadData(e.getPlayer());
    }

    @EventHandler
    public void newOrigin(OriginChangeEvent e){
        OriginDataContainer.unloadData(e.getPlayer());
        OriginDataContainer.loadData(e.getPlayer());
    }
}
