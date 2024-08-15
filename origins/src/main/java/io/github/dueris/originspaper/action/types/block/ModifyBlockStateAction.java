package io.github.dueris.originspaper.action.types.block;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.ResourceOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public class ModifyBlockStateAction {

	public static void action(@NotNull SerializableData.Instance data, @NotNull Triple<Level, BlockPos, Direction> block) {
		BlockState state = block.getLeft().getBlockState(block.getMiddle());
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
			if (data.getBoolean("cycle")) {
				block.getLeft().setBlockAndUpdate(block.getMiddle(), state.cycle(property));
			} else {
				Object value = state.getValue(property);
				if (data.isPresent("enum") && value instanceof Enum) {
					modifyEnumState(block.getLeft(), block.getMiddle(), state, property, data.getString("enum"));
				} else if (data.isPresent("value") && value instanceof Boolean) {
					block.getLeft().setBlockAndUpdate(block.getMiddle(), state.setValue((Property<Boolean>) property, data.getBoolean("value")));
				} else if (data.isPresent("operation") && data.isPresent("change") && value instanceof Integer) {
					ResourceOperation op = data.get("operation");
					int opValue = data.getInt("change");
					int newValue = (int) value;
					switch (op) {
						case ADD -> newValue += opValue;
						case SET -> newValue = opValue;
					}
					Property<Integer> integerProperty = (Property<Integer>) property;
					if (integerProperty.getPossibleValues().contains(newValue)) {
						block.getLeft().setBlockAndUpdate(block.getMiddle(), state.setValue(integerProperty, newValue));
					}
				}
			}
		}
	}

	private static <T extends Comparable<T>> void modifyEnumState(Level world, BlockPos pos, BlockState originalState, @NotNull Property<T> property, String value) {
		Optional<T> enumValue = property.getValue(value);
		enumValue.ifPresent(v -> world.setBlockAndUpdate(pos, originalState.setValue(property, v)));
	}

	public static @NotNull ActionFactory<Triple<Level, BlockPos, Direction>> getFactory() {
		return new ActionFactory<>(OriginsPaper.apoliIdentifier("modify_block_state"),
			SerializableData.serializableData()
				.add("property", SerializableDataTypes.STRING)
				.add("operation", ApoliDataTypes.RESOURCE_OPERATION, ResourceOperation.ADD)
				.add("change", SerializableDataTypes.INT, null)
				.add("value", SerializableDataTypes.BOOLEAN, null)
				.add("enum", SerializableDataTypes.STRING, null)
				.add("cycle", SerializableDataTypes.BOOLEAN, false),
			ModifyBlockStateAction::action
		);
	}
}
