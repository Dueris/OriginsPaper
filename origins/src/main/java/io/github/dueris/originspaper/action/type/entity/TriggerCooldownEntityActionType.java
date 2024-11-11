package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerReference;
import io.github.dueris.originspaper.power.type.CooldownPowerType;
import io.github.dueris.calio.data.SerializableData;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class TriggerCooldownEntityActionType extends EntityActionType {

    public static final TypedDataObjectFactory<TriggerCooldownEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("power", ApoliDataTypes.POWER_REFERENCE),
        data -> new TriggerCooldownEntityActionType(
            data.get("power")
        ),
        (actionType, serializableData) -> serializableData.instance()
            .set("power", actionType.power)
    );

    private final PowerReference power;

    public TriggerCooldownEntityActionType(PowerReference power) {
        this.power = power;
    }

    @Override
    protected void execute(Entity entity) {

        if (power.getPowerTypeFrom(entity) instanceof CooldownPowerType cooldownPowerType) {
            cooldownPowerType.use();
        }

    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return EntityActionTypes.TRIGGER_COOLDOWN;
    }

}
