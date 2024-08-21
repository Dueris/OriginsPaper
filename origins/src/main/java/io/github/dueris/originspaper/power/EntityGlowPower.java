package io.github.dueris.originspaper.power;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.calio.util.holder.ObjectProvider;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.Shape;
import io.github.dueris.originspaper.util.entity.GlowingEntitiesUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Set;

public class EntityGlowPower extends PowerType {
	private final ConditionFactory<Entity> entityCondition;
	private final ConditionFactory<Tuple<Entity, Entity>> bientityCondition;
	private final boolean useTeams;
	private final float red;
	private final float green;
	private final float blue;

	public EntityGlowPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority,
						   ConditionFactory<Entity> entityCondition, ConditionFactory<Tuple<Entity, Entity>> bientityCondition, boolean useTeams, float red, float green, float blue) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.entityCondition = entityCondition;
		this.bientityCondition = bientityCondition;
		this.useTeams = useTeams;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public static SerializableData buildFactory() {
		return PowerType.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("entity_glow"))
			.add("entity_condition", ApoliDataTypes.ENTITY_CONDITION, null)
			.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
			.add("use_teams", SerializableDataTypes.BOOLEAN, true)
			.add("red", SerializableDataTypes.FLOAT, 1.0F)
			.add("green", SerializableDataTypes.FLOAT, 1.0F)
			.add("blue", SerializableDataTypes.FLOAT, 1.0F);
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

	@Override
	public void tick(@NotNull Player player) {
		CraftPlayer p = (CraftPlayer) player.getBukkitEntity();
		ServerLevel level = (ServerLevel) player.level();
		Set<Entity> entities = Shape.getEntities(Shape.SPHERE, level, CraftLocation.toVec3D(p.getLocation()), 45);
		entities.add(player);
		GlowingEntitiesUtils utils = OriginsPaper.glowingEntitiesUtils;
		try {
			if (isActive(player)) {
				for (Entity entity : entities) {
					if (entityCondition != null && !entityCondition.test(entity)) {
						utils.unsetGlowing(entity.getBukkitEntity(), p);
						continue;
					}
					if (bientityCondition != null && !bientityCondition.test(new Tuple<>(player, entity))) {
						utils.unsetGlowing(entity.getBukkitEntity(), p);
						continue;
					}
					if (useTeams && player.getTeam() != null) {
						ChatColor color = CraftChatMessage.getColor(player.getTeam().getColor());
						utils.setGlowing(entity.getBukkitEntity(), p, (color == null || color.toString().equalsIgnoreCase("§r")) ? ChatColor.WHITE : color);
					} else {
						java.awt.Color awtColor = new Color(Math.round(red * 255), Math.round(green * 255), Math.round(blue * 255));
						utils.setGlowing(entity.getBukkitEntity(), p, translateBarColor(GlowTranslator.getColor(awtColor)));
					}
				}
			} else {
				for (Entity entity : entities) {
					utils.unsetGlowing(entity.getBukkitEntity(), p);
				}
			}
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	protected static class GlowTranslator implements ObjectProvider<BarColor> {
		public static BarColor getColor(java.awt.Color color) {
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
