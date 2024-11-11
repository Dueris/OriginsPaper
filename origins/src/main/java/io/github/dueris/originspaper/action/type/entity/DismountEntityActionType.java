package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class DismountEntityActionType extends EntityActionType {

	@Override
	protected void execute(Entity entity) {
		entity.stopRiding();
	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return EntityActionTypes.DISMOUNT;
	}

}
