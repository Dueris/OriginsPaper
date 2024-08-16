package io.github.dueris.originspaper.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.Shape;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class AreaOfEffectAction {

	public static void action(@NotNull SerializableData.Instance data, @NotNull Entity entity) {

		Consumer<Tuple<Entity, Entity>> biEntityAction = data.get("bientity_action");
		Predicate<Tuple<Entity, Entity>> biEntityCondition = data.get("bientity_condition");
		Shape shape = data.get("shape");

		boolean includeActor = data.get("include_actor");
		double radius = data.get("radius");

		for (Entity target : Shape.getEntities(shape, entity.level(), entity.getPosition(1.0f), radius)) {

			if (target == entity && !includeActor) {
				continue;
			}

			Tuple<Entity, Entity> actorAndTarget = new Tuple<>(entity, target);
			if (biEntityCondition == null || biEntityCondition.test(actorAndTarget)) {
				biEntityAction.accept(actorAndTarget);
			}

		}

	}

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(
				OriginsPaper.apoliIdentifier("area_of_effect"),
				SerializableData.serializableData()
						.add("radius", SerializableDataTypes.DOUBLE, 16D)
						.add("shape", SerializableDataTypes.enumValue(Shape.class), Shape.CUBE)
						.add("bientity_action", ApoliDataTypes.BIENTITY_ACTION)
						.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
						.add("include_target", SerializableDataTypes.BOOLEAN, false)
						.add("include_actor", SerializableDataTypes.BOOLEAN, false),
				AreaOfEffectAction::action
		);
	}
}
