package io.github.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class InRainCondition {

	public static @NotNull ConditionFactory<Entity> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("in_rain"),
			InstanceDefiner.instanceDefiner(),
			(data, entity) -> {
				return entity.getBukkitEntity().isInRain();
			}
		);
	}
}
