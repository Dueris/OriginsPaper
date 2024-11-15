package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.client.render.EntityRenderer;
import io.github.dueris.originspaper.condition.BiEntityCondition;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.plugin.OriginsPlugin;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.util.GlowingEntitiesUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Optional;

import static io.github.dueris.originspaper.client.render.EntityRenderer.translateBarColor;

public class SelfGlowPowerType extends PowerType {

	public static final TypedDataObjectFactory<SelfGlowPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("entity_condition", EntityCondition.DATA_TYPE.optional(), Optional.empty())
			.add("bientity_condition", BiEntityCondition.DATA_TYPE.optional(), Optional.empty())
			.add("use_teams", SerializableDataTypes.BOOLEAN, true)
			.add("red", ApoliDataTypes.NORMALIZED_FLOAT, 1.0F)
			.add("green", ApoliDataTypes.NORMALIZED_FLOAT, 1.0F)
			.add("blue", ApoliDataTypes.NORMALIZED_FLOAT, 1.0F),
		(data, condition) -> new SelfGlowPowerType(
			data.get("entity_condition"),
			data.get("bientity_condition"),
			data.get("use_teams"),
			data.get("red"),
			data.get("green"),
			data.get("blue"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("entity_condition", powerType.entityCondition)
			.set("bientity_condition", powerType.biEntityCondition)
			.set("use_teams", powerType.usesTeams())
			.set("red", powerType.getRed())
			.set("green", powerType.getGreen())
			.set("blue", powerType.getBlue())
	);

	private final Optional<EntityCondition> entityCondition;
	private final Optional<BiEntityCondition> biEntityCondition;

	private final boolean useTeams;

	private final float red;
	private final float green;
	private final float blue;

	public SelfGlowPowerType(Optional<EntityCondition> entityCondition, Optional<BiEntityCondition> biEntityCondition, boolean useTeams, float red, float green, float blue, Optional<EntityCondition> condition) {
		super(condition);
		this.entityCondition = entityCondition;
		this.biEntityCondition = biEntityCondition;
		this.useTeams = useTeams;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.SELF_GLOW;
	}

	public boolean doesApply(Entity viewer) {
		return entityCondition.map(condition -> condition.test(viewer)).orElse(true)
			&& biEntityCondition.map(condition -> condition.test(viewer, getHolder())).orElse(true);
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

	@Override
	public void serverTick() {
		GlowingEntitiesUtils utils = OriginsPlugin.glowingEntitiesUtils;
		try {
			if (getHolder() instanceof net.minecraft.world.entity.player.Player player && isActive()) {
				if (useTeams && getHolder().getTeam() != null) {
					utils.setGlowing(getHolder().getBukkitEntity(), (Player) getHolder().getBukkitEntity(), CraftChatMessage.getColor(getHolder().getTeam().getColor()));
				} else {
					java.awt.Color awtColor = new Color(Math.round(getRed() * 255), Math.round(getGreen() * 255), Math.round(getBlue() * 255));
					utils.setGlowing(player.getBukkitEntity(), (Player) getHolder().getBukkitEntity(), translateBarColor(EntityRenderer.GlowTranslator.getColor(awtColor)));
				}
			} else {
				if (!(getHolder() instanceof ServerPlayer receiver)) return;
				utils.unsetGlowing(getHolder().getBukkitEntity(), receiver.getBukkitEntity());
			}
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}
}
