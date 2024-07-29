package me.dueris.originspaper.factory.conditions.types.entity;

import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class CollidedHorizontallyCondition {

	public static @NotNull ConditionFactory<Entity> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("collided_horizontally"),
			InstanceDefiner.instanceDefiner(),
			(data, entity) -> {
				return entity.horizontalCollision;
			}
		);
	}
}
