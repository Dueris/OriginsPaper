package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.power.type.PowerType;
import io.github.dueris.originspaper.power.type.PowerTypes;
import io.github.dueris.calio.data.SerializableData;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class PowerTypeEntityConditionType extends EntityConditionType {

    public static final TypedDataObjectFactory<PowerTypeEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("power_type", PowerTypes.DATA_TYPE),
        data -> new PowerTypeEntityConditionType(
            data.get("power_type")
        ),
        (conditionType, serializableData) -> serializableData.instance()
            .set("power_type", conditionType.powerType)
    );

    private final PowerConfiguration<PowerType> powerType;

    public PowerTypeEntityConditionType(PowerConfiguration<PowerType> powerType) {
        this.powerType = powerType;
    }

    @Override
    public boolean test(Entity entity) {
        return PowerHolderComponent.getOptional(entity)
            .stream()
            .map(PowerHolderComponent::getPowerTypes)
            .flatMap(Collection::stream)
            .map(PowerType::getConfig)
            .anyMatch(powerType::equals);
    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return EntityConditionTypes.POWER_TYPE;
    }

}
