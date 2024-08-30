package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.factory.PowerReference;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class PowerActiveConditionType {

	public static boolean condition(Entity entity, @NotNull PowerReference powerReference) {
		return powerReference.getType() != null && powerReference.getType().isActive(entity);
	}

	public static @NotNull ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("power_active"),
			new SerializableData()
				.add("power", ApoliDataTypes.POWER_REFERENCE),
			(data, entity) -> condition(entity,
				data.get("power")
			)
		);
	}

}
