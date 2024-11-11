package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerReference;
import io.github.dueris.calio.data.SerializableData;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class PowerActiveEntityConditionType extends EntityConditionType {

    public static final TypedDataObjectFactory<PowerActiveEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("power", ApoliDataTypes.POWER_REFERENCE),
        data -> new PowerActiveEntityConditionType(
            data.get("power")
        ),
        (conditionType, serializableData) -> serializableData.instance()
            .set("power", conditionType.power)
    );

    private final PowerReference power;

    public PowerActiveEntityConditionType(PowerReference power) {
        this.power = power;
    }

    @Override
    public boolean test(Entity entity) {
        return power.isActive(entity);
    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return EntityConditionTypes.POWER_ACTIVE;
    }

}
