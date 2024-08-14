package io.github.dueris.originspaper.action.types.entity;

import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class DismountAction {

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(OriginsPaper.apoliIdentifier("dismount"),
			InstanceDefiner.instanceDefiner(),
			(data, entity) -> entity.stopRiding());
	}
}
