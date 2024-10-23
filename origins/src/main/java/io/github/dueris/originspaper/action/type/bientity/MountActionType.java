package io.github.dueris.originspaper.action.type.bientity;

import net.minecraft.world.entity.Entity;

public class MountActionType {

	public static void action(Entity actor, Entity target) {

		if (actor == null || target == null) {
			return;
		}

		target.getBukkitEntity().setPassenger(actor.getBukkitEntity());

	}

}
