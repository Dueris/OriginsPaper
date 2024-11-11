package io.github.dueris.originspaper.condition.type.fluid;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.FluidConditionType;
import io.github.dueris.originspaper.condition.type.FluidConditionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

public class FluidFluidConditionType extends FluidConditionType {

    public static final TypedDataObjectFactory<FluidFluidConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("fluid", SerializableDataTypes.FLUID),
        data -> new FluidFluidConditionType(
            data.get("fluid")
        ),
        (conditionType, serializableData) -> serializableData.instance()
            .set("fluid", conditionType.fluid)
    );

    private final Fluid fluid;

    public FluidFluidConditionType(Fluid fluid) {
        this.fluid = fluid;
    }

    @Override
    public boolean test(FluidState fluidState) {
        return fluidState.is(fluid);
    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return FluidConditionTypes.FLUID;
    }

}
