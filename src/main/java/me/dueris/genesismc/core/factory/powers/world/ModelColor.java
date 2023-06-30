package me.dueris.genesismc.core.factory.powers.world;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.events.OriginChangeEvent;
import me.dueris.genesismc.core.utils.OriginContainer;
import net.skinsrestorer.api.SkinsRestorerAPI;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineskin.MineskinClient;
import org.mineskin.data.Skin;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

import static me.dueris.genesismc.core.factory.powers.Powers.model_color;

public class ModelColor implements Listener {
    private SkinsRestorerAPI skinsRestorerAPI = null;

    @EventHandler
    public void onPlayerChoose(OriginChangeEvent event) {
        Player player = event.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (model_color.contains(player)) {
                    for (OriginContainer origin : OriginPlayer.getOrigin(player).values()) {
                        Long red = (Long) origin.getPowerFileFromType("origins:model_color").getModifier().get("red");
                        Long blue = (Long) origin.getPowerFileFromType("origins:model_color").getModifier().get("blue");
                        Long green = (Long) origin.getPowerFileFromType("origins:model_color").getModifier().get("green");
                        String savePath = Bukkit.getServer().getPluginManager().getPlugin("genesismc").getDataFolder().getPath() + File.separator + "skins";
                        skinsRestorerAPI = SkinsRestorerAPI.getApi();
                        ModelColor.modifyPlayerSkin(player, Math.toIntExact(red), Math.toIntExact(green), Math.toIntExact(blue), savePath, false, skinsRestorerAPI);

                        String skinData = player.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "modified-skin-url"), PersistentDataType.STRING).toString();
                        if(player.getPlayerProfile().getTextures().getSkinModel() == PlayerTextures.SkinModel.CLASSIC){
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "skin set " + player.getName() + " " + player.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "modified-skin-url"), PersistentDataType.STRING).toString() + " CLASSIC");
                        }else{
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "skin set " + player.getName() + " " + player.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "modified-skin-url"), PersistentDataType.STRING).toString() + " SLIM");
                        }

                    }
                } else {
                    for (OriginContainer origin : OriginPlayer.getOrigin(player).values()) {
                        int red = 0;
                        int blue = 0;
                        int green = 0;
                        String savePath = Bukkit.getServer().getPluginManager().getPlugin("genesismc").getDataFolder().getPath() + File.separator + "skins";
                        skinsRestorerAPI = SkinsRestorerAPI.getApi();
                        ModelColor.modifyPlayerSkin(player, red, green, blue, savePath, true, skinsRestorerAPI);

                        String skinData = player.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "original-skin-url"), PersistentDataType.STRING).toString();
                        if(player.getPlayerProfile().getTextures().getSkinModel() == PlayerTextures.SkinModel.CLASSIC){
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "skin set " + player.getName() + " " + player.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "original-skin-url"), PersistentDataType.STRING).toString() + " CLASSIC");
                        }else{
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "skin set " + player.getName() + " " + player.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "original-skin-url"), PersistentDataType.STRING).toString() + " SLIM");
                        }

                    }
                }
                this.cancel();
            }
        }.runTaskTimer(GenesisMC.getPlugin(), 4L, 1L);
    }

    public static void modifyPlayerSkin(Player player, int redTint, int greenTint, int blueTint, String savePath, boolean retrieve, SkinsRestorerAPI skinsRestorerAPI) {
        PlayerProfile gameProfile = ((CraftPlayer) player).getPlayerProfile();
        String textureProperty = gameProfile.getTextures().getSkin().getFile();
        String imageUrl = textureProperty;
        String uuid = player.getUniqueId().toString();
        String originalFileName = uuid + ".png";
        String modifiedFileName = uuid + "_modified.png";

        try {
            BufferedImage originalImage = downloadImage(imageUrl, savePath, originalFileName);
            BufferedImage modifiedImage = modifyImage(originalImage, redTint, greenTint, blueTint);
            saveImage(modifiedImage, savePath, modifiedFileName);
            MineskinClient mineskinClient = new MineskinClient();

            File modifiedFile = new File(savePath, modifiedFileName);

            CompletableFuture<Skin> future = mineskinClient.generateUpload(modifiedFile);
            future.thenAccept(skinData -> {
                if (skinData == null) {
                    // Failed to generate the skin data
                    future.cancel(true);
                    return;
                }

                Skin skin = skinData;

                String url = skin.data.texture.url;
                player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "modified-skin-url"), PersistentDataType.STRING, url);

            });

            CompletableFuture<Skin> futureorg = mineskinClient.generateUpload(originalImage);
            futureorg.thenAccept(skinData -> {
                if (skinData == null) {
                    // Failed to generate the skin data
                    return;
                }

                Skin skin = skinData;

                String url = skin.data.texture.url;
                player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "original-skin-url"), PersistentDataType.STRING, url);
                String playername = player.getName();

            });
        } catch (IOException e) {
            System.out.println("Failed to process the player's skin.");
            e.printStackTrace();
        }
    }

    private static BufferedImage downloadImage(String imageUrl, String savePath, String fileName) throws IOException {
        URL url = new URL("https://textures.minecraft.net" + imageUrl);
        BufferedImage image = ImageIO.read(url);
        File outputDir = new File(savePath);
        outputDir.mkdirs();

        File outputFile = new File(savePath, fileName);
        ImageIO.write(image, "png", outputFile);

        return image;
    }

    private static BufferedImage modifyImage(BufferedImage originalImage, int redTint, int greenTint, int blueTint) {
        BufferedImage modifiedImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < modifiedImage.getWidth(); x++) {
            for (int y = 0; y < modifiedImage.getHeight(); y++) {
                int rgb = originalImage.getRGB(x, y);
                int alpha = (rgb >> 24) & 0xFF;
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                // Apply tint as an overlay
                red = blendColorComponent(red, redTint);
                green = blendColorComponent(green, greenTint);
                blue = blendColorComponent(blue, blueTint);

                int modifiedRGB = (alpha << 24) | (red << 16) | (green << 8) | blue;
                modifiedImage.setRGB(x, y, modifiedRGB);
            }
        }

        return modifiedImage;
    }

    private static int blendColorComponent(int original, int tint) {
        int blended = original + tint;
        return Math.min(Math.max(blended, 0), 255);
    }



    /*
    private static BufferedImage modifyImage(BufferedImage originalImage, int redTint, int greenTint, int blueTint) {
    BufferedImage modifiedImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics graphics = modifiedImage.getGraphics();
    graphics.drawImage(originalImage, 0, 0, null);

    for (int x = 0; x < modifiedImage.getWidth(); x++) {
        for (int y = 0; y < modifiedImage.getHeight(); y++) {
            int rgb = modifiedImage.getRGB(x, y);
            Color color = new Color(rgb, true);

            if (color.getAlpha() != 0) {
                int modifiedRGB = new Color(
                        Math.max(color.getRed() - redTint, 0),
                        Math.max(color.getGreen() - greenTint, 0),
                        Math.max(color.getBlue() - blueTint, 0),
                        color.getAlpha()
                ).getRGB();
                modifiedImage.setRGB(x, y, modifiedRGB);
            }
        }
    }

    return modifiedImage;
}
     */

    private static void saveImage(BufferedImage image, String savePath, String fileName) throws IOException {
        File outputDir = new File(savePath);
        outputDir.mkdirs();

        File outputFile = new File(savePath, fileName);
        ImageIO.write(image, "png", outputFile);
    }

}
