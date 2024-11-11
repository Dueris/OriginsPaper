package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerReference;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PowerEntityConditionType extends EntityConditionType {

    public static final TypedDataObjectFactory<PowerEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("power", ApoliDataTypes.POWER_REFERENCE)
            .add("source", SerializableDataTypes.IDENTIFIER.optional(), Optional.empty()),
        data -> new PowerEntityConditionType(
            data.get("power"),
            data.get("source")
        ),
        (conditionType, serializableData) -> serializableData.instance()
            .set("power", conditionType.power)
            .set("source", conditionType.source)
    );

    private final PowerReference power;
    private final Optional<ResourceLocation> source;

    public PowerEntityConditionType(PowerReference power, Optional<ResourceLocation> source) {
        this.power = power;
        this.source = source;
    }

    @Override
    public boolean test(Entity entity) {
        return PowerHolderComponent.getOptional(entity)
            .map(component -> source
                .map(id -> component.hasPower(power, id))
                .orElseGet(() -> component.hasPower(power)))
            .orElse(false);
    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return EntityConditionTypes.POWER;
    }

}
