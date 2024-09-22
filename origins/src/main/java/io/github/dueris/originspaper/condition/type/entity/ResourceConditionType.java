package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.Comparison;
import io.github.dueris.originspaper.power.factory.PowerReference;
import io.github.dueris.originspaper.power.type.CooldownPower;
import io.github.dueris.originspaper.power.type.ResourcePower;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class ResourceConditionType {

	public static boolean condition(Entity entity, PowerReference power, @NotNull Comparison comparison, int compareTo) {
		return comparison.compare(getResourceValue(entity, power), compareTo);
	}

	private static int getResourceValue(Entity entity, PowerReference power) {
		return switch (power.getType()) {
			case ResourcePower varInt -> varInt.getValue(entity);
			case CooldownPower cooldown -> cooldown.getRemainingTicks(entity);
			case null, default -> 0;
		};
	}

	public static @NotNull ConditionTypeFactory<Entity> getFactory() {
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
