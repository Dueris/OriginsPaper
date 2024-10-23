package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.util.Shape;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class AreaOfEffectActionType {

	public static void action(Entity entity, Consumer<Tuple<Entity, Entity>> biEntityAction, Predicate<Tuple<Entity, Entity>> biEntityCondition, Shape shape, double radius, boolean includeActor) {

		for (Entity target : Shape.getEntities(shape, entity.level(), entity.getPosition(1.0f), radius)) {

			if (entity.equals(target) && !includeActor) {
				continue;
			}

			Tuple<Entity, Entity> actorAndTarget = new Tuple<>(entity, target);

			if (biEntityCondition.test(actorAndTarget)) {
				biEntityAction.accept(actorAndTarget);
			}

		}

	}

	public static ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("area_of_effect"),
			new SerializableData()
				.add("bientity_action", ApoliDataTypes.BIENTITY_ACTION)
				.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
				.add("shape", SerializableDataType.enumValue(Shape.class), Shape.CUBE)
				.add("radius", SerializableDataTypes.DOUBLE, 16D)
				.add("include_actor", SerializableDataTypes.BOOLEAN, false),
			(data, entity) -> action(entity,
				data.getOrElse("bientity_action", actorAndTarget -> {
				}),
				data.getOrElse("bientity_condition", actorAndTarget -> true),
				data.get("shape"),
				data.get("radius"),
				data.get("include_actor")
			)
		);
	}

}

