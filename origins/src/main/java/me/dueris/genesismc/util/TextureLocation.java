package me.dueris.genesismc.util;

import me.dueris.calio.registry.Registrable;
import me.dueris.calio.registry.Registrar;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.registry.Registries;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class TextureLocation implements Registrable {
	public static HashMap<String, BarColor> textureMap = new HashMap<>();
	private final NamespacedKey key;

	public TextureLocation(NamespacedKey key) {
		this.key = key;
	}

	public static void registerAll() throws IOException {
		if (CraftApoli.datapacksInDir() == null) return;

		Registrar<TextureLocation> textureRegistry = GenesisMC.getPlugin().registry.retrieve(Registries.TEXTURE_LOCATION);

		for (File pack : CraftApoli.datapacksInDir()) {
			if (pack == null || !pack.isDirectory() || pack.listFiles() == null) continue;

			for (File folders : pack.listFiles()) {
				if (folders.getName().equalsIgnoreCase("assets")) {
					for (File root : folders.listFiles()) {
						if (!root.isDirectory()) continue;

						String rootName = root.getName();
						File texturesFolder = new File(root, "textures");

						if (!texturesFolder.exists() || !texturesFolder.isDirectory()) continue;

						try {
							Files.walk(texturesFolder.toPath())
									.sorted(Comparator.reverseOrder())
									.forEach(path -> {
										if (path.toString().endsWith(".png")) {
											String texturePath = path.toAbsolutePath().toString().replace(texturesFolder.getAbsolutePath(), "")
													.replace("\\", "/");
											textureRegistry.register(new TextureLocation(NamespacedKey.fromString(rootName + ":textures" + texturePath)));
										}
									});
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		for (TextureLocation location : textureRegistry.values()) {
			if (CraftApoli.datapacksInDir() == null) return;

			for (File pack : CraftApoli.datapacksInDir()) {
				if (pack == null || !pack.isDirectory() || pack.listFiles() == null) continue;

				for (File folders : pack.listFiles()) {
					if (folders.getName().equalsIgnoreCase("assets")) {
						File mainRoot = new File(folders, location.key.asString().split(":")[0]);

						if (!mainRoot.exists() || !mainRoot.isDirectory()) continue;

						Path path = mainRoot.toPath().resolve(location.key.asString().split(":")[1]);

						if (!Files.exists(path)) continue;

						try {
							BufferedImage image = ImageIO.read(path.toFile());

							int height = image.getHeight();

							java.util.List<Color> modifiedPixels = new ArrayList<>();

							for (int y = 2; y < height; y += 11) {
								modifiedPixels.add(new Color(image.getRGB(2, y)));
							}

							int index = 0;
							for (Color color : modifiedPixels) {
								if (color != null) {
									textureMap.put(location.getKey().asString() + "/-/" + index, convertToBarColor(color));
									index++;
								}
							}

						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

	}

	public static int indexOfColor(Color[] array, Color targetColor) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == null) continue;
			if (array[i].equals(targetColor)) {
				return i;
			}
		}
		return -1;
	}

	public static BarColor convertToBarColor(Color color) {
		int rgb = color.getRGB();
		int red = (rgb >> 16) & 0xFF;
		int green = (rgb >> 8) & 0xFF;
		int blue = rgb & 0xFF;

		if (red > green && red > blue) {
			if (red - green < 30) return BarColor.YELLOW;
			return BarColor.RED;
		} else if (green > red && green > blue) {
			return BarColor.GREEN;
		} else if (blue > red && blue > green) {
			return BarColor.BLUE;
		} else if (red == green && red == blue) {
			return BarColor.WHITE;
		} else if (red == green) {
			return BarColor.YELLOW;
		} else if (red == blue) {
			return BarColor.PURPLE;
		} else {
			return BarColor.GREEN;
		}
	}

	@Override
	public NamespacedKey getKey() {
		return key;
	}
}
