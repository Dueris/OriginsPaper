package io.github.dueris.originspaper.action.type.block;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.ResourceOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ModifyBlockStateActionType {

	public static void action(@NotNull Level world, BlockPos pos, String propertyName, ResourceOperation operation, @Nullable Integer change, @Nullable Boolean value, @Nullable String enumName, boolean cycle) {

		BlockState blockState = world.getBlockState(pos);
		Property<?> property = blockState.getProperties()
			.stream()
			.filter(prop -> prop.getName().equals(propertyName))
			.findFirst()
			.orElse(null);

		if (property == null) {
			return;
		}

		if (cycle) {
			world.setBlockAndUpdate(pos, blockState.cycle(property));
			return;
		}

		switch (property) {
			case EnumProperty<?> enumProp when enumName != null && !enumName.isEmpty() ->
				setEnumProperty(enumProp, enumName, world, pos, blockState);
			case BooleanProperty boolProp when value != null ->
				world.setBlockAndUpdate(pos, blockState.setValue(boolProp, value));
			case IntegerProperty intProp when change != null -> {

				int newValue = switch (operation) {
					case ADD -> Optional.of(blockState.getValue(intProp)).orElse(0) + change;
					case SET -> change;
				};

				if (intProp.getPossibleValues().contains(newValue)) {
					world.setBlockAndUpdate(pos, blockState.setValue(intProp, newValue));
				}

			}
			default -> {

			}
		}

	}

	private static <T extends Enum<T> & StringRepresentable> void setEnumProperty(@NotNull EnumProperty<T> property, String name, Level world, BlockPos pos, BlockState originalState) {
		property.getValue(name).ifPresentOrElse(
			propValue -> world.setBlockAndUpdate(pos, originalState.setValue(property, propValue)),
			() -> OriginsPaper.LOGGER.warn("Couldn't set enum property \"{}\" of block at {} to \"{}\"! Expected value to be any of {}", property.getName(), pos.toShortString(), name, String.join(", ", property.getPossibleValues().stream().map(StringRepresentable::getSerializedName).toList()))
		);
	}

	public static @NotNull ActionTypeFactory<Triple<Level, BlockPos, Direction>> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("modify_block_state"),
			new SerializableData()
				.add("property", SerializableDataTypes.STRING)
				.add("operation", ApoliDataTypes.RESOURCE_OPERATION, ResourceOperation.ADD)
				.add("change", SerializableDataTypes.INT, null)
				.add("value", SerializableDataTypes.BOOLEAN, null)
				.add("enum", SerializableDataTypes.STRING, null)
				.add("cycle", SerializableDataTypes.BOOLEAN, false),
			(data, block) -> action(block.getLeft(), block.getMiddle(),
				data.get("property"),
				data.get("operation"),
				data.get("change"),
				data.get("value"),
				data.get("enum"),
				data.get("cycle")
			)
		);
	}
}
