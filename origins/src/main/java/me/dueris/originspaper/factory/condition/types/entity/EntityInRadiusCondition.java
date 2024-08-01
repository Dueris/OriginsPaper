package me.dueris.originspaper.factory.condition.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.condition.ConditionFactory;
import me.dueris.originspaper.factory.data.ApoliDataTypes;
import me.dueris.originspaper.factory.data.types.Comparison;
import me.dueris.originspaper.factory.data.types.Shape;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class EntityInRadiusCondition {

	public static boolean condition(@NotNull DeserializedFactoryJson data, Entity entity) {

		Predicate<Tuple<Entity, Entity>> biEntityCondition = data.get("bientity_condition");
		Shape shape = data.get("shape");

		Comparison comparison = data.get("comparison");
		int compareTo = data.get("compare_to");

		double radius = data.get("radius");
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

	public static @NotNull ConditionFactory<Entity> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("entity_in_radius"),
			InstanceDefiner.instanceDefiner()
				.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION)
				.add("shape", SerializableDataTypes.enumValue(Shape.class), Shape.CUBE)
				.add("radius", SerializableDataTypes.DOUBLE)
				.add("compare_to", SerializableDataTypes.INT, 1)
				.add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL),
			EntityInRadiusCondition::condition
		);
	}
}
