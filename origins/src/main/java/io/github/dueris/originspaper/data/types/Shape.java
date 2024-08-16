package io.github.dueris.originspaper.data.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public enum Shape {
	CUBE,
	CHEBYSHEV,
	STAR,
	MANHATTAN,
	SPHERE,
	EUCLIDEAN;

	public static @NotNull Collection<BlockPos> getPositions(BlockPos center, Shape shape, int radius) {
		Set<BlockPos> positions = new HashSet<>();

		for (int i = -radius; i <= radius; i++) {
			for (int j = -radius; j <= radius; j++) {
				for (int k = -radius; k <= radius; k++) {
					if (shape == CUBE
							|| shape == CHEBYSHEV
							|| (shape == SPHERE || shape == EUCLIDEAN) && i * i + j * j + k * k <= radius * radius
							|| Math.abs(i) + Math.abs(j) + Math.abs(k) <= radius) {
						positions.add(new BlockPos(center.offset(i, j, k)));
					}
				}
			}
		}

		return positions;
	}

	public static void executeAtPositions(BlockPos center, Shape shape, int radius, Consumer<BlockPos> consumer) {
		for (int i = -radius; i <= radius; i++) {
			for (int j = -radius; j <= radius; j++) {
				for (int k = -radius; k <= radius; k++) {
					if (shape == CUBE
							|| shape == CHEBYSHEV
							|| (shape == SPHERE || shape == EUCLIDEAN) && i * i + j * j + k * k <= radius * radius
							|| Math.abs(i) + Math.abs(j) + Math.abs(k) <= radius) {
						consumer.accept(new BlockPos(center.offset(i, j, k)));
					}
				}
			}
		}
	}

	public static @NotNull Set<Entity> getEntities(Shape shape, @NotNull Level world, Vec3 center, double radius) {
		Set<Entity> entities = new HashSet<>();
		double diameter = radius * 2.0;

		for (Entity entity : world.getEntitiesOfClass(Entity.class, AABB.ofSize(center, diameter, diameter, diameter))) {
			double x = Math.abs(entity.getX() - center.x);
			double y = Math.abs(entity.getY() - center.y);
			double z = Math.abs(entity.getZ() - center.z);
			if (getDistance(shape, x, y, z) < radius) {
				entities.add(entity);
			}
		}

		return entities;
	}

	public static double getDistance(@NotNull Shape shape, double xDistance, double yDistance, double zDistance) {
		return switch (shape) {
			case CUBE, CHEBYSHEV -> Math.max(Math.max(xDistance, yDistance), zDistance);
			case STAR, MANHATTAN -> xDistance + yDistance + zDistance;
			case SPHERE, EUCLIDEAN -> Math.sqrt(xDistance * xDistance + yDistance * yDistance + zDistance * zDistance);
		};
	}
}
