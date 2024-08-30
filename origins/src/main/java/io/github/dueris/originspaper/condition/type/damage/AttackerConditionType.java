package io.github.dueris.originspaper.condition.type.damage;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

import java.util.function.Predicate;

public class AttackerConditionType {

	public static boolean condition(DamageSource damageSource, Predicate<Entity> entityCondition) {
		Entity attacker = damageSource.getEntity();
		return attacker != null
			&& entityCondition.test(attacker);
	}

	public static ConditionTypeFactory<Tuple<DamageSource, Float>> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("attacker"),
			new SerializableData()
				.add("entity_condition", ApoliDataTypes.ENTITY_CONDITION, null),
			(data, sourceAndAmount) -> condition(sourceAndAmount.getA(),
				data.getOrElse("entity_condition", e -> true)
			)
		);
	}

}
