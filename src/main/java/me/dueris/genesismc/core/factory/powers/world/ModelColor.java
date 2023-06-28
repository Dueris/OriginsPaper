package me.dueris.genesismc.core.factory.powers.world;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.events.OriginChangeEvent;
import me.dueris.genesismc.core.utils.OriginContainer;
import net.skinsrestorer.api.SkinsRestorerAPI;
import net.skinsrestorer.api.PlayerWrapper;
import net.skinsrestorer.api.SkinVariant;
import net.skinsrestorer.api.exception.SkinRequestException;
import net.skinsrestorer.api.property.IProperty;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

import static me.dueris.genesismc.core.factory.powers.Powers.model_color;

public class ModelColor implements Listener {
    private SkinsRestorerAPI skinsRestorerAPI = null;

    @EventHandler
    public void onPlayerChoose(OriginChangeEvent event) {
        Player player = event.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                if(model_color.contains(player)){
                    for(OriginContainer origin : OriginPlayer.getOrigin(player).values()){
                        int red = (int) origin.getPowerFileFromType("origins:model_color").getModifier().get("red");
                        int blue = (int) origin.getPowerFileFromType("origins:model_color").getModifier().get("blue");
                        int green = (int) origin.getPowerFileFromType("origins:model_color").getModifier().get("green");
                        String savePath = Bukkit.getServer().getPluginManager().getPlugin("genesismc").getDataFolder().getPath() + "skins";
                        skinsRestorerAPI = SkinsRestorerAPI.getApi();
                        ModelColor.modifyPlayerSkin(player, red, green, blue, savePath, false, skinsRestorerAPI);
                    }
                }else {
                    for (OriginContainer origin : OriginPlayer.getOrigin(player).values()) {
                        int red = 0;
                        int blue = 0;
                        int green = 0;
                        String savePath = Bukkit.getServer().getPluginManager().getPlugin("genesismc").getDataFolder().getPath() + "skins";
                        skinsRestorerAPI = SkinsRestorerAPI.getApi();
                        ModelColor.modifyPlayerSkin(player, red, green, blue, savePath, true, skinsRestorerAPI);
                    }
                }
                this.cancel();
        }
    }.runTaskTimer(GenesisMC.getPlugin(), 5L, 1L);

    }

    public static void modifyPlayerSkin(Player player, int redTint, int greenTint, int blueTint, String savePath, boolean original, SkinsRestorerAPI skinsRestorerAPI) {
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
        Graphics graphics = modifiedImage.getGraphics();
        graphics.drawImage(originalImage, 0, 0, null);

        for (int x = 0; x < modifiedImage.getWidth(); x++) {
            for (int y = 0; y < modifiedImage.getHeight(); y++) {
                int rgb = modifiedImage.getRGB(x, y);
                Color color = new Color(rgb, true);

                if (color.getAlpha() != 0) {
                    int modifiedRGB = new Color(
                            Math.min(color.getRed() + redTint, 255),
                            Math.min(color.getGreen() + greenTint, 255),
                            Math.min(color.getBlue() + blueTint, 255),
                            color.getAlpha()
                    ).getRGB();
                    modifiedImage.setRGB(x, y, modifiedRGB);
                }
            }
        }

        return modifiedImage;
    }

    private static void saveImage(BufferedImage image, String savePath, String fileName) throws IOException {
        File outputDir = new File(savePath);
        outputDir.mkdirs();

        File outputFile = new File(savePath, fileName);
        ImageIO.write(image, "png", outputFile);
    }

}
