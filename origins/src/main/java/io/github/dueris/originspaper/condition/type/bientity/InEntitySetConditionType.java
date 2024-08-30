package io.github.dueris.originspaper.condition.type.bientity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.factory.PowerReference;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

// TODO - EntitySets - Dueris
public class InEntitySetConditionType {

	public static boolean condition(Entity actor, Entity target, PowerReference power) {
		return false;
	}

	public static @NotNull ConditionTypeFactory<Tuple<Entity, Entity>> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("in_entity_set"),
			new SerializableData()
				.add("set", ApoliDataTypes.POWER_REFERENCE),
			(data, actorAndTarget) -> condition(actorAndTarget.getA(), actorAndTarget.getB(),
				data.get("set")
			)
		);
	}

}
