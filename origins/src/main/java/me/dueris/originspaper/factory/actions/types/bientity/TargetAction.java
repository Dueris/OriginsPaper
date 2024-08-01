package me.dueris.originspaper.factory.actions.types.bientity;

import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.actions.ActionFactory;
import me.dueris.originspaper.factory.data.ApoliDataTypes;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;

import java.util.function.Consumer;

public class TargetAction {

	public static void action(DeserializedFactoryJson data, Tuple<Entity, Entity> actorAndTarget) {

		Entity target = actorAndTarget.getB();

		if (target != null) {
			data.<Consumer<Entity>>get("action").accept(target);
		}

	}

	public static ActionFactory<Tuple<Entity, Entity>> getFactory() {
		return new ActionFactory<>(
			OriginsPaper.apoliIdentifier("target_action"),
			InstanceDefiner.instanceDefiner()
				.add("action", ApoliDataTypes.ENTITY_ACTION),
			TargetAction::action
		);
	}
}
