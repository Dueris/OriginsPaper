package me.dueris.originspaper.util;

import me.dueris.calio.registry.Registrable;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TextureLocation implements Registrable {
	public static HashMap<String, BarColor> textureMap = new HashMap<>();
	private final NamespacedKey key;
	private final BufferedImage image;
	private final List<BarColor> containedColors = new ArrayList<>();

	public TextureLocation(NamespacedKey key, BufferedImage image) {
		this.key = key;
		this.image = image;
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
			this.containedColors.add(c);
			textureMap.put(key.key().asString() + "/-/" + index, c);
			index++;
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
	public NamespacedKey key() {
		return this.key;
	}

	public BufferedImage getImage() {
		return image;
	}
}