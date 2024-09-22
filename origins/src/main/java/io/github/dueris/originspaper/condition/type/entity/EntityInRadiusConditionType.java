package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.Comparison;
import io.github.dueris.originspaper.data.types.Shape;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class EntityInRadiusConditionType {

	public static boolean condition(Entity entity, Predicate<Tuple<Entity, Entity>> biEntityCondition, Shape shape, double radius, @NotNull Comparison comparison, int compareTo) {

		int countThreshold = switch (comparison) {
			case EQUAL, LESS_THAN_OR_EQUAL, GREATER_THAN -> compareTo + 1;
			case LESS_THAN, GREATER_THAN_OR_EQUAL -> compareTo;
			default -> -1;
		};

		int count = 0;
		for (Entity target : Shape.getEntities(shape, entity.level(), entity.getPosition(1.0F), radius)) {

			if (biEntityCondition.test(new Tuple<>(entity, target))) {
				++count;
			}

			if (count == countThreshold) {
				break;
			}

		}

		return comparison.compare(count, compareTo);

	}

	public static @NotNull ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("entity_in_radius"),
			new SerializableData()
				.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION)
				.add("shape", SerializableDataTypes.enumValue(Shape.class), Shape.CUBE)
				.add("radius", SerializableDataTypes.DOUBLE)
				.add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL)
				.add("compare_to", SerializableDataTypes.INT, 1),
			(data, entity) -> condition(entity,
				data.get("bientity_condition"),
				data.get("shape"),
				data.get("radius"),
				data.get("comparison"),
				data.get("compare_to")
			)
		);
	}

}
