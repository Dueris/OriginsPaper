package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.PowerUpdateEvent;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.DontRegister;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.RaycastUtils;
import me.dueris.modelcolor.ModelColorAPI;
import me.dueris.modelcolor.colortransformers.OriginsTransformer;
import net.skinsrestorer.api.PropertyUtils;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.exception.DataRequestException;
import net.skinsrestorer.api.property.SkinProperty;
import net.skinsrestorer.api.storage.PlayerStorage;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftLocation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerTextures;
import org.mineskin.MineskinClient;
import org.mineskin.data.Skin;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ModelColor { // Left empty due to it needing to be registered on certain circumstances

    public static class ModelTransformer extends CraftPower implements Listener, DontRegister {

        public void applyModelTransformer(ModelColorAPI api, SkinsRestorer skinsRestorer, Player p, Power power) {
            PlayerStorage storage = skinsRestorer.getPlayerStorage();
            try {
                Optional<SkinProperty> property = storage.getSkinForPlayer(p.getUniqueId(), p.getName());
                if (property.isPresent()) {
                    String currentSkinURL = PropertyUtils.getSkinTextureUrl(property.get());
                    String uuid = p.getUniqueId().toString();
                    String modifiedFile = uuid + "_modified";
                    double r = power.getNumberOrDefault("red", 0.0f).getDouble();
                    double g = power.getNumberOrDefault("green", 0.0f).getDouble();
                    double b = power.getNumberOrDefault("blue", 0.0f).getDouble();

                    CompletableFuture.runAsync(() -> {
                        try {
                            // Generate skin files
                            BufferedImage currentSkin = api.createSourceFile(currentSkinURL, uuid);
                            File modifiedSkin = api.createTransformed(currentSkin, new OriginsTransformer(), modifiedFile, r, g, b);

                            MineskinClient client = new MineskinClient();
                            CompletableFuture<Skin> future = client.generateUpload(modifiedSkin);
                            future.thenAccept(skin -> {
                                if (skin == null) {
                                    future.cancel(true);
                                    throw new RuntimeException("MineSkinClient returned null skin data! Perhaps check your internet connection?");
                                }
                                String url = skin.data.texture.url;
                                url = formatUrl(url);
                                p.getPersistentDataContainer().set(GenesisMC.identifier("modified-skin-url"), PersistentDataType.STRING, url);
                            });

                            p.getPersistentDataContainer().set(GenesisMC.identifier("original-skin-url"), PersistentDataType.STRING, currentSkinURL);
                            p.saveData();
                        } catch (IOException e) {
                            throw new RuntimeException("An exception occurred when attempting to apply model changes to a player", e);
                        }
                    }).get();
                }
            } catch (DataRequestException | InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        public String formatUrl(String url) {
            if (!url.startsWith("http://textures.minecraft.net")) {
                return "http://textures.minecraft.net" + url;
            }
            return url;
        }

        public String getSlim(Player p) {
            return p.getPlayerProfile().getTextures().getSkinModel().equals(PlayerTextures.SkinModel.CLASSIC) ? "CLASSIC" : "SLIM";
        }

        @EventHandler
        public void update(PowerUpdateEvent e) {
            if (Bukkit.getPluginManager().isPluginEnabled("SkinsRestorer")) {
                if (e.getPower().getType().equalsIgnoreCase(getType())) { // Power Update for apoli:model_color
                    if (e.isRemoved()) { // Power was removed, clear skin
                        RaycastUtils.executeNMSCommand(((CraftEntity) e.getPlayer()).getHandle(), CraftLocation.toVec3D(e.getPlayer().getLocation()), "skin clear @s");
                    } else {
                        ModelColorAPI api = ModelColorAPI.create(Bukkit.getPluginsFolder().getAbsolutePath() + File.separator + "GenesisMC" + File.separator + "skins");
                        SkinsRestorer skinsRestorer = SkinsRestorerProvider.get();
                        applyModelTransformer(api, skinsRestorer, e.getPlayer(), e.getPower());
                        String url = e.getPlayer().getPersistentDataContainer().get(GenesisMC.identifier("modified-skin-url"), PersistentDataType.STRING);
                        String SPACE = " ";
                        RaycastUtils.executeNMSCommand(((CraftEntity) e.getPlayer()).getHandle(), CraftLocation.toVec3D(e.getPlayer().getLocation()), "skin set " + url + SPACE + "@s" + SPACE + getSlim(e.getPlayer()));
                    }
                }
            }
        }

        @Override
        public String getType() {
            return "apoli:model_color";
        }

        @Override
        public ArrayList<Player> getPlayersWithPower() {
            return model_color;
        }

        @Override
        public void setActive(Player p, String tag, Boolean bool) {
            if (powers_active.containsKey(p)) {
                if (powers_active.get(p).containsKey(tag)) {
                    powers_active.get(p).replace(tag, bool);
                } else {
                    powers_active.get(p).put(tag, bool);
                }
            } else {
                powers_active.put(p, new HashMap<>());
                setActive(p, tag, bool);
            }
        }
    }
}
