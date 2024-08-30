package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class PassengerActionType {

	public static void action(@NotNull Entity entity, Consumer<Entity> entityAction, Consumer<Tuple<Entity, Entity>> biEntityAction, Predicate<Tuple<Entity, Entity>> biEntityCondition, boolean recursive) {

		if (!entity.isVehicle()) {
			return;
		}

		Iterable<Entity> passengers = recursive
			? entity.getIndirectPassengers()
			: entity.getPassengers();

		for (Entity passenger : passengers) {

			Tuple<Entity, Entity> actorAndTarget = new Tuple<>(passenger, entity);

			if (biEntityCondition.test(actorAndTarget)) {
				entityAction.accept(passenger);
				biEntityAction.accept(actorAndTarget);
			}

		}

	}

	public static @NotNull ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("passenger_action"),
			new SerializableData()
				.add("action", ApoliDataTypes.ENTITY_ACTION, null)
				.add("bientity_action", ApoliDataTypes.BIENTITY_ACTION, null)
				.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
				.add("recursive", SerializableDataTypes.BOOLEAN, false),
			(data, entity) -> action(entity,
				data.getOrElse("action", e -> {
				}),
				data.getOrElse("bientity_action", actorAndTarget -> {
				}),
				data.getOrElse("bientity_condition", actorAndTarget -> true),
				data.get("recursive")
			)
		);
	}

}
