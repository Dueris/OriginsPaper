package io.github.dueris.originspaper.client;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.client.texture.TexturesImpl;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerManager;
import io.github.dueris.originspaper.power.type.CooldownPowerType;
import io.github.dueris.originspaper.power.type.HudRendered;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class HudRenderManager {
	public static final AtomicReference<List<Component>> nullBarComponents = new AtomicReference<>(null);
	public final Map<String, AssemblyComponent> assemblyComponentMap = new HashMap<>();
	public final Map<NamespacedKey, RenderInf> renderInf = new HashMap<>();
	private final NamespacedKey saveKey = new NamespacedKey(OriginsPaper.getPlugin(), "cooldowns");

	public HudRenderManager() {
		File icon = new File(OriginsPaper.getPlugin().getDataFolder(), "resources/empty_bar.png");
		if (!icon.exists()) {
			OriginsPaper.getPlugin().saveResource("resources/empty_bar.png", false);
		}

		try {
			nullBarComponents.set(makeRenderPixels(ImageIO.read(icon)));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void init() {
		for (ResourceLocation powerId : PowerManager.keySet()) {
			Power power = PowerManager.get(powerId);
			if (power.create(null) instanceof HudRendered hudRendered) {
				MinecraftClient.HUD_RENDER.register(hudRendered.getRenderSettings().getSpriteLocation(), powerId, hudRendered.getRenderSettings().getBarIndex(),
					new RenderInf(hudRendered.getRuntimeMax(), powerId.toString(), hudRendered.getRenderSettings().isInverted(), (hudRendered instanceof CooldownPowerType)));
			}
		}
	}

	public Component appendRender(float percentage, @NotNull RenderInf info, int height) {
		return assemblyComponentMap.get(info.getIcon())
			.assemble(percentage, height);
	}

	public long getRender(@NotNull Player player, NamespacedKey key) {
		PersistentDataContainer pdc = player.getPersistentDataContainer()
			.getOrDefault(saveKey, PersistentDataType.TAG_CONTAINER, player.getPersistentDataContainer()
				.getAdapterContext().newPersistentDataContainer());
		return Math.max(0, pdc.getOrDefault(key, PersistentDataType.LONG, 0L) - Instant.now().toEpochMilli());
	}

	public List<NamespacedKey> getRenders(@NotNull Player player) {
		PersistentDataContainer pdc = player.getPersistentDataContainer()
			.getOrDefault(saveKey, PersistentDataType.TAG_CONTAINER, player.getPersistentDataContainer()
				.getAdapterContext().newPersistentDataContainer());
		List<NamespacedKey> keys = new ArrayList<>(pdc.getKeys());
		keys.removeIf(key -> !currentlyDisplayingRender(player, key));
		return keys;
	}

	public void resetRendering(@NotNull Player player) {
		player.getPersistentDataContainer().remove(saveKey);
	}

	public boolean currentlyDisplayingRender(Player player, NamespacedKey key) {
		return getRender(player, key) > 0;
	}

	public void setRender(@NotNull Player player, NamespacedKey key, int cooldown) {
		PersistentDataContainer pdc = player.getPersistentDataContainer()
			.getOrDefault(saveKey, PersistentDataType.TAG_CONTAINER, player.getPersistentDataContainer()
				.getAdapterContext().newPersistentDataContainer());
		pdc.set(key, PersistentDataType.LONG, Instant.now().toEpochMilli() + (cooldown * 50L));
		player.getPersistentDataContainer().set(saveKey, PersistentDataType.TAG_CONTAINER, pdc);
	}

	public void setRender(Player player, NamespacedKey key) {
		setRender(player, key, renderInf.get(key).getRuntime());
	}

	public AssemblyComponent makeAssemblyComponent(BufferedImage image) {
		return new AssemblyComponent(makeRenderPixels(image), makeIconPixels(image));
	}

	public Component makeIconPixels(@NotNull BufferedImage image) {
		BufferedImage iconImage = image.getSubimage(73, 0, 8, 8);
		Component icon = Component.empty();
		String pixels = "\ue000\ue001\ue002\ue003\ue004\ue005\ue006\ue007";

		for (int x = 0; x < 8; ++x) {
			for (int y = 0; y < 8; ++y) {
				int col = iconImage.getRGB(x, y);
				if ((new Color(col, true)).getAlpha() < 255) {
					icon = icon.append(Component.text("\uf002"));
				} else {
					icon = icon.append(Component.text(pixels.charAt(y)).color(TextColor.color(col)));
				}

				icon = icon.append(Component.text(y == 7 ? "\uf001" : "\uf000"));
			}
		}

		return icon;
	}

	public List<Component> makeRenderPixels(@NotNull BufferedImage image) {
		BufferedImage barImage = image.getSubimage(0, 2, 71, 5);
		String pixels = "\ue002\ue003\ue004\ue005\ue006";
		List<Component> result = new ArrayList<>();

		for (int x = 0; x < 71; ++x) {
			Component c = Component.empty();

			for (int y = 0; y < 5; ++y) {
				int col = barImage.getRGB(x, y);
				if (col == 0) {
					c = c.append(Component.text("\uf002"));
				} else {
					c = c.append(Component.text(pixels.charAt(y)).color(TextColor.color(col)));
				}

				if (y != 4) {
					c = c.append(Component.text("\uf000"));
				}
			}

			result.add(c);
		}

		return result;
	}

	public void register(ResourceLocation textureKey, @NotNull ResourceLocation powerId, int barIndex, @NotNull RenderInf info) {
		List<Tuple<Integer, TexturesImpl.TextureLocation>> textureLocation = TexturesImpl.REGISTRY.get(textureKey);
		Tuple<Integer, TexturesImpl.TextureLocation> tuple = textureLocation.get(barIndex);
		if (!assemblyComponentMap.containsKey(powerId.toString())) {
			AssemblyComponent component = makeAssemblyComponent(tuple.getB().image());
			assemblyComponentMap.put(powerId.toString(), component);
		}
		renderInf.put(CraftNamespacedKey.fromMinecraft(powerId), info);
	}

	public record AssemblyComponent(List<Component> barPieces, Component icon) {

		public @NotNull Component assemble(float completion, int height) {
			double num = Math.floor((float) this.barPieces.size() * completion);
			Component result = this.icon.append(Component.text("\uf002"));

			for (int i = 0; i < this.barPieces.size(); ++i) {
				result = result.append(((double) i <= num ? this.barPieces : HudRenderManager.nullBarComponents.get()).get(i));
				result = result.append(Component.text("\uf001"));
			}

			@Subst("origins:resource_bar/height_0") String formatted = "origins:resource_bar/height_%s".formatted(height);
			return result.font(Key.key(formatted));
		}
	}

	@SuppressWarnings("unused")
	public static final class RenderInf {
		private final boolean reversed;
		private final String icon;
		private final boolean tick;
		private int runtime;

		public RenderInf(int runtime, String icon, boolean reversed, boolean tick) {
			this.runtime = runtime;
			this.icon = icon;
			this.reversed = reversed;
			this.tick = tick;
		}

		public RenderInf(int runtime, String icon, boolean tick) {
			this(runtime, icon, tick, false);
		}

		public RenderInf(int runtime, boolean reversed, boolean tick) {
			this(runtime, null, reversed);
		}

		public RenderInf(int runtime) {
			this(runtime, null, false);
		}

		public boolean isReversed() {
			return reversed;
		}

		public String getIcon() {
			return icon;
		}

		public int getRuntime() {
			return runtime;
		}

		public void setRuntime(int runtime) {
			this.runtime = runtime;
		}

		public boolean shouldTick() {
			return tick;
		}
	}
}

