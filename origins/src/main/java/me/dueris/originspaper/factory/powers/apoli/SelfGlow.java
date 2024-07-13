package me.dueris.originspaper.factory.powers.apoli;

import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionExecutor;
import me.dueris.originspaper.factory.data.types.Shape;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import me.dueris.originspaper.util.TextureLocation;
import me.dueris.originspaper.util.entity.GlowingEntitiesUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.Set;

public class SelfGlow extends PowerType {

	private final FactoryJsonObject entityCondition;
	private final FactoryJsonObject bientityCondition;
	private final boolean useTeams;
	private final float red;
	private final float green;
	private final float blue;

	public SelfGlow(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject entityCondition, FactoryJsonObject bientityCondition, boolean useTeams, float red, float green, float blue) {
		super(name, description, hidden, condition, loading_priority);
		this.entityCondition = entityCondition;
		this.bientityCondition = bientityCondition;
		this.useTeams = useTeams;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("self_glow"))
			.add("entity_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("bientity_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("use_teams", boolean.class, true)
			.add("red", float.class, 1.0F)
			.add("green", float.class, 1.0F)
			.add("blue", float.class, 1.0F);
	}

	public static ChatColor translateBarColor(BarColor barColor) {
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
	public void tick(Player p) {
		ServerPlayer player = ((CraftPlayer) p).getHandle();
		ServerLevel level = (ServerLevel) player.level();
		Set<Entity> entities = Shape.getEntities(Shape.SPHERE, level, CraftLocation.toVec3D(p.getLocation()), 60);
		entities.add(player);
		GlowingEntitiesUtils utils = OriginsPaper.glowingEntitiesUtils;
		try {
			if (isActive(p)) {
				for (Entity entity : entities) {
					if (!(entity instanceof ServerPlayer receiver)) continue;
					if (!ConditionExecutor.testEntity(entityCondition, entity.getBukkitEntity())) {
						utils.unsetGlowing(p, receiver.getBukkitEntity());
						continue;
					}
					if (!ConditionExecutor.testBiEntity(bientityCondition, p, entity.getBukkitEntity())) {
						utils.unsetGlowing(p, receiver.getBukkitEntity());
						continue;
					}
					if (useTeams && player.getTeam() != null) {
						utils.setGlowing(p, receiver.getBukkitEntity(), CraftChatMessage.getColor(player.getTeam().getColor()));
					} else {
						Color awtColor = new Color(red, green, blue);
						utils.setGlowing(p, receiver.getBukkitEntity(), translateBarColor(TextureLocation.convertToBarColor(awtColor)));
					}
				}
			} else {
				for (Entity entity : entities) {
					if (!(entity instanceof ServerPlayer receiver)) continue;
					utils.unsetGlowing(p, receiver.getBukkitEntity());
				}
			}
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean useTeams() {
		return useTeams;
	}

	public FactoryJsonObject getEntityCondition() {
		return entityCondition;
	}

	public FactoryJsonObject getBientityCondition() {
		return bientityCondition;
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
}
