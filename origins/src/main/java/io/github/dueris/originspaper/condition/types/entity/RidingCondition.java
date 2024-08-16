package io.github.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class RidingCondition {

	public static @NotNull ConditionFactory<Entity> getFactory() {
		return new ConditionFactory<>(
				OriginsPaper.apoliIdentifier("riding"),
				SerializableData.serializableData()
						.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null),
				(data, entity) -> {
					if (entity.isPassenger()) {
						if (data.isPresent("bientity_condition")) {
							Predicate<Tuple<Entity, Entity>> condition = data.get("bientity_condition");
							Entity vehicle = entity.getVehicle();
							return condition.test(new Tuple<>(entity, vehicle));
						}
						return true;
					}
					return false;
				}
		);
	}
}
