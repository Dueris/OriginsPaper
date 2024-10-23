package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Predicate;

public class PreventEntityCollisionPowerType extends PowerType {

	private final Predicate<Tuple<Entity, Entity>> biEntityCondition;

	public PreventEntityCollisionPowerType(Power power, LivingEntity entity, Predicate<Tuple<Entity, Entity>> biEntityCondition) {
		super(power, entity);
		this.biEntityCondition = biEntityCondition;
	}

	public boolean doesApply(Entity target) {
		return biEntityCondition == null || biEntityCondition.test(new Tuple<>(entity, target));
	}

	public static boolean doesApply(Entity fromEntity, Entity collidingEntity) {
		return PowerHolderComponent.hasPowerType(fromEntity, PreventEntityCollisionPowerType.class, p -> p.doesApply(collidingEntity))
			|| PowerHolderComponent.hasPowerType(collidingEntity, PreventEntityCollisionPowerType.class, p -> p.doesApply(fromEntity));
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("prevent_entity_collision"),
			new SerializableData()
				.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null),
			data -> (power, entity) -> new PreventEntityCollisionPowerType(power, entity,
				data.get("bientity_condition")
			)
		).allowCondition();
	}

}
