package me.dueris.originspaper.factory.actions.types.bientity;

import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

public class LeashAction {

	public static void action(DeserializedFactoryJson data, Tuple<Entity, Entity> actorAndTarget) {

		Entity actor = actorAndTarget.getA();
		Entity target = actorAndTarget.getB();

		if (actor == null || !(target instanceof Mob mobTarget)) {
			return;
		}

		if (!mobTarget.isLeashed()) {
			mobTarget.setLeashedTo(actor, true);
		}

	}

	public static ActionFactory<Tuple<Entity, Entity>> getFactory() {
		return new ActionFactory<>(
			Apoli.identifier("leash"),
			new SerializableData(),
			LeashAction::action
		);
	}
}
