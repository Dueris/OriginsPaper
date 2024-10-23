package io.github.dueris.originspaper.action.type.bientity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;

import java.util.function.Consumer;

public class InvertActionType {

	public static void action(Entity actor, Entity target, Consumer<Tuple<Entity, Entity>> biEntityAction) {
		biEntityAction.accept(new Tuple<>(target, actor));
	}

	public static ActionTypeFactory<Tuple<Entity, Entity>> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("invert"),
			new SerializableData()
				.add("action", ApoliDataTypes.BIENTITY_ACTION),
			(data, actorAndTarget) -> action(actorAndTarget.getA(), actorAndTarget.getB(),
				data.get("action")
			)
		);
	}

}
