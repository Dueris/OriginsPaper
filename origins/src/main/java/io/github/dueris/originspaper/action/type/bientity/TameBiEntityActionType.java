package io.github.dueris.originspaper.action.type.bientity;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.BiEntityActionType;
import io.github.dueris.originspaper.action.type.BiEntityActionTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class TameBiEntityActionType extends BiEntityActionType {

    @Override
	protected void execute(Entity actor, Entity target) {

        if (actor instanceof Player actorPlayer) {

            if (target instanceof TamableAnimal tameableTarget && !tameableTarget.isTame()) {
                tameableTarget.tame(actorPlayer);
            }

            else if (target instanceof AbstractHorse targetHorseLike && !targetHorseLike.isTamed()) {
                targetHorseLike.tameWithName(actorPlayer);
            }

        }

    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return BiEntityActionTypes.TAME;
    }

}
