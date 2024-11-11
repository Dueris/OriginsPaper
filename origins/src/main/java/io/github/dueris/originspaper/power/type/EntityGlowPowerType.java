package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.BiEntityCondition;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.plugin.OriginsPlugin;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerConfiguration;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Predicate;

public class EntityGlowPowerType extends PowerType {

	public static final TypedDataObjectFactory<EntityGlowPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("entity_condition", EntityCondition.DATA_TYPE.optional(), Optional.empty())
			.add("bientity_condition", BiEntityCondition.DATA_TYPE.optional(), Optional.empty())
			.add("use_teams", SerializableDataTypes.BOOLEAN, true)
			.add("red", ApoliDataTypes.NORMALIZED_FLOAT, 1.0F)
			.add("green", ApoliDataTypes.NORMALIZED_FLOAT, 1.0F)
			.add("blue", ApoliDataTypes.NORMALIZED_FLOAT, 1.0F),
		(data, condition) -> new EntityGlowPowerType(
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
			.set("use_teams", powerType.useTeams)
			.set("red", powerType.red)
			.set("green", powerType.green)
			.set("blue", powerType.blue)
	);

	private final Optional<EntityCondition> entityCondition;
	private final Optional<BiEntityCondition> biEntityCondition;

	private final boolean useTeams;

	private final float red;
	private final float green;
	private final float blue;

	public EntityGlowPowerType(Optional<EntityCondition> entityCondition, Optional<BiEntityCondition> biEntityCondition, boolean useTeams, float red, float green, float blue, Optional<EntityCondition> condition) {
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
		return PowerTypes.ENTITY_GLOW;
	}

	public boolean doesApply(Entity entity) {
		return entityCondition.map(condition -> condition.test(entity)).orElse(true)
			&& biEntityCondition.map(condition -> condition.test(getHolder(), entity)).orElse(true);
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
	public void onRemoved() {
		try {
			clear();
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	public void clear() throws ReflectiveOperationException {
		if (getHolder() instanceof ServerPlayer livingEntity) {
			for (Entity entity : OriginsPlugin.glowingEntitiesUtils.getGlowing(livingEntity)) {
				OriginsPlugin.glowingEntitiesUtils.unsetGlowing(entity.getBukkitEntity(), livingEntity.getBukkitEntity());
			}
		}
	}
}
