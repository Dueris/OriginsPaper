package io.github.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import org.jetbrains.annotations.NotNull;

public class ExposedToSkyCondition {

	public static @NotNull ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("exposed_to_sky"),
			SerializableData.serializableData(),
			(data, entity) -> {
				BlockPos blockPos = entity.getVehicle() instanceof Boat ? (BlockPos.containing(entity.getX(), (double) Math.round(entity.getY()), entity.getZ())).above() : BlockPos.containing(entity.getX(), (double) Math.round(entity.getY()), entity.getZ());
				return entity.level().canSeeSky(blockPos);
			}
		);
	}
}
