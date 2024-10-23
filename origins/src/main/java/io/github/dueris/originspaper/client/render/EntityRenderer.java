package io.github.dueris.originspaper.client.render;

import io.github.dueris.calio.util.holder.ObjectProvider;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.plugin.OriginsPlugin;
import io.github.dueris.originspaper.power.type.EntityGlowPowerType;
import io.github.dueris.originspaper.power.type.PreventEntityRenderPowerType;
import io.github.dueris.originspaper.util.GlowingEntitiesUtils;
import io.github.dueris.originspaper.util.Shape;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public final class EntityRenderer {
	private final ServerPlayer player;
	private final List<Entity> lastSeenEntities = new LinkedList<>();

	public EntityRenderer(ServerPlayer player) {
		this.player = player;
	}

	public static ChatColor translateBarColor(@NotNull BarColor barColor) {
		return switch (barColor) {
			case BLUE -> ChatColor.BLUE;
			case GREEN -> ChatColor.GREEN;
			case PINK -> ChatColor.LIGHT_PURPLE;
			case PURPLE -> ChatColor.DARK_PURPLE;
			case RED -> ChatColor.RED;
			case YELLOW -> ChatColor.YELLOW;
			default -> ChatColor.WHITE;
		};
	}

	public void tick() {
		PowerHolderComponent component = PowerHolderComponent.KEY.getNullable(player);
		if (component == null) {
			return;
		}

		ServerLevel level = (ServerLevel) player.level();
		Set<Entity> entities = Shape.getEntities(Shape.SPHERE, level, player.position(), 45);
		entities.add(player);

		for (EntityGlowPowerType power : component.getPowerTypes(EntityGlowPowerType.class, true)) {
			CraftPlayer p = player.getBukkitEntity();
			GlowingEntitiesUtils utils = OriginsPlugin.glowingEntitiesUtils;
			try {
				if (power.isActive()) {
					for (Entity entity : entities) {
						boolean applies = power.doesApply(entity);
						if (!applies) {
							utils.unsetGlowing(entity.getBukkitEntity(), p);
							continue;
						}
						if (power.usesTeams() && player.getTeam() != null) {
							ChatColor color = CraftChatMessage.getColor(player.getTeam().getColor());
							utils.setGlowing(entity.getBukkitEntity(), p, (color == null || color.toString().equalsIgnoreCase("§r")) ? ChatColor.WHITE : color);
						} else {
							java.awt.Color awtColor = new Color(Math.round(power.getRed() * 255), Math.round(power.getGreen() * 255), Math.round(power.getBlue() * 255));
							utils.setGlowing(entity.getBukkitEntity(), p, translateBarColor(GlowTranslator.getColor(awtColor)));
						}
					}
				} else {
					power.clear();
				}
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException(e);
			}
		}

		for (PreventEntityRenderPowerType power : component.getPowerTypes(PreventEntityRenderPowerType.class, true)) {
			CraftPlayer p = player.getBukkitEntity();
			if (power.isActive()) {
				for (Entity entity : entities) {
					boolean applies = power.doesApply(entity);
					if (!applies) {
						p.showEntity(OriginsPlugin.plugin, entity.getBukkitEntity());
						continue;
					}
					p.hideEntity(OriginsPlugin.plugin, entity.getBukkitEntity());
				}
			} else {
				for (Entity entity : entities) {
					p.showEntity(OriginsPlugin.plugin, entity.getBukkitEntity());
				}
			}
		}
	}

	public ServerPlayer player() {
		return player;
	}

	public static class GlowTranslator implements ObjectProvider<BarColor> {
		public static BarColor getColor(java.awt.@NotNull Color color) {
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
		public BarColor get() {
			return BarColor.WHITE;
		}
	}

}
