package io.github.dueris.originspaper.condition.factory;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.Comparison;
import io.github.dueris.originspaper.data.types.Shape;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.phys.Vec3;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.function.Consumer;

public class DistanceFromCoordinatesConditionRegistry {
	private static final LinkedList<Object> previousWarnings = new LinkedList<>();

	public DistanceFromCoordinatesConditionRegistry() {
	}

	private static void warnOnce(String warning, Object key) {
		if (!previousWarnings.contains(key)) {
			previousWarnings.add(key);
			OriginsPaper.LOGGER.warn(warning);
		}

	}

	private static void warnOnce(String warning) {
		warnOnce(warning, warning);
	}

	private static <T> T warnCouldNotGetObject(String object, String from, T assumption) {
		warnOnce("Could not retrieve " + object + " from " + from + " for distance_from_spawn condition, assuming " + assumption + " for condition.");
		return assumption;
	}

	private static String[] getAliases() {
		return new String[]{"distance_from_spawn", "distance_from_coordinates"};
	}

	private static SerializableData getSerializableData(String alias) {
		return new SerializableData()
			.add("reference", SerializableDataTypes.STRING, alias.equals("distance_from_coordinates") ? "world_origin" : "world_spawn")
			.add("offset", SerializableDataTypes.VECTOR, new Vec3(0.0, 0.0, 0.0))
			.add("coordinates", SerializableDataTypes.VECTOR, new Vec3(0.0, 0.0, 0.0))
			.add("ignore_x", SerializableDataTypes.BOOLEAN, false)
			.add("ignore_y", SerializableDataTypes.BOOLEAN, false)
			.add("ignore_z", SerializableDataTypes.BOOLEAN, false)
			.add("shape", SerializableDataTypes.enumValue(Shape.class), Shape.CUBE)
			.add("scale_reference_to_dimension", SerializableDataTypes.BOOLEAN, true)
			.add("scale_distance_to_dimension", SerializableDataTypes.BOOLEAN, false)
			.add("comparison", ApoliDataTypes.COMPARISON)
			.add("compare_to", SerializableDataTypes.DOUBLE)
			.add("result_on_wrong_dimension", SerializableDataTypes.BOOLEAN, null)
			.add("round_to_digit", SerializableDataTypes.INT, null);
	}

	private static boolean compareOutOfBounds(Comparison comparison) {
		return comparison == Comparison.NOT_EQUAL || comparison == Comparison.GREATER_THAN || comparison == Comparison.GREATER_THAN_OR_EQUAL;
	}

	private static boolean testCondition(SerializableData.Instance data, BlockInWorld block, Entity entity) {
		boolean scaleReferenceToDimension = data.getBoolean("scale_reference_to_dimension");
		boolean setResultOnWrongDimension = data.isPresent("result_on_wrong_dimension");
		boolean resultOnWrongDimension = setResultOnWrongDimension && data.getBoolean("result_on_wrong_dimension");
		double x = 0.0;
		double y = 0.0;
		double z = 0.0;
		Vec3 pos;
		Level world;
		if (block != null) {
			BlockPos blockPos = block.getPos();
			pos = new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ());
			LevelReader worldView = block.getLevel();
			if (!(worldView instanceof Level)) {
				return warnCouldNotGetObject("world", "block", compareOutOfBounds(data.get("comparison")));
			}

			world = (Level) worldView;
		} else {
			pos = entity.position();
			world = entity.getCommandSenderWorld();
		}

		double currentDimensionCoordinateScale = world.dimensionType().coordinateScale();
		switch (data.getString("reference")) {
			case "player_spawn":
			case "player_natural_spawn":
				if (entity instanceof Player) {
					warnOnce("Used reference '" + data.getString("reference") + "' which is not implemented yet, defaulting to world spawn.");
				}

				if (entity == null) {
					warnOnce("Used entity-condition-only reference point in block condition, defaulting to world spawn.");
				}
			case "world_spawn":
				if (setResultOnWrongDimension && world.dimension() != Level.OVERWORLD) {
					return resultOnWrongDimension;
				}

				BlockPos spawnPos = world.getSharedSpawnPos();

				x = spawnPos.getX();
				y = spawnPos.getY();
				z = spawnPos.getZ();
			case "world_origin":
		}

		Vec3 coords = data.get("coordinates");
		Vec3 offset = data.get("offset");
		x += coords.x + offset.x;
		y += coords.y + offset.y;
		z += coords.z + offset.z;
		if (scaleReferenceToDimension && (x != 0.0 || z != 0.0)) {
			if (currentDimensionCoordinateScale == 0.0) {
				return compareOutOfBounds(data.get("comparison"));
			}

			x /= currentDimensionCoordinateScale;
			z /= currentDimensionCoordinateScale;
		}

		double xDistance = data.getBoolean("ignore_x") ? 0.0 : Math.abs(pos.x() - x);
		double yDistance = data.getBoolean("ignore_y") ? 0.0 : Math.abs(pos.y() - y);
		double zDistance = data.getBoolean("ignore_z") ? 0.0 : Math.abs(pos.z() - z);
		if (data.getBoolean("scale_distance_to_dimension")) {
			xDistance *= currentDimensionCoordinateScale;
			zDistance *= currentDimensionCoordinateScale;
		}

		double distance = Shape.getDistance(data.get("shape"), xDistance, yDistance, zDistance);
		if (data.isPresent("round_to_digit")) {
			distance = (new BigDecimal(distance)).setScale(data.getInt("round_to_digit"), RoundingMode.HALF_UP).doubleValue();
		}

		return ((Comparison) data.get("comparison")).compare(distance, data.getDouble("compare_to"));
	}

	public static void registerBlockCondition(Consumer<ConditionTypeFactory<BlockInWorld>> registryFunction) {
		String[] var1 = getAliases();
		int var2 = var1.length;

		for (String alias : var1) {
			registryFunction.accept(new ConditionTypeFactory<>(OriginsPaper.apoliIdentifier(alias), getSerializableData(alias), (data, block) -> {
				return testCondition(data, block, null);
			}));
		}

	}

	public static void registerEntityCondition(Consumer<ConditionTypeFactory<Entity>> registryFunction) {
		String[] var1 = getAliases();
		int var2 = var1.length;

		for (String alias : var1) {
			registryFunction.accept(new ConditionTypeFactory<>(OriginsPaper.apoliIdentifier(alias), getSerializableData(alias), (data, entity) -> {
				return testCondition(data, null, entity);
			}));
		}

	}
}
