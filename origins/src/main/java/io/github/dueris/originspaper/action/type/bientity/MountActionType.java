package io.github.dueris.originspaper.action.type.bientity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class MountActionType {

	public static void action(Entity actor, Entity target) {

		if (actor == null || target == null) {
			return;
		}

		actor.startRiding(target, true);
		if (!actor.level().isClientSide && target instanceof ServerPlayer targetPlayer) {
			target.getBukkitEntity().setPassenger(targetPlayer.getBukkitEntity());
		}

	}

}
