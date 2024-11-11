package io.github.dueris.originspaper.action.type.bientity;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.BiEntityActionType;
import io.github.dueris.originspaper.action.type.BiEntityActionTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import org.jetbrains.annotations.NotNull;

public class LeashBiEntityActionType extends BiEntityActionType {

    @Override
	protected void execute(Entity actor, Entity target) {

        if (actor == null || !(target instanceof Leashable leashable)) {
            return;
        }

        if (!leashable.isLeashed()) {
            leashable.setLeashedTo(actor, true);
        }

    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return BiEntityActionTypes.LEASH;
    }

}
