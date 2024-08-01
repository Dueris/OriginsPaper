package me.dueris.originspaper.factory.condition.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.condition.ConditionFactory;
import me.dueris.originspaper.factory.condition.types.EntityConditions;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class MovingCondition {

	public static boolean condition(@NotNull DeserializedFactoryJson data, Entity entity) {
		return (data.getBoolean("horizontally") && EntityConditions.isEntityMovingHorizontal(entity.getBukkitEntity()))
			|| (data.getBoolean("vertically") && EntityConditions.isEntityMovingVertical(entity.getBukkitEntity()));
	}

	public static @NotNull ConditionFactory<Entity> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("moving"),
			InstanceDefiner.instanceDefiner()
				.add("horizontally", SerializableDataTypes.BOOLEAN, true)
				.add("vertically", SerializableDataTypes.BOOLEAN, true),
			MovingCondition::condition
		);
	}
}
