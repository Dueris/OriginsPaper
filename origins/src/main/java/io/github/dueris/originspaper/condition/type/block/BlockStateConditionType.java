package io.github.dueris.originspaper.condition.type.block;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.util.Comparison;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.Nullable;

public class BlockStateConditionType {

	public static boolean condition(BlockState blockState, String propertyName, @Nullable Comparison comparison, @Nullable Integer compareTo, @Nullable Boolean value, @Nullable String enumName) {

		var propertyValue = blockState.getProperties()
			.stream()
			.filter(prop -> prop.getName().equals(propertyName))
			.map(blockState::getValue)
			.findFirst()
			.orElse(null);

		return switch (propertyValue) {
			case Enum<?> enumValue when enumName != null -> enumValue.name().equalsIgnoreCase(enumName);
			case Boolean booleanValue when value != null -> booleanValue == value;
			case Integer intValue when comparison != null && compareTo != null ->
				comparison.compare(intValue, compareTo);
			case null, default -> propertyValue != null;
		};

	}

	public static ConditionTypeFactory<BlockInWorld> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("block_state"),
			new SerializableData()
				.add("property", SerializableDataTypes.STRING)
				.add("comparison", ApoliDataTypes.COMPARISON, null)
				.add("compare_to", SerializableDataTypes.INT, null)
				.add("value", SerializableDataTypes.BOOLEAN, null)
				.add("enum", SerializableDataTypes.STRING, null),
			(data, cachedBlock) -> condition(cachedBlock.getState(),
				data.get("property"),
				data.get("comparison"),
				data.get("compare_to"),
				data.get("value"),
				data.get("enum")
			)
		);
	}

}
