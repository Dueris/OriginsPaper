package io.github.dueris.originspaper.util;

import net.minecraft.resources.ResourceLocation;
import org.bukkit.boss.BarColor;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Renderer {
	private static final Map<ResourceLocation, String> namespace2path;
	private static final Map<String, RenderImpl> RENDER_MAP = new HashMap<>();

	static {
		List<String> spriteLocations = List.of(
			"origins:textures/gui/resource_bar.png",
			"origins:textures/gui/community/spiderkolo/resource_bar_01.png",
			"origins:textures/gui/community/spiderkolo/resource_bar_02.png",
			"origins:textures/gui/community/spiderkolo/resource_bar_03.png",
			"origins:textures/gui/community/spiderkolo/resource_bar_points_01.png",
			"origins:textures/gui/community/huang/resource_bar_01.png",
			"origins:textures/gui/community/huang/resource_bar_02.png"
		);
		Map<ResourceLocation, String> map = new HashMap<>();
		for (String spriteLocation : spriteLocations) {
			map.put(ResourceLocation.parse(spriteLocation), "assets/origins/" + spriteLocation.split(":")[1]);
		}
		namespace2path = Collections.unmodifiableMap(map);
	}

	public static void init() throws Throwable {
		JarFile plugin = new JarFile(Path.of(Renderer.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toFile());
		for (Map.Entry<ResourceLocation, String> entry : namespace2path.entrySet()) {
			ResourceLocation resourceLocation = entry.getKey();
			JarEntry jarEntry = plugin.getJarEntry(entry.getValue());

			if (jarEntry == null) {
				throw new IOException("Unable to locate " + entry.getValue() + " render image!");
			}

			try (InputStream entryStream = plugin.getInputStream(jarEntry)) {
				BufferedImage image = ImageIO.read(entryStream);
				if (image == null) {
					throw new IOException("Failed to read image for " + resourceLocation);
				}

				RenderMap textureLocation = new RenderMap(
					ResourceLocation.fromNamespaceAndPath(resourceLocation.getNamespace(), resourceLocation.getPath()), image);

				int index = 0;
				for (Map.Entry<String, BarColor> colorEntry : textureLocation.renderMap.entrySet()) {
					RENDER_MAP.put(colorEntry.getKey(), new RenderImpl(index, resourceLocation, image, colorEntry.getValue()));
					index++;
				}
			}
		}
	}

	public static @NotNull RenderImpl findRender(int index, ResourceLocation spriteLocation) {
		RenderImpl foundSprite = RENDER_MAP.get(spriteLocation + "=" + index);
		if (foundSprite != null) {
			return foundSprite;
		}

		throw new IllegalArgumentException("Unable to locate render impl of '" + spriteLocation + "' at index [" + index + "]");
	}

	public record RenderImpl(int index, ResourceLocation spriteLocation, BufferedImage backend, BarColor renderColor) {
	}

	private static class RenderMap {
		private final HashMap<String, BarColor> renderMap = new HashMap<>();

		public RenderMap(ResourceLocation key, @NotNull BufferedImage image) {
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
				BarColor c = convertToBarColor(color);
				renderMap.put(key + "=" + (index - 1), c);
				index++;
			}
		}

		public static BarColor convertToBarColor(@NotNull Color color) {
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
	}

}
