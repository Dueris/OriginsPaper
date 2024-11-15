package io.github.dueris.originspaper.action.type.bientity;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.BiEntityActionType;
import io.github.dueris.originspaper.action.type.BiEntityActionTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class SetInLoveBiEntityActionType extends BiEntityActionType {

	@Override
	protected void execute(Entity actor, Entity target) {

		if (target instanceof Animal targetAnimal && actor instanceof Player actorPlayer) {
			targetAnimal.setInLove(actorPlayer);
		}

	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return BiEntityActionTypes.SET_IN_LOVE;
	}

}
