package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class ShowToastEntityActionType extends EntityActionType {

	// private final CustomToastData customToastData;

    /* public ShowToastEntityActionType(CustomToastData customToastData) {
        this.customToastData = customToastData;
    } */

	@Override
	protected void execute(Entity entity) {

        /* if (!entity.level().isClientSide() && entity instanceof CustomToastViewer viewer) {
            viewer.apoli$showToast(customToastData);
        } */ // TODO

	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return EntityActionTypes.SHOW_TOAST;
	}

}
