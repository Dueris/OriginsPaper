package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class RidingConditionType {

	public static boolean condition(@NotNull Entity entity, Predicate<Tuple<Entity, Entity>> biEntityCondition) {
		Entity vehicle = entity.getVehicle();
		return vehicle != null
			&& biEntityCondition.test(new Tuple<>(entity, vehicle));
	}

	public static @NotNull ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("riding"),
			new SerializableData()
				.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null),
			(data, entity) -> condition(entity,
				data.getOrElse("bientity_condition", actorAndTarget -> true)
			)
		);
	}

}
