package io.github.dueris.originspaper.action.type.bientity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;

public class TameActionType {

	public static void action(Entity actor, Entity target) {

		if (!(actor instanceof Player actorPlayer)) {
			return;
		}

		if (target instanceof TamableAnimal tameableTarget && !tameableTarget.isTame()) {
			tameableTarget.tame(actorPlayer);
		} else if (target instanceof AbstractHorse targetHorseLike && !targetHorseLike.isTamed()) {
			targetHorseLike.tameWithName(actorPlayer);
		}

	}

}
