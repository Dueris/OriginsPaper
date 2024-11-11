package io.github.dueris.originspaper.action.type.block;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.BlockActionType;
import io.github.dueris.originspaper.action.type.BlockActionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.util.ResourceOperation;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ModifyBlockStateBlockActionType extends BlockActionType {

    public static final TypedDataObjectFactory<ModifyBlockStateBlockActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("property", SerializableDataTypes.STRING)
            .add("operation", ApoliDataTypes.RESOURCE_OPERATION, ResourceOperation.ADD)
            .add("change", SerializableDataTypes.INT.optional(), Optional.empty())
            .add("value", SerializableDataTypes.BOOLEAN.optional(), Optional.empty())
            .add("enum", SerializableDataTypes.STRING.optional(), Optional.empty())
            .add("cycle", SerializableDataTypes.BOOLEAN, false),
        data -> new ModifyBlockStateBlockActionType(
            data.get("property"),
            data.get("operation"),
            data.get("change"),
            data.get("value"),
            data.get("enum"),
            data.get("cycle")
        ),
        (actionType, serializableData) -> serializableData.instance()
            .set("property", actionType.property)
            .set("operation", actionType.operation)
            .set("change", actionType.change)
            .set("value", actionType.boolValue)
            .set("enum", actionType.enumValue)
            .set("cycle", actionType.cycle)
    );

    private final String property;

    private final ResourceOperation operation;
    private final Optional<Integer> change;

    private final Optional<Boolean> boolValue;
    private final Optional<String> enumValue;

    private final boolean cycle;

    public ModifyBlockStateBlockActionType(String property, ResourceOperation operation, Optional<Integer> change, Optional<Boolean> boolValue, Optional<String> enumValue, boolean cycle) {
        this.property = property;
        this.operation = operation;
        this.change = change;
        this.boolValue = boolValue;
        this.enumValue = enumValue;
        this.cycle = cycle;
    }

    @Override
	protected void execute(Level world, BlockPos pos, Optional<Direction> direction) {

        BlockState blockState = world.getBlockState(pos);
        Property<?> blockProperty = blockState.getProperties()
            .stream()
            .filter(prop -> prop.getName().equals(property))
            .findFirst()
            .orElse(null);

        if (blockProperty == null) {
            return;
        }

        if (cycle) {
            world.setBlockAndUpdate(pos, blockState.cycle(blockProperty));
            return;
        }

        switch (blockProperty) {
            case EnumProperty<?> enumProp when enumValue.isPresent() && !enumValue.get().isEmpty() ->
                setEnumProperty(enumProp, enumValue.get(), world, pos, blockState);
            case BooleanProperty boolProp when boolValue.isPresent() ->
                world.setBlockAndUpdate(pos, blockState.setValue(boolProp, boolValue.get()));
            case IntegerProperty intProp when change.isPresent() -> {

                int newValue = switch (operation) {
                    case ADD ->
                        Optional.of(blockState.getValue(intProp)).orElse(0) + change.get();
                    case SET ->
                        change.get();
                };

                if (intProp.getPossibleValues().contains(newValue)) {
                    world.setBlockAndUpdate(pos, blockState.setValue(intProp, newValue));
                }

            }
            default -> {

            }
        }

    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return BlockActionTypes.MODIFY_BLOCK_STATE;
    }

    private static <T extends Enum<T> & StringRepresentable> void setEnumProperty(EnumProperty<T> property, String name, Level world, BlockPos pos, BlockState originalState) {
        property.getValue(name).ifPresentOrElse(
            propValue -> world.setBlockAndUpdate(pos, originalState.setValue(property, propValue)),
            () -> OriginsPaper.LOGGER.warn("Couldn't set enum property \"{}\" of block at {} to \"{}\"! Expected value to be any of {}", property.getName(), pos.toShortString(), name, String.join(", ", property.getPossibleValues().stream().map(StringRepresentable::getSerializedName).toList()))
        );
    }

}
