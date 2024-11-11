package io.github.dueris.originspaper.condition.type.block;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.FluidCondition;
import io.github.dueris.originspaper.condition.type.BlockConditionType;
import io.github.dueris.originspaper.condition.type.BlockConditionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.calio.data.SerializableData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class FluidBlockConditionType extends BlockConditionType {

    public static final TypedDataObjectFactory<FluidBlockConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("fluid_condition", FluidCondition.DATA_TYPE),
        data -> new FluidBlockConditionType(
            data.get("fluid_condition")
        ),
        (conditionType, serializableData) -> serializableData.instance()
            .set("fluid_condition", conditionType.fluidCondition)
    );

    private final FluidCondition fluidCondition;

    public FluidBlockConditionType(FluidCondition fluidCondition) {
        this.fluidCondition = fluidCondition;
    }

    @Override
    public boolean test(Level world, BlockPos pos, BlockState blockState, Optional<BlockEntity> blockEntity) {
        return fluidCondition.test(world.getFluidState(pos));
    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return BlockConditionTypes.FLUID;
    }

}
