package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.plugin.OriginsPlugin;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Predicate;

public class EntityGlowPowerType extends PowerType {

	private final Predicate<Entity> entityCondition;
	private final Predicate<Tuple<Entity, Entity>> biEntityCondition;

	private final boolean useTeams;

	private final float red;
	private final float green;
	private final float blue;

	public EntityGlowPowerType(Power power, LivingEntity entity, Predicate<Entity> entityCondition, Predicate<Tuple<Entity, Entity>> biEntityCondition, boolean useTeams, float red, float green, float blue) {
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
			OriginsPaper.apoliIdentifier("entity_glow"),
			new SerializableData()
				.add("entity_condition", ApoliDataTypes.ENTITY_CONDITION, null)
				.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
				.add("use_teams", SerializableDataTypes.BOOLEAN, true)
				.add("red", SerializableDataTypes.FLOAT, 1.0F)
				.add("green", SerializableDataTypes.FLOAT, 1.0F)
				.add("blue", SerializableDataTypes.FLOAT, 1.0F),
			data -> (power, entity) -> new EntityGlowPowerType(power, entity,
				data.get("entity_condition"),
				data.get("bientity_condition"),
				data.getBoolean("use_teams"),
				data.getFloat("red"),
				data.getFloat("green"),
				data.getFloat("blue")
			)
		).allowCondition();
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
		if (entity instanceof ServerPlayer livingEntity) {
			for (Entity entity : OriginsPlugin.glowingEntitiesUtils.getGlowing(livingEntity)) {
				OriginsPlugin.glowingEntitiesUtils.unsetGlowing(entity.getBukkitEntity(), livingEntity.getBukkitEntity());
			}
		}
	}

	public boolean doesApply(Entity e) {
		return (entityCondition == null || entityCondition.test(e))
			&& (biEntityCondition == null || biEntityCondition.test(new Tuple<>(entity, e)));
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

