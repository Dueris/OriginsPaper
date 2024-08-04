package me.dueris.originspaper.factory.condition.types.entity;

import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.data.ApoliDataTypes;
import me.dueris.originspaper.factory.condition.ConditionFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class RidingRootCondition {

	public static @NotNull ConditionFactory<Entity> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("riding_root"),
			InstanceDefiner.instanceDefiner()
				.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null),
			(data, entity) -> {
				if (entity.isPassenger()) {
					if (data.isPresent("bientity_condition")) {
						Predicate<Tuple<Entity, Entity>> condition = data.get("bientity_condition");
						Entity vehicle = entity.getRootVehicle();
						return condition.test(new Tuple<>(entity, vehicle));
					}
					return true;
				}
				return false;
			}
		);
	}
}
