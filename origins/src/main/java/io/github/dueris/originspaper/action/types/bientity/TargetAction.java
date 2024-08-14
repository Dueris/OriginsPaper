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

public class TargetAction {

	public static void action(DeserializedFactoryJson data, @NotNull Tuple<Entity, Entity> actorAndTarget) {

		Entity target = actorAndTarget.getB();

		if (target != null) {
			data.<Consumer<Entity>>get("action").accept(target);
		}

	}

	public static @NotNull ActionFactory<Tuple<Entity, Entity>> getFactory() {
		return new ActionFactory<>(
			OriginsPaper.apoliIdentifier("target_action"),
			InstanceDefiner.instanceDefiner()
				.add("action", ApoliDataTypes.ENTITY_ACTION),
			TargetAction::action
		);
	}
}
