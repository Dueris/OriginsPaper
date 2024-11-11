package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.util.Comparison;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class AttributeEntityConditionType extends EntityConditionType {

    public static final TypedDataObjectFactory<AttributeEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("attribute", SerializableDataTypes.ATTRIBUTE_ENTRY)
            .add("comparison", ApoliDataTypes.COMPARISON)
            .add("compare_to", SerializableDataTypes.DOUBLE),
        data -> new AttributeEntityConditionType(
            data.get("attribute"),
            data.get("comparison"),
            data.get("compare_to")
        ),
        (conditionType, serializableData) -> serializableData.instance()
            .set("attribute", conditionType.attribute)
            .set("comparison", conditionType.comparison)
            .set("compare_to", conditionType.compareTo)
    );

    private final Holder<Attribute> attribute;

    private final Comparison comparison;
    private final double compareTo;

    public AttributeEntityConditionType(Holder<Attribute> attribute, Comparison comparison, double compareTo) {
        this.attribute = attribute;
        this.comparison = comparison;
        this.compareTo = compareTo;
    }

    @Override
    public boolean test(Entity entity) {

        if (entity instanceof LivingEntity livingEntity) {
            return Optional.ofNullable(livingEntity.getAttribute(attribute))
                .map(AttributeInstance::getValue)
                .map(value -> comparison.compare(value, compareTo))
                .orElse(false);
        }

        else {
            return false;
        }

    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return EntityConditionTypes.ATTRIBUTE;
    }

}
