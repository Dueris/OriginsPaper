package me.dueris.genesismc.core.factory.powers.entity;

import com.destroystokyo.paper.profile.PlayerProfile;
import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.events.OriginChangeEvent;
import me.dueris.genesismc.core.utils.OriginContainer;
import net.skinsrestorer.api.PlayerWrapper;
import net.skinsrestorer.api.SkinsRestorerAPI;
import net.skinsrestorer.api.exception.SkinRequestException;
import net.skinsrestorer.api.property.IProperty;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.mineskin.MineskinClient;
import org.mineskin.data.Skin;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

import static me.dueris.genesismc.core.factory.powers.Powers.*;

public class PlayerRender extends BukkitRunnable {
    @Override
    public void run() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getMainScoreboard();
        Team team = scoreboard.getTeam("origin-players");
        if (team == null) {
            team = scoreboard.registerNewTeam("origin-players");
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            boolean isInvisible = p.hasPotionEffect(PotionEffectType.INVISIBILITY);
            boolean isInTranslucentList = translucent.contains(p);
            boolean isInPhantomForm = OriginPlayer.isInPhantomForm(p);

            if (isInPhantomForm) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    if (!other.equals(p)) {
                        other.hidePlayer(GenesisMC.getPlugin(), p);
                    }
                }
                if (!team.getEntries().contains(p)) {
                    team.addEntry(p.getName());
                }
            } else if (isInvisible && !isInTranslucentList) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    if (!other.equals(p)) {
                        other.hidePlayer(GenesisMC.getPlugin(), p);
                    }
                }
                Location location = p.getLocation();
                location.getWorld().spawnParticle(Particle.SPELL_MOB_AMBIENT, location, 2, 0.0, 0.0, 0.0, 1.0, null);
                if (!team.getEntries().contains(p)) {
                    team.addEntry(p.getName());
                }
            } else {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    if (!other.equals(p)) {
                        other.showPlayer(GenesisMC.getPlugin(), p);
                    }
                }
                if (!team.getEntries().contains(p)) {
                    team.addEntry(p.getName());
                }
            }

            if (isInTranslucentList) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    if (!other.equals(p)) {
                        other.showPlayer(GenesisMC.getPlugin(), p);
                    }
                }
                p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 15, 255, false, false, false));
                if (!team.getEntries().contains(p)) {
                    team.addEntry(p.getName());
                }
            }

            // Hide player from pumpkin_hate players if wearing a pumpkin
            ItemStack helmet = p.getInventory().getHelmet();
            boolean wearingPumpkin = helmet != null && helmet.getType() == Material.CARVED_PUMPKIN;

            for (Player target : Bukkit.getOnlinePlayers()) {
                if (pumpkin_hate.contains(target)) {
                    if (wearingPumpkin) {
                        target.hidePlayer(GenesisMC.getPlugin(), p);
                    } else {
                        target.showPlayer(GenesisMC.getPlugin(), p);
                    }
                }
            }
        }
    }
    public static class ModelColor implements Listener {
        private SkinsRestorerAPI skinsRestorerAPI = null;
        @SuppressWarnings("null")
        @EventHandler
        public void onPlayerChoose(OriginChangeEvent event) {
            Player player = event.getPlayer();
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (model_color.contains(player)) {
                        for (OriginContainer origin : OriginPlayer.getOrigin(player).values()) {
                            if(origin.getPowerFileFromType("origins:model_color") == null){
                                model_color.remove(player);
                            }
                            if(origin.getPowerFileFromType("origins:model_color") == null){
                                model_color.remove(player);
                            }
                            if(origin.getPowerFileFromType("origins:model_color") == null){
                                model_color.remove(player);
                            }
                            Double red = (Double) origin.getPowerFileFromType("origins:model_color").getModifier().get("red");
                            Double blue = (Double) origin.getPowerFileFromType("origins:model_color").getModifier().get("blue");
                            Double green = (Double) origin.getPowerFileFromType("origins:model_color").getModifier().get("green");
                            Long alphaTint = (long) origin.getPowerFileFromType("origins:model_color").getModifier().get("alpha");
                            String savePath = Bukkit.getServer().getPluginManager().getPlugin("genesismc").getDataFolder().getPath() + File.separator + "skins";
                            skinsRestorerAPI = SkinsRestorerAPI.getApi();
                            ModelColor.modifyPlayerSkin(player, red, green, blue, savePath, alphaTint, skinsRestorerAPI, false, origin);
                            if (model_color.contains(player)) {
                                if(player.getPlayerProfile().getTextures().getSkinModel() == PlayerTextures.SkinModel.CLASSIC){
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "skin set " + player.getName() + " " + player.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "modified-skin-url"), PersistentDataType.STRING) + " CLASSIC");
                                }else{
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "skin set " + player.getName() + " " + player.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "modified-skin-url"), PersistentDataType.STRING) + " SLIM");
                                }
                            }
                        }
                    } else {
                        for (OriginContainer origin : OriginPlayer.getOrigin(player).values()) {
                            Double red = Double.valueOf(0);
                            Double blue = Double.valueOf(0);
                            Double green = Double.valueOf(0);
                            Long alphaTint = 0L;
                            String savePath = Bukkit.getServer().getPluginManager().getPlugin("genesismc").getDataFolder().getPath() + File.separator + "skins";
                            skinsRestorerAPI = SkinsRestorerAPI.getApi();
                            ModelColor.modifyPlayerSkin(player, red, green, blue, savePath, alphaTint, skinsRestorerAPI, true, origin);
                            if (!model_color.contains(player)) {
                                if(player.getPlayerProfile().getTextures().getSkinModel() == PlayerTextures.SkinModel.CLASSIC){
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "skin set " + player.getName() + " " + player.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "original-skin-url"), PersistentDataType.STRING) + " CLASSIC");
                                }else{
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "skin set " + player.getName() + " " + player.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "original-skin-url"), PersistentDataType.STRING) + " SLIM");
                                }
                            }
                        }
                    }
                    this.cancel();
                }
            }.runTaskTimer(GenesisMC.getPlugin(), 4L, 1L);
        }

        @EventHandler
        public void JoinApplyTest(PlayerJoinEvent e){
            Player player = e.getPlayer();
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (model_color.contains(player)) {
                        if(player.getPlayerProfile().getTextures().getSkinModel() == PlayerTextures.SkinModel.CLASSIC){
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "skin set " + player.getName() + " " + player.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "modified-skin-url"), PersistentDataType.STRING) + " CLASSIC");
                        }else{
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "skin set " + player.getName() + " " + player.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "modified-skin-url"), PersistentDataType.STRING) + " SLIM");
                        }
                    } else {
                        if(player.getPlayerProfile().getTextures().getSkinModel() == PlayerTextures.SkinModel.CLASSIC){
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "skin set " + player.getName() + " " + player.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "original-skin-url"), PersistentDataType.STRING) + " CLASSIC");
                        }else{
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "skin set " + player.getName() + " " + player.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "original-skin-url"), PersistentDataType.STRING) + " SLIM");
                        }
                    }
                    this.cancel();
                }
            }.runTaskTimer(GenesisMC.getPlugin(), 4L, 1L);
        }

        public static void modifyPlayerSkin(Player player, Double redTint, Double greenTint, Double blueTint, String savePath, Long alphaTint, SkinsRestorerAPI skinsRestorerAPI, boolean applyOriginal, OriginContainer origin) {
            PlayerProfile gameProfile = player.getPlayerProfile();
            String textureProperty = skinsRestorerAPI.getSkinTextureUrl(SkinsRestorerAPI.getApi().getSkinData(player.getName()));
            String imageUrl = textureProperty;
            String uuid = player.getUniqueId().toString();
            String originalFileName = uuid + ".png";
            String modifiedFileName = uuid + "_modified.png";

            try {
                BufferedImage originalImage = downloadImage(imageUrl, savePath, originalFileName);
                BufferedImage modifiedImage = modifyImage(originalImage, redTint, greenTint, blueTint, alphaTint, player, origin);
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
                    PlayerProfile playerProfile = player.getPlayerProfile();


                    Skin skin = skinData;

                    String url = skin.data.texture.url;
                    player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "modified-skin-url"), PersistentDataType.STRING, url);
                    IProperty platformprop = skinsRestorerAPI.createPlatformProperty(player.getUniqueId() + "_modified", skin.data.texture.value, skin.data.texture.signature);
                    PlayerWrapper playerWrapper = new PlayerWrapper(player);
                    skinsRestorerAPI.applySkin(playerWrapper, platformprop);

                    try {
                        // /api custom
                            skinsRestorerAPI.setSkinData(player.getUniqueId() + "_modified", skinsRestorerAPI.createPlatformProperty(player.getUniqueId() + "_modified", skin.data.texture.value, skin.data.texture.signature), 0);
                        // #setSkin() for player skin
                        skinsRestorerAPI.setSkin(player.getName(), player.getUniqueId() + "_modified");

                        // Force skin refresh for player
                        skinsRestorerAPI.applySkin(new PlayerWrapper(player));

                    } catch (SkinRequestException e) {
                        e.printStackTrace();
                    }

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
                    if(applyOriginal){
                        try {
                            // /api custom
                            skinsRestorerAPI.setSkinData(player.getUniqueId().toString(), skinsRestorerAPI.createPlatformProperty(player.getUniqueId().toString(), skin.data.texture.value, skin.data.texture.signature), 0);
                            // Force skin refresh for player
                            skinsRestorerAPI.removeSkin(playername);
                            skinsRestorerAPI.applySkin(new PlayerWrapper(player));

                        } catch (SkinRequestException e) {
                            e.printStackTrace();
                        }
                    }

                });
            } catch (IOException e) {
                //rip
            }
        }
        @SuppressWarnings("javax.imageio.IIOException")
        private static BufferedImage downloadImage(String imageUrl, String savePath, String fileName) throws IOException {
            URL url = new URL(imageUrl);
            BufferedImage image = ImageIO.read(url);
            File outputDir = new File(savePath);
            outputDir.mkdirs();

            File outputFile = new File(savePath, fileName);
            ImageIO.write(image, "png", outputFile);

            return image;
        }

        private static BufferedImage modifyImage(BufferedImage originalImage, double redTint, double greenTint, double blueTint, double alphaTint, Player player, OriginContainer origin) {
            if (redTint > 1 || greenTint > 1 || blueTint > 1 || alphaTint > 1) {
                throw new IllegalArgumentException("Color values must be between 0 and 1.");
            }

            BufferedImage modifiedImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_ARGB);

            if (origin.getPowerFileFromType("origins:model_color").getModelRenderType() == null) return modifiedImage;

            if (origin.getPowerFileFromType("origins:model_color").getModelRenderType().equalsIgnoreCase("add")) {
                for (int x = 0; x < modifiedImage.getWidth(); x++) {
                    for (int y = 0; y < modifiedImage.getHeight(); y++) {
                        // Get the original RGB values of the pixel
                        int rgb = originalImage.getRGB(x, y);

                        // Extract the color components (alpha, red, green, blue)
                        int alpha = (rgb >> 24) & 0xFF;
                        int red = (rgb >> 16) & 0xFF;
                        int green = (rgb >> 8) & 0xFF;
                        int blue = rgb & 0xFF;

                        // Apply additive blending to the color components
                        red = blendColorComponentAdditive(red, (int) (redTint));
                        green = blendColorComponentAdditive(green, (int) (greenTint));
                        blue = blendColorComponentAdditive(blue, (int) (blueTint));

                        // Create the modified RGB value with the updated color components
                        int modifiedRGB = (alpha << 24) | (red << 16) | (green << 8) | blue;

                        // Set the modified pixel value in the modified image
                        modifiedImage.setRGB(x, y, modifiedRGB);
                    }
                }
            } else if (origin.getPowerFileFromType("origins:model_color").getModelRenderType().equalsIgnoreCase("subtract")) {
                for (int x = 0; x < modifiedImage.getWidth(); x++) {
                    for (int y = 0; y < modifiedImage.getHeight(); y++) {
                        int rgb = originalImage.getRGB(x, y);
                        int alpha = (rgb >> 24) & 0xFF;
                        int red = (rgb >> 16) & 0xFF;
                        int green = (rgb >> 8) & 0xFF;
                        int blue = rgb & 0xFF;

                        // Apply tint as an overlay
                        red = blendColorComponentSubtractive(red, (int) redTint);
                        green = blendColorComponentSubtractive(green, (int) greenTint);
                        blue = blendColorComponentSubtractive(blue, (int) blueTint);

                        int modifiedRGB = (alpha << 24) | (red << 16) | (green << 8) | blue;
                        modifiedImage.setRGB(x, y, modifiedRGB);
                    }
                }
            } else if (origin.getPowerFileFromType("origins:model_color").getModelRenderType().equalsIgnoreCase("multiply")) {
                for (int x = 0; x < modifiedImage.getWidth(); x++) {
                    for (int y = 0; y < modifiedImage.getHeight(); y++) {
                        int rgb = originalImage.getRGB(x, y);
                        int alpha = (rgb >> 24) & 0xFF;
                        int red = (rgb >> 16) & 0xFF;
                        int green = (rgb >> 8) & 0xFF;
                        int blue = rgb & 0xFF;

                        // Apply tint as an overlay
                        red = blendColorComponentMultiply(red, (int) redTint);
                        green = blendColorComponentMultiply(green, (int) greenTint);
                        blue = blendColorComponentMultiply(blue, (int) blueTint);

                        int modifiedRGB = (alpha << 24) | (red << 16) | (green << 8) | blue;
                        modifiedImage.setRGB(x, y, modifiedRGB);
                    }
                }
            } else if (origin.getPowerFileFromType("origins:model_color").getModelRenderType().equalsIgnoreCase("divide")) {
                for (int x = 0; x < modifiedImage.getWidth(); x++) {
                    for (int y = 0; y < modifiedImage.getHeight(); y++) {
                        int rgb = originalImage.getRGB(x, y);
                        int alpha = (rgb >> 24) & 0xFF;
                        int red = (rgb >> 16) & 0xFF;
                        int green = (rgb >> 8) & 0xFF;
                        int blue = rgb & 0xFF;

                        // Apply tint as an overlay
                        red = blendColorComponentDivide(red, (int) redTint);
                        green = blendColorComponentDivide(green, (int) greenTint);
                        blue = blendColorComponentDivide(blue, (int) blueTint);

                        int modifiedRGB = (alpha << 24) | (red << 16) | (green << 8) | blue;
                        modifiedImage.setRGB(x, y, modifiedRGB);
                    }
                }
            } else if(origin.getPowerFileFromType("origins:model_color").getModelRenderType().equalsIgnoreCase("original")){
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
            }
            return modifiedImage;
        }

        private static int blendColorComponent(int original, double tint) {
            double blended = original * tint;
            int clamped = (int) Math.min(Math.max(blended, 0), 255);
            return clamped;
        }

        private static int blendColorComponentAdditive(int baseColor, int tint) {
            int blendedColor = baseColor + tint;
            return Math.min(blendedColor, 255);
        }

        private static int blendColorComponentSubtractive(int baseColor, int tint) {
            int blendedColor = baseColor - tint;
            return Math.max(blendedColor, 0);
        }

        private static int blendColorComponentMultiply(int baseColor, int tint) {
            int blendedColor = (int) ((baseColor / 255.0) * tint);
            return Math.min(blendedColor, 255);
        }

        private static int blendColorComponentDivide(int baseColor, int tint) {
            if (tint == 0) {
                return 0;
            }

            int blendedColor = (int) ((baseColor / 255.0) / (tint / 255.0) * 255);
            return Math.min(blendedColor, 255);
        }

        private static void saveImage(BufferedImage image, String savePath, String fileName) throws IOException {
            File outputDir = new File(savePath);
            outputDir.mkdirs();

            File outputFile = new File(savePath, fileName);
            ImageIO.write(image, "png", outputFile);
        }

    }
}

