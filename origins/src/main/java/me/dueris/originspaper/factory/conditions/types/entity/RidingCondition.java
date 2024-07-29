package me.dueris.originspaper.factory.conditions.types.entity;

import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import me.dueris.originspaper.factory.data.ApoliDataTypes;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class RidingCondition {

	public static @NotNull ConditionFactory<Entity> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("riding"),
			InstanceDefiner.instanceDefiner()
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
