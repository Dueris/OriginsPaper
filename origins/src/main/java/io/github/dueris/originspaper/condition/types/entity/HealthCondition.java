package io.github.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.Comparison;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class HealthCondition {

	public static @NotNull ConditionFactory<Entity> getFactory() {
		return new ConditionFactory<>(
				OriginsPaper.apoliIdentifier("health"),
				SerializableData.serializableData()
						.add("comparison", ApoliDataTypes.COMPARISON)
						.add("compare_to", SerializableDataTypes.FLOAT),
				(data, entity) -> {
					return ((Comparison) data.get("comparison")).compare(entity instanceof LivingEntity ? ((LivingEntity) entity).getHealth() : 0f, data.getFloat("compare_to"));
				}
		);
	}
}
