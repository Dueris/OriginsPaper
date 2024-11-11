package io.github.dueris.originspaper.condition.type.fluid;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.FluidConditionType;
import io.github.dueris.originspaper.condition.type.FluidConditionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

public class InTagFluidConditionType extends FluidConditionType {

    public static final TypedDataObjectFactory<InTagFluidConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("tag", SerializableDataTypes.FLUID_TAG),
        data -> new InTagFluidConditionType(
            data.get("tag")
        ),
        (conditionType, serializableData) -> serializableData.instance()
            .set("tag", conditionType.tag)
    );

    private final TagKey<Fluid> tag;

    public InTagFluidConditionType(TagKey<Fluid> tag) {
        this.tag = tag;
    }

    @Override
    public boolean test(FluidState fluidState) {
        return fluidState.is(tag);
    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return FluidConditionTypes.IN_TAG;
    }

}
