package io.github.dueris.originspaper.action.type.bientity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class TargetActionType {

	public static void action(Entity target, Consumer<Entity> action) {

		if (target != null) {
			action.accept(target);
		}

	}

	public static @NotNull ActionTypeFactory<Tuple<Entity, Entity>> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("target_action"),
			new SerializableData()
				.add("action", ApoliDataTypes.ENTITY_ACTION),
			(data, actorAndTarget) -> action(actorAndTarget.getB(),
				data.get("action")
			)
		);
	}

}
