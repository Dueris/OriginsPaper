package io.github.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import org.jetbrains.annotations.NotNull;

public class ExposedToSkyCondition {

	public static @NotNull ConditionFactory<Entity> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("exposed_to_sky"),
			InstanceDefiner.instanceDefiner(),
			(data, entity) -> {
				BlockPos blockPos = entity.getVehicle() instanceof Boat ? (BlockPos.containing(entity.getX(), (double) Math.round(entity.getY()), entity.getZ())).above() : BlockPos.containing(entity.getX(), (double) Math.round(entity.getY()), entity.getZ());
				return entity.level().canSeeSky(blockPos);
			}
		);
	}
}
