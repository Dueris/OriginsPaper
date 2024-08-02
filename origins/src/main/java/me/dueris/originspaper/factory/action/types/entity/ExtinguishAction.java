package me.dueris.originspaper.factory.action.types.entity;

import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.action.ActionFactory;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class ExtinguishAction {

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(OriginsPaper.identifier("extinguish"),
			InstanceDefiner.instanceDefiner(),
			(data, entity) -> entity.clearFire());
	}
}
