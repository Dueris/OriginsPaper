package io.github.dueris.originspaper.action.types.bientity;

import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class SetInLoveAction {

	public static void action(DeserializedFactoryJson data, @NotNull Tuple<Entity, Entity> actorAndTarget) {

		if (actorAndTarget.getB() instanceof Animal targetAnimal && actorAndTarget.getA() instanceof Player actorPlayer) {
			targetAnimal.setInLove(actorPlayer);
		}

	}

	public static @NotNull ActionFactory<Tuple<Entity, Entity>> getFactory() {
		return new ActionFactory<>(
			OriginsPaper.apoliIdentifier("set_in_love"),
			InstanceDefiner.instanceDefiner(),
			SetInLoveAction::action
		);
	}
}
