package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.Comparison;
import io.github.dueris.originspaper.power.factory.PowerReference;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

// TODO - EntitySets - Dueris
public class EntitySetSizeConditionType {

	public static boolean condition(Entity entity, PowerReference power, @NotNull Comparison comparison, int compareTo) {

		int setSize = 0;

		return comparison.compare(setSize, compareTo);

	}

	public static @NotNull ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("entity_set_size"),
			new SerializableData()
				.add("set", ApoliDataTypes.POWER_REFERENCE)
				.add("comparison", ApoliDataTypes.COMPARISON)
				.add("compare_to", SerializableDataTypes.INT),
			(data, entity) -> condition(entity,
				data.get("set"),
				data.get("comparison"),
				data.get("compare_to")
			)
		);
	}

}
