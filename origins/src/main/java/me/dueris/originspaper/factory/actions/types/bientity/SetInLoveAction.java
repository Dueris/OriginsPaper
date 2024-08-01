package me.dueris.originspaper.factory.actions.types.bientity;

import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.actions.ActionFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;

public class SetInLoveAction {

	public static void action(DeserializedFactoryJson data, Tuple<Entity, Entity> actorAndTarget) {

		if (actorAndTarget.getB() instanceof Animal targetAnimal && actorAndTarget.getA() instanceof Player actorPlayer) {
			targetAnimal.setInLove(actorPlayer);
		}

	}

	public static ActionFactory<Tuple<Entity, Entity>> getFactory() {
		return new ActionFactory<>(
			OriginsPaper.apoliIdentifier("set_in_love"),
			InstanceDefiner.instanceDefiner(),
			SetInLoveAction::action
		);
	}
}
