package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Predicate;

public class PreventEntityRenderPowerType extends PowerType {

	private final Predicate<Entity> entityCondition;
	private final Predicate<Tuple<Entity, Entity>> bientityCondition;

	public PreventEntityRenderPowerType(Power power, LivingEntity entity, Predicate<Entity> entityCondition, Predicate<Tuple<Entity, Entity>> bientityCondition) {
		super(power, entity);
		this.entityCondition = entityCondition;
		this.bientityCondition = bientityCondition;
	}

	public boolean doesApply(Entity e) {
		return (entityCondition == null || entityCondition.test(e))
			&& (bientityCondition == null || bientityCondition.test(new Tuple<>(entity, e)));
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("prevent_entity_render"),
			new SerializableData()
				.add("entity_condition", ApoliDataTypes.ENTITY_CONDITION, null)
				.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null),
			data -> (power, entity) -> new PreventEntityRenderPowerType(power, entity,
				data.get("entity_condition"),
				data.get("bientity_condition")
			)
		).allowCondition();
	}

}
