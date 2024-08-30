package io.github.dueris.originspaper.action.type.bientity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;

public class SetInLoveActionType {

	public static void action(Entity actor, Entity target) {

		if (target instanceof Animal targetAnimal && actor instanceof Player actorPlayer) {
			targetAnimal.setInLove(actorPlayer);
		}

	}

}
