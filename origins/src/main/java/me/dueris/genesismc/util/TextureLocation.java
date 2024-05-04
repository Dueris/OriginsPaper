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
import java.util.Comparator;
import java.util.HashMap;

public class TextureLocation implements Registrable {
    public static HashMap<String, BarColor> textureMap = new HashMap<>();
    private final NamespacedKey key;

    public TextureLocation(NamespacedKey key) {
	this.key = key;
    }

    public static void parseAll() throws IOException {

	if (CraftApoli.datapacksInDir() == null) return;
	for (File pack : CraftApoli.datapacksInDir()) {
	    if (pack == null) continue;
	    if (!pack.isDirectory()) continue;
	    if (pack.listFiles() == null) continue;
	    for (File folders : pack.listFiles()) {
		if (folders == null) continue;
		if (folders.getName().equalsIgnoreCase("assets")) {
		    for (File root : folders.listFiles()) {
			String rootname = root.getName();
			for (File file : root.listFiles()) {
			    if (file.getName().equalsIgnoreCase("textures")) {
				Files.walk(file.toPath())
				    .sorted(Comparator.reverseOrder()) // Sort in reverse order
				    .forEach(path -> {
					if (path.toString().endsWith(".png")) {
					    GenesisMC.getPlugin().registry.retrieve(Registries.TEXTURE_LOCATION).register(new TextureLocation(NamespacedKey.fromString(rootname + ":textures" + path.toAbsolutePath().toString().replace(file.getAbsolutePath(), "").replace("\\", "/"))));
					}
				    });
			    }
			}
		    }
		}
	    }
	}

	for (TextureLocation location : ((Registrar<TextureLocation>) GenesisMC.getPlugin().registry.retrieve(Registries.TEXTURE_LOCATION)).values()) {
	    if (CraftApoli.datapacksInDir() == null) return;
	    for (File pack : CraftApoli.datapacksInDir()) {
		if (pack == null) continue;
		if (!pack.isDirectory()) continue;
		if (pack.listFiles() == null) continue;
		for (File folders : pack.listFiles()) {
		    if (folders.getName().equalsIgnoreCase("assets")) {
			File mainRoot = new File(folders, location.key.asString().split(":")[0]);
			if (!mainRoot.exists()) continue;
			Path path = Path.of(mainRoot.getAbsolutePath() + File.separator + location.key.asString().split(":")[1]);
			if (path.toFile().exists()) {
			    File resource = path.toFile();
			    try {
				BufferedImage image = ImageIO.read(resource);

				int width = image.getWidth();
				int height = image.getHeight();

				Color[] originalPixels = new Color[height];
				Color[] modifiedPixels = new Color[height];

				for (int y = 0; y < height; y++) {
				    originalPixels[y] = new Color(image.getRGB(2, y));
				}

				int currentIndex = 0;
				while (currentIndex < height) {
				    if (currentIndex >= 180) break;

				    currentIndex += 2;
				    modifiedPixels[currentIndex] = originalPixels[currentIndex];
				    currentIndex += 9;
				    if (height - currentIndex <= 3) {
					break;
				    }
				    modifiedPixels[currentIndex] = originalPixels[currentIndex];
				    currentIndex += 9;
				}

				int index = 0;
				for (Color color : modifiedPixels) {
				    if (color == null) continue;
				    textureMap.put(location.getKey().asString() + "/-/" + index, convertToBarColor(color));
				    index++;
				}

			    } catch (IOException e) {
				e.printStackTrace();
			    }
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
