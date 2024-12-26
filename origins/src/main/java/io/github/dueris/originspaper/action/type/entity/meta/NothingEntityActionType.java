package io.github.dueris.originspaper.action.type.entity.meta;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.action.type.meta.NothingMetaActionType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class NothingEntityActionType extends EntityActionType implements NothingMetaActionType {

	@Override
	protected void execute(Entity entity) {

	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return EntityActionTypes.NOTHING;
	}

}
