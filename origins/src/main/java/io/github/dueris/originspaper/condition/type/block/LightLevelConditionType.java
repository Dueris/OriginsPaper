package io.github.dueris.originspaper.condition.type.block;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.Comparison;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LightLevelConditionType {

	public static boolean condition(@NotNull BlockInWorld cachedBlock, @Nullable LightLayer lightType, Comparison comparison, int compareTo) {

		Level world = (Level) cachedBlock.getLevel();
		BlockPos pos = cachedBlock.getPos();

		int lightLevel;

		if (lightType != null) {
			lightLevel = world.getBrightness(lightType, pos);
		} else {

			if (world.isClientSide) {
				world.updateSkyBrightness();
			}

			lightLevel = world.getMaxLocalRawBrightness(pos);

		}

		return comparison.compare(lightLevel, compareTo);

	}

	public static @NotNull ConditionTypeFactory<BlockInWorld> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("light_level"),
			new SerializableData()
				.add("light_type", SerializableDataTypes.enumValue(LightLayer.class), null)
				.add("comparison", ApoliDataTypes.COMPARISON)
				.add("compare_to", SerializableDataTypes.INT),
			(data, cachedBlock) -> condition(cachedBlock,
				data.get("light_type"),
				data.get("comparison"),
				data.get("compare_to")
			)
		);
	}

}
