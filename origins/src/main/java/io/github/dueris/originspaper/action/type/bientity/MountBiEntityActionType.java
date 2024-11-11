package io.github.dueris.originspaper.action.type.bientity;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.BiEntityActionType;
import io.github.dueris.originspaper.action.type.BiEntityActionTypes;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class MountBiEntityActionType extends BiEntityActionType {

    @Override
	protected void execute(Entity actor, Entity target) {

        if (actor == null || target == null) {
            return;
        }

        actor.startRiding(target, true);

    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return BiEntityActionTypes.MOUNT;
    }

}
