package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.originspaper.access.SubmergableEntity;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

public class SubmergedInEntityConditionType extends EntityConditionType {

    public static final TypedDataObjectFactory<SubmergedInEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("fluid", SerializableDataTypes.FLUID_TAG),
        data -> new SubmergedInEntityConditionType(
            data.get("fluid")
        ),
        (conditionType, serializableData) -> serializableData.instance()
            .set("fluid", conditionType.fluid)
    );

    private final TagKey<Fluid> fluid;

    public SubmergedInEntityConditionType(TagKey<Fluid> fluid) {
        this.fluid = fluid;
    }

    @Override
    public boolean test(Entity entity) {
        return entity instanceof SubmergableEntity submergableEntity
            && submergableEntity.apoli$isSubmergedInLoosely(fluid);
    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return EntityConditionTypes.SUBMERGED_IN;
    }

}
