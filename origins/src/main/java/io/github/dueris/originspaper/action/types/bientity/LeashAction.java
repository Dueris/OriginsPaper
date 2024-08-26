package io.github.dueris.originspaper.action.types.bientity;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionTypeFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.NotNull;

public class LeashAction {

	public static void action(SerializableData.Instance data, @NotNull Tuple<Entity, Entity> actorAndTarget) {

		Entity actor = actorAndTarget.getA();
		Entity target = actorAndTarget.getB();

		if (actor == null || !(target instanceof Mob mobTarget)) {
			return;
		}

		if (!mobTarget.isLeashed()) {
			mobTarget.setLeashedTo(actor, true);
		}

	}

	public static @NotNull ActionTypeFactory<Tuple<Entity, Entity>> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("leash"),
			SerializableData.serializableData(),
			LeashAction::action
		);
	}
}
