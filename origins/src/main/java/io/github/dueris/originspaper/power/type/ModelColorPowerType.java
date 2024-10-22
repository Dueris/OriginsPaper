package io.github.dueris.originspaper.power.type;

import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.DataResult;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import io.github.dueris.originspaper.util.Scheduler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.util.thread.NamedThreadFactory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mineskin.GenerateOptions;
import org.mineskin.JsoupRequestHandler;
import org.mineskin.MineSkinClient;
import org.mineskin.data.Skin;
import org.mineskin.data.Visibility;
import org.mineskin.exception.MineSkinRequestException;
import org.mineskin.response.MineSkinResponse;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.concurrent.*;

public class ModelColorPowerType extends PowerType {
	private static final IOOperator API = IOOperator.create("cache/skins/");
	private static final MineSkinClient CLIENT = !OriginsPaper.OriginConfiguration.getConfiguration().getString("api-key", "").isEmpty() ? MineSkinClient.builder()
		.requestHandler(JsoupRequestHandler::new)
		.userAgent("OriginsPaper")
		.apiKey(OriginsPaper.OriginConfiguration.getConfiguration().getString("api-key"))
		.build() : null;
	private static final ExecutorService ASYNC_SERVICE = Executors.newFixedThreadPool(1, new NamedThreadFactory("ModelColorBuilder"));
	private static boolean sentWarning = false;
	private final ConcurrentHashMap<Player, Tuple<String, String>> RUNTIME_CACHE = new ConcurrentHashMap<>();
	private final float red;
	private final float green;
	private final float blue;
	private final float alpha;

	public ModelColorPowerType(Power power, LivingEntity entity, float red, float green, float blue, float alpha) {
		super(power, entity);
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}

	public static void serializeSkinData(String fileName, SkinData skinData) throws IOException {
		File directory = new File("cache/skins/");
		if (!directory.exists()) {
			directory.mkdirs();
		}

		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("cache/skins/" + fileName + ".skin"))) {
			oos.writeObject(skinData);
		}
	}

	public static @Nullable SkinData deserializeSkinData(String fileName) throws IOException, ClassNotFoundException {
		if (!Paths.get("cache/skins/" + fileName + ".skin").toFile().exists()) {
			return null;
		}
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("cache/skins/" + fileName + ".skin"))) {
			return (SkinData) ois.readObject();
		}
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("model_color"),
			new SerializableData()
				.add("red", SerializableDataTypes.FLOAT, 1.0F)
				.add("green", SerializableDataTypes.FLOAT, 1.0F)
				.add("blue", SerializableDataTypes.FLOAT, 1.0F)
				.add("alpha", SerializableDataTypes.FLOAT, 1.0F)
				// OriginsPaper - region start - add validation for client to inform server if the api is unavailable
				.validate((instance -> {
					if (CLIENT == null && !sentWarning) {
						OriginsPaper.LOGGER.warn("ModelColor power is disabled due to the 'api-key' defined in the origins configuration not being present. Please set that value via mineskin to use this power.");
						sentWarning = true;
					}
					return DataResult.success(instance);
				})),
			// OriginsPaper - region end
			data -> (power, entity) -> new ModelColorPowerType(power, entity,
				data.getFloat("red"),
				data.getFloat("green"),
				data.getFloat("blue"),
				data.getFloat("alpha")
			)
		).allowCondition();
	}

	public float getRed() {
		return red;
	}

	public float getGreen() {
		return green;
	}

	public float getBlue() {
		return blue;
	}

	public float getAlpha() {
		return alpha;
	}

	@Deprecated(forRemoval = true) // Translucency is technically not possible with our implementation - Dueris
	public boolean isTranslucent() {
		return alpha < 1.0F;
	}

	@Override
	public void onRemoved() {
		if (!(entity instanceof ServerPlayer) || CLIENT == null) return;
		clearPower((ServerPlayer) entity);
	}

	@Override
	public void onAdded() {
		if (!(entity instanceof ServerPlayer) || CLIENT == null) return;
		applyPower((ServerPlayer) entity);
	}

	private void applyPower(@NotNull ServerPlayer player) {
		if (red == 0 && green == 0 && blue == 0) return;

		@Nullable Tuple<String, String> cached = load(player);
		if (cached != null) {
			String toSet = cached.getB();
			OriginsPaper.LOGGER.info("Found cached skin data for power `{}`! Using URL: {}", getPowerId().toString(), toSet);
			setSkin(player, toSet);
			return;
		}

		OriginsPaper.LOGGER.info("Starting ModelColor async processor...");

		ASYNC_SERVICE.submit(() -> {
			OriginsPaper.LOGGER.info("Requesting client info...");

			String profileUrl = askMojangForSkin(player);
			try {
				OriginsPaper.LOGGER.info("Using {} for original skin URL, building transformed", profileUrl);
				BufferedImage image = API.createSourceFile(profileUrl, player.getStringUUID() + "__original");
				try {
					File transformed = API.createTransformed(
						image, player.getStringUUID() + "__modified_" + getPowerId().toString().replace(":", "+"), red, green, blue
					);

					GenerateOptions options = GenerateOptions.create()
						.name(player.getStringUUID() + "_originspaper")
						.visibility(Visibility.PUBLIC);

					OriginsPaper.LOGGER.info("Generating upload..");
					CLIENT.generateUpload(transformed, options)
						.thenAccept(response -> {
							Skin skin = response.getSkin();
							OriginsPaper.LOGGER.info("Generated ModelColor URL: {}", skin.data().texture().url());

							RUNTIME_CACHE.put(player, new Tuple<>(profileUrl, skin.data().texture().url()));
							save(player);
							setSkin(player, skin.data().texture().url());
						})
						.exceptionally(throwable -> {
							if (throwable instanceof CompletionException completionException) {
								throwable = completionException.getCause();
							}

							if (throwable instanceof MineSkinRequestException requestException) {
								MineSkinResponse<?> response = requestException.getResponse();
								OriginsPaper.LOGGER.error(response.getMessageOrError());
							}

							throwable.printStackTrace();
							return null;
						}).get();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			} catch (InterruptedException | ExecutionException e) {
				throw new RuntimeException(e);
			}
		});
	}

	private String askMojangForSkin(@NotNull ServerPlayer player) {
		StringBuilder response = new StringBuilder();
		try {
			String url = "https://sessionserver.mojang.com/session/minecraft/profile/" + player.getStringUUID().replace("-", "");
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("GET");
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;

			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();

			JsonObject profile = JsonParser.parseString(response.toString()).getAsJsonObject();
			String encodedTexture = profile.getAsJsonArray("properties")
				.get(0).getAsJsonObject().get("value").getAsString();

			String decodedTexture = new String(Base64.getDecoder().decode(encodedTexture));

			JsonObject textures = JsonParser.parseString(decodedTexture).getAsJsonObject();
			return textures.getAsJsonObject("textures")
				.getAsJsonObject("SKIN").get("url").getAsString();

		} catch (Exception e) {
			throw new RuntimeException("Unable to get skin from Mojang! RESPONSE: " + response, e);
		}
	}

	public void setSkin(@NotNull ServerPlayer player, String url) {
		Scheduler.INSTANCE.queue((m) -> {
			CraftPlayer craftPlayer = player.getBukkitEntity();
			CraftPlayerProfile profile = (CraftPlayerProfile) craftPlayer.getPlayerProfile();
			PlayerTextures textures = profile.getTextures();

			try {
				textures.setSkin(new URL(url), profile.getTextures().getSkinModel());
				profile.setTextures(textures);
				craftPlayer.setPlayerProfile(profile);

				for (org.bukkit.entity.Player otherPlayer : Bukkit.getOnlinePlayers()) {
					if (otherPlayer.getUniqueId().equals(craftPlayer.getUniqueId())
						|| !otherPlayer.canSee(craftPlayer)) {
						continue;
					}

					otherPlayer.hidePlayer(OriginsPaper.getPlugin(), craftPlayer);
					otherPlayer.showPlayer(OriginsPaper.getPlugin(), craftPlayer);
				}

				SkinRefresher refresher = new SkinRefresher();
				refresher.refresh(player);
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}, 1);
	}

	private void clearPower(ServerPlayer player) {
		if (red == 0 && green == 0 && blue == 0) return;

		@Nullable Tuple<String, String> cached = load(player);
		if (cached != null) {
			String toSet = cached.getA();
			OriginsPaper.LOGGER.info("Found cached skin data for power `{}`! Using URL: {}", getPowerId().toString(), toSet);
			setSkin(player, toSet);
			return;
		}

		String profileSkin = askMojangForSkin(player);
		setSkin(player, profileSkin);
	}

	public void save(@NotNull ServerPlayer player) {
		Tuple<String, String> runtime = RUNTIME_CACHE.get(player);
		try {
			serializeSkinData(player.getStringUUID() + "--" + getPowerId().toString().replace(":", "+"),
				new SkinData(runtime.getA(), runtime.getB()));
		} catch (IOException e) {
			throw new RuntimeException("Unable to serialize skin file!", e);
		}
	}

	public Tuple<String, String> load(@NotNull ServerPlayer player) {
		try {
			SkinData data = deserializeSkinData(player.getStringUUID() + "--" + getPowerId().toString().replace(":", "+"));
			if (data == null) {
				return null;
			}
			return new Tuple<>(data.originalURL(), data.modifiedURL());
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static class IOOperator {
		private final File saveDir;

		private IOOperator(String saveOutput) {
			this.saveDir = Paths.get(saveOutput).toFile();
		}

		public static @NotNull ModelColorPowerType.IOOperator create(String saveOutput) {
			return new IOOperator(saveOutput);
		}

		public static BufferedImage downloadImage(String imageUrl, String savePath, String fileName) throws IOException {
			URL url = new URL(imageUrl);
			BufferedImage image = ImageIO.read(url);
			File outputDir = new File(savePath);
			outputDir.mkdirs();

			File outputFile = new File(savePath, fileName + ".png");
			if (outputFile.exists()) {
				Files.delete(outputFile.toPath());
			}
			ImageIO.write(image, "png", outputFile);

			return image;
		}

		public static void saveImage(BufferedImage image, String savePath, String fileName) throws IOException {
			File outputDir = new File(savePath);
			outputDir.mkdirs();

			File outputFile = new File(savePath, fileName);
			ImageIO.write(image, "png", outputFile);
		}

		public File getSaveDir() {
			return saveDir == null ? Paths.get("skins").toFile() : saveDir;
		}

		public BufferedImage createSourceFile(String url, String outputName) {
			try {
				Path saveDir = getSaveDir().toPath();
				if (!saveDir.toFile().exists()) {
					saveDir.toFile().mkdirs();
				} else {
					if (!saveDir.toFile().isDirectory()) {
						throw new RuntimeException("Provided saveDir isn't a valid directory");
					}
				}

				return downloadImage(url, getSaveDir().getAbsolutePath(), outputName);
			} catch (Exception throwable) {
				throwable.printStackTrace();
				return null;
			}
		}

		public File createTransformed(BufferedImage file, String saveName, double r, double g, double b) throws IOException {
			if (r > 1 || g > 1 || b > 1) throw new IllegalStateException("RGB values must be under 1");
			for (int x = 0; x < file.getWidth(); x++) {
				for (int y = 0; y < file.getHeight(); y++) {
					file.setRGB(x, y, transform(new Color(file.getRGB(x, y)), r, g, b).getRGB());
				}
			}

			saveImage(file, getSaveDir().getAbsolutePath(), saveName + ".png");
			return Paths.get(getSaveDir().getAbsolutePath() + File.separator + saveName + ".png").toFile();
		}

		public Color transform(@NotNull Color color, double r, double g, double b) {
			if (color.getAlpha() == 255 && color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0)
				return new Color(color.getRed(), color.getBlue(), color.getGreen(), 0); // Assume transparent, lets not do this lol
			int red = color.getRed();
			int green = color.getGreen();
			int blue = color.getBlue();
			Color pix = new Color(
				calculateValue(red * r),
				calculateValue(green * g),
				calculateValue(blue * b),
				calculateValue(color.getAlpha()));
			return pix;
		}

		protected int calculateValue(double fl) {
			return (int) (fl < 0 ? 0 : fl > 255 ? 255 : fl);
		}
	}

	public static final class SkinRefresher {
		private final Method refreshPlayerMethod;

		@Inject
		public SkinRefresher() {
			try {
				refreshPlayerMethod = CraftPlayer.class.getDeclaredMethod("refreshPlayer");
				refreshPlayerMethod.setAccessible(true);
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException(e);
			}
		}

		public void refresh(@NotNull Player player) {
			try {
				refreshPlayerMethod.invoke(player.getBukkitEntity());
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public record SkinData(String originalURL, String modifiedURL) implements Serializable {
		@Serial
		private static final long serialVersionUID = 5L;


		@Override
		public @NotNull String toString() {
			return "SkinData{originalURL='" + originalURL + "', modifiedURL='" + modifiedURL + "'}";
		}
	}

}

