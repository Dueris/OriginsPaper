package io.github.dueris.originspaper.client.texture;

import io.github.dueris.originspaper.OriginsPaper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

// 8 pixels tall - 81 pixels wide
public class TexturesImpl {
	public static final ConcurrentHashMap<ResourceLocation, List<Tuple<Integer, TextureLocation>>> REGISTRY = new ConcurrentHashMap<>() {
		@Override
		public List<Tuple<Integer, TextureLocation>> get(Object key) {
			ResourceLocation asResourceLocation = (ResourceLocation) key;
			if (!asResourceLocation.getNamespace().equalsIgnoreCase("origins")) {
				asResourceLocation = ResourceLocation.fromNamespaceAndPath("origins", asResourceLocation.getPath());
			}
			return super.get(asResourceLocation);
		}
	};

	public static void init() {
		try (JarFile originsPaper = new JarFile(OriginsPaper.jarFile.toFile())) {
			for (GuiLocation value : GuiLocation.values()) {
				JarEntry entry = originsPaper.getJarEntry("assets/origins/" + value.location);
				if (entry == null) {
					throw new RuntimeException("Unable to get resource value of '{}', corrupted??".replace("{}", value.location));
				}

				try (InputStream stream = originsPaper.getInputStream(entry)) {
					BufferedImage guiImage = ImageIO.read(stream);

					final List<BufferedImage> subImages = new LinkedList<>();

					int w = 81;
					int h = 8;
					int g = 2;

					int tH = guiImage.getHeight();
					int y = -2;

					while (y + h <= tH) {
						if (y > 0) {
							BufferedImage subImage = guiImage.getSubimage(0, y, w, h);
							subImages.add(subImage);
						}

						y += h + g;
					}

					int i = 0;
					ResourceLocation toPut = OriginsPaper.identifier(value.location);
					if (!REGISTRY.containsKey(toPut)) {
						REGISTRY.put(toPut, new LinkedList<>());
					}
					for (BufferedImage subImage : subImages) {
						REGISTRY.get(toPut).add(new Tuple<>(
							i, new TextureLocation(toPut, i, subImage)
						));
						i++;
					}
				}
			}
		} catch (Throwable e) {
			throw new RuntimeException("An unexpected error occurred when parsing the OriginsPaper renderer!", e);
		}
		if (REGISTRY.isEmpty()) {
			throw new IllegalStateException("Texture registry was empty after parsing! This literally shouldnt be possible...");
		}
	}

	private enum GuiLocation {
		HUANG_01("textures/gui/community/huang/resource_bar_01.png"),
		HUANG_02("textures/gui/community/huang/resource_bar_02.png"),
		SPIDERKOLO_01("textures/gui/community/spiderkolo/resource_bar_01.png"),
		SPIDERKOLO_02("textures/gui/community/spiderkolo/resource_bar_02.png"),
		SPIDERKOLO_03("textures/gui/community/spiderkolo/resource_bar_03.png"),
		RESOURCE_BAR("textures/gui/resource_bar.png");

		final String location;

		GuiLocation(String location) {
			this.location = location;
		}
	}

	public record TextureLocation(ResourceLocation identifier, int barIndex, BufferedImage image) {
	}

	public record PowerWrappedTextureLocation(ResourceLocation powerId, TextureLocation textureLocation) {
	}
}
