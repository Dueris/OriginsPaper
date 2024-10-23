package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.util.Comparison;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class TimeOfDayConditionType {

	public static boolean condition(Level world, Comparison comparison, int compareTo) {
		return comparison.compare(world.getDayTime() % 24000L, compareTo);
	}

	public static ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("time_of_day"),
			new SerializableData()
				.add("comparison", ApoliDataTypes.COMPARISON)
				.add("compare_to", SerializableDataTypes.INT),
			(data, entity) -> condition(entity.level(),
				data.get("comparison"),
				data.get("compare_to")
			)
		);
	}

}
