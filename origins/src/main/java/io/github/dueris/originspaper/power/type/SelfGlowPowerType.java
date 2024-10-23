package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.client.render.EntityRenderer;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.plugin.OriginsPlugin;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import io.github.dueris.originspaper.util.GlowingEntitiesUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.function.Predicate;

import static io.github.dueris.originspaper.client.render.EntityRenderer.translateBarColor;

public class SelfGlowPowerType extends PowerType {

	private final Predicate<Entity> entityCondition;
	private final Predicate<Tuple<Entity, Entity>> biEntityCondition;

	private final boolean useTeams;

	private final float red;
	private final float green;
	private final float blue;

	public SelfGlowPowerType(Power power, LivingEntity entity, Predicate<Entity> entityCondition, Predicate<Tuple<Entity, Entity>> biEntityCondition, boolean useTeams, float red, float green, float blue) {
		super(power, entity);
		this.entityCondition = entityCondition;
		this.biEntityCondition = biEntityCondition;
		this.useTeams = useTeams;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("self_glow"),
			new SerializableData()
				.add("entity_condition", ApoliDataTypes.ENTITY_CONDITION, null)
				.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
				.add("use_teams", SerializableDataTypes.BOOLEAN, true)
				.add("red", SerializableDataTypes.FLOAT, 1.0F)
				.add("green", SerializableDataTypes.FLOAT, 1.0F)
				.add("blue", SerializableDataTypes.FLOAT, 1.0F),
			data -> (power, entity) -> new SelfGlowPowerType(power, entity,
				data.get("entity_condition"),
				data.get("bientity_condition"),
				data.getBoolean("use_teams"),
				data.getFloat("red"),
				data.getFloat("green"),
				data.getFloat("blue")
			)
		).allowCondition();
	}

	public boolean doesApply(Entity viewer) {
		return (entityCondition == null || entityCondition.test(viewer))
			&& (biEntityCondition == null || biEntityCondition.test(new Tuple<>(viewer, entity)));
	}

	@Override
	public void tick() {
		GlowingEntitiesUtils utils = OriginsPlugin.glowingEntitiesUtils;
		try {
			if (entity instanceof net.minecraft.world.entity.player.Player player && isActive()) {
				if (useTeams && entity.getTeam() != null) {
					utils.setGlowing(entity.getBukkitEntity(), (Player) entity.getBukkitEntity(), CraftChatMessage.getColor(entity.getTeam().getColor()));
				} else {
					java.awt.Color awtColor = new Color(Math.round(getRed() * 255), Math.round(getGreen() * 255), Math.round(getBlue() * 255));
					utils.setGlowing(player.getBukkitEntity(), (Player) entity.getBukkitEntity(), translateBarColor(EntityRenderer.GlowTranslator.getColor(awtColor)));
				}
			} else {
				if (!(entity instanceof ServerPlayer receiver)) return;
				utils.unsetGlowing(entity.getBukkitEntity(), receiver.getBukkitEntity());
			}
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean usesTeams() {
		return useTeams;
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
