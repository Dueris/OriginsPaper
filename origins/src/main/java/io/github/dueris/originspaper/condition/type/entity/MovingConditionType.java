package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import net.minecraft.world.entity.Entity;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class MovingConditionType {
	private static Location[] prevLoca;

	public static boolean condition(Entity entity, boolean horizontally, boolean vertically) {
		return (horizontally && isEntityMovingHorizontal(entity))
			|| (vertically && isEntityMovingVertical(entity));
	}

	public static ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("moving"),
			new SerializableData()
				.add("horizontally", SerializableDataTypes.BOOLEAN, true)
				.add("vertically", SerializableDataTypes.BOOLEAN, true),
			(data, entity) -> condition(entity,
				data.get("horizontally"),
				data.get("vertically")
			)
		);
	}

	public static boolean isEntityMovingHorizontal(@NotNull Entity entity) {
		int entID = entity.getBukkitEntity().getEntityId();
		Location prevLocat = prevLoca[entID];
		Location cuLo = entity.getBukkitEntity().getLocation().clone();
		prevLoca[entID] = cuLo;
		if (prevLocat == null) return true;

		return cuLo.getX() != prevLocat.getX() || prevLocat.getZ() != cuLo.getZ();
	}

	public static boolean isEntityMovingVertical(@NotNull Entity entity) {
		int entID = entity.getBukkitEntity().getEntityId();
		Location prevLocat = prevLoca[entID];
		Location cuLo = entity.getBukkitEntity().getLocation().clone();
		prevLoca[entID] = cuLo;
		if (prevLocat == null) return true;

		return cuLo.getY() != prevLocat.getY();
	}

}
