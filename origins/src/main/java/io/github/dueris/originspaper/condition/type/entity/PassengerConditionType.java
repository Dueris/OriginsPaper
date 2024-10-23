package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.util.Comparison;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;

import java.util.function.Predicate;

public class PassengerConditionType {

	public static boolean condition(Entity entity, Predicate<Tuple<Entity, Entity>> biEntityCondition, Comparison comparison, int compareTo) {

		int matches = (int) entity.getPassengers()
			.stream()
			.map(passenger -> new Tuple<>(passenger, entity))
			.filter(biEntityCondition)
			.count();

		return comparison.compare(matches, compareTo);

	}

	public static ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("passenger"),
			new SerializableData()
				.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
				.add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL)
				.add("compare_to", SerializableDataTypes.INT, 1),
			(data, entity) -> condition(entity,
				data.getOrElse("bientity_condition", actorAndTarget -> true),
				data.get("comparison"),
				data.get("compare_to")
			)
		);
	}

}
