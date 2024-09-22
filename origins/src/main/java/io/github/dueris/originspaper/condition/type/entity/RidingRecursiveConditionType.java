package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.Comparison;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class RidingRecursiveConditionType {

	public static boolean condition(@NotNull Entity entity, Predicate<Tuple<Entity, Entity>> biEntityCondition, Comparison comparison, int compareTo) {

		Entity vehicle = entity.getVehicle();
		int matches = 0;

		if (vehicle == null) {
			return comparison.compare(matches, compareTo);
		}

		while (vehicle != null) {

			if (biEntityCondition.test(new Tuple<>(entity, vehicle))) {
				matches++;
			}

			vehicle = vehicle.getVehicle();

		}

		return comparison.compare(matches, compareTo);

	}

	public static @NotNull ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("riding_recursive"),
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
