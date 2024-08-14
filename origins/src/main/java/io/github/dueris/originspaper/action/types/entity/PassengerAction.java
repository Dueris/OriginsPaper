package io.github.dueris.originspaper.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class PassengerAction {

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(OriginsPaper.apoliIdentifier("passenger_action"),
			InstanceDefiner.instanceDefiner()
				.add("action", ApoliDataTypes.ENTITY_ACTION, null)
				.add("bientity_action", ApoliDataTypes.BIENTITY_ACTION, null)
				.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
				.add("recursive", SerializableDataTypes.BOOLEAN, false),
			(data, entity) -> {
				Consumer<Entity> entityAction = data.get("action");
				Consumer<Tuple<Entity, Entity>> bientityAction = data.get("bientity_action");
				Predicate<Tuple<Entity, Entity>> cond = data.get("bientity_condition");
				if (!entity.isVehicle() || (entityAction == null && bientityAction == null)) {
					return;
				}
				Iterable<Entity> passengers = data.getBoolean("recursive") ? entity.getIndirectPassengers() : entity.getPassengers();
				for (Entity passenger : passengers) {
					if (cond == null || cond.test(new Tuple<>(passenger, entity))) {
						if (entityAction != null) {
							entityAction.accept(passenger);
						}
						if (bientityAction != null) {
							bientityAction.accept(new Tuple<>(passenger, entity));
						}
					}
				}
			}
		);
	}
}
