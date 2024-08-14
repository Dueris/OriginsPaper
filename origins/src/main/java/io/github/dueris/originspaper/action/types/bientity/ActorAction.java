package io.github.dueris.originspaper.action.types.bientity;

import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ActorAction {

	public static void action(DeserializedFactoryJson data, @NotNull Tuple<Entity, Entity> actorAndTarget) {

		Entity actor = actorAndTarget.getA();

		if (actor != null) {
			data.<Consumer<Entity>>get("action").accept(actor);
		}

	}

	public static @NotNull ActionFactory<Tuple<Entity, Entity>> getFactory() {
		return new ActionFactory<>(
			OriginsPaper.apoliIdentifier("actor_action"),
			InstanceDefiner.instanceDefiner()
				.add("action", ApoliDataTypes.ENTITY_ACTION),
			ActorAction::action
		);
	}
}
