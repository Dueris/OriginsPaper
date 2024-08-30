package io.github.dueris.originspaper.action.type.bientity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;

public class LeashActionType {

	public static void action(Entity actor, Entity target) {

		if (actor == null || !(target instanceof Leashable leashable)) {
			return;
		}

		if (leashable.isLeashed()) {
			leashable.setLeashedTo(actor, true);
		}

	}

}
