package io.github.dueris.originspaper.action.types.bientity;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionTypeFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class SetInLoveAction {

	public static void action(SerializableData.Instance data, @NotNull Tuple<Entity, Entity> actorAndTarget) {

		if (actorAndTarget.getB() instanceof Animal targetAnimal && actorAndTarget.getA() instanceof Player actorPlayer) {
			targetAnimal.setInLove(actorPlayer);
		}

	}

	public static @NotNull ActionTypeFactory<Tuple<Entity, Entity>> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("set_in_love"),
			SerializableData.serializableData(),
			SetInLoveAction::action
		);
	}
}
