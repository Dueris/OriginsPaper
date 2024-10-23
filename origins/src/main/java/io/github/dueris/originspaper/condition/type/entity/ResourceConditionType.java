package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.PowerReference;
import io.github.dueris.originspaper.power.type.CooldownPowerType;
import io.github.dueris.originspaper.power.type.VariableIntPowerType;
import io.github.dueris.originspaper.util.Comparison;
import net.minecraft.world.entity.Entity;

public class ResourceConditionType {

	public static boolean condition(Entity entity, PowerReference power, Comparison comparison, int compareTo) {
		return comparison.compare(getResourceValue(entity, power), compareTo);
	}

	private static int getResourceValue(Entity entity, PowerReference power) {
		return switch (power.getType(entity)) {
			case VariableIntPowerType varInt -> varInt.getValue();
			case CooldownPowerType cooldown -> cooldown.getRemainingTicks();
			case null, default -> 0;
		};
	}

	public static ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("resource"),
			new SerializableData()
				.add("resource", ApoliDataTypes.POWER_REFERENCE)
				.add("comparison", ApoliDataTypes.COMPARISON)
				.add("compare_to", SerializableDataTypes.INT),
			(data, entity) -> condition(entity,
				data.get("resource"),
				data.get("comparison"),
				data.get("compare_to")
			)
		);
	}

}
