package me.dueris.originspaper.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.action.ActionFactory;
import me.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class RidingAction {

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(OriginsPaper.apoliIdentifier("riding_action"),
			InstanceDefiner.instanceDefiner()
				.add("action", ApoliDataTypes.ENTITY_ACTION, null)
				.add("bientity_action", ApoliDataTypes.BIENTITY_ACTION, null)
				.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
				.add("recursive", SerializableDataTypes.BOOLEAN, false),
			(data, entity) -> {
				Consumer<Entity> entityAction = data.get("action");
				Consumer<Tuple<Entity, Entity>> bientityAction = data.get("bientity_action");
				Predicate<Tuple<Entity, Entity>> cond = data.get("bientity_condition");
				if (!entity.isPassenger() || (entityAction == null && bientityAction == null)) {
					return;
				}
				if (data.getBoolean("recursive")) {
					Entity vehicle = entity.getVehicle();
					while (vehicle != null) {
						if (cond == null || cond.test(new Tuple<>(entity, vehicle))) {
							if (entityAction != null) {
								entityAction.accept(vehicle);
							}
							if (bientityAction != null) {
								bientityAction.accept(new Tuple<>(entity, vehicle));
							}
						}
						vehicle = vehicle.getVehicle();
					}
				} else {
					Entity vehicle = entity.getVehicle();
					if (cond == null || cond.test(new Tuple<>(entity, vehicle))) {
						if (entityAction != null) {
							entityAction.accept(vehicle);
						}
						if (bientityAction != null) {
							bientityAction.accept(new Tuple<>(entity, vehicle));
						}
					}
				}
			}
		);
	}
}
