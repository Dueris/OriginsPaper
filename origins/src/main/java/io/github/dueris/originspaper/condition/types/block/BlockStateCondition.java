package io.github.dueris.originspaper.condition.types.block;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.Comparison;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class BlockStateCondition {

	public static @NotNull ConditionFactory<BlockInWorld> getFactory() {
		return new ConditionFactory<>(
				OriginsPaper.apoliIdentifier("block_state"),
				SerializableData.serializableData()
						.add("property", SerializableDataTypes.STRING)
						.add("comparison", ApoliDataTypes.COMPARISON, null)
						.add("compare_to", SerializableDataTypes.INT, null)
						.add("value", SerializableDataTypes.BOOLEAN, null)
						.add("enum", SerializableDataTypes.STRING, null),
				(data, block) -> {
					BlockState state = block.getState();
					Collection<Property<?>> properties = state.getProperties();
					String desiredPropertyName = data.getString("property");
					Property<?> property = null;
					for (Property<?> p : properties) {
						if (p.getName().equals(desiredPropertyName)) {
							property = p;
							break;
						}
					}
					if (property != null) {
						Object value = state.getValue(property);
						if (data.isPresent("enum") && value instanceof Enum) {
							return ((Enum) value).name().equalsIgnoreCase(data.getString("enum"));
						} else if (data.isPresent("value") && value instanceof Boolean) {
							return (Boolean) value == data.getBoolean("value");
						} else if (data.isPresent("comparison") && data.isPresent("compare_to") && value instanceof Integer) {
							return ((Comparison) data.get("comparison")).compare((Integer) value, data.getInt("compare_to"));
						}
						return true;
					}
					return false;
				}
		);
	}
}
