package io.github.dueris.originspaper.action.type.bientity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.factory.PowerReference;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;

// TODO - EntitySets - Dueris
public class RemoveFromEntitySetActionType {

	public static void action(Entity actor, Entity target, PowerReference power) {
	}

	public static ActionTypeFactory<Tuple<Entity, Entity>> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("remove_from_entity_set"),
			new SerializableData()
				.add("set", ApoliDataTypes.POWER_REFERENCE),
			(data, actorAndTarget) -> action(actorAndTarget.getA(), actorAndTarget.getB(),
				data.get("set")
			)
		);
	}

}
