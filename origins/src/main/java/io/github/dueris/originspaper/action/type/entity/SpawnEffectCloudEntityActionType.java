package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.alchemy.PotionContents;
import org.jetbrains.annotations.NotNull;

public class SpawnEffectCloudEntityActionType extends EntityActionType {

    public static final TypedDataObjectFactory<SpawnEffectCloudEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("effect_component", SerializableDataTypes.POTION_CONTENTS_COMPONENT, PotionContents.EMPTY)
            .add("wait_time", SerializableDataTypes.INT, 10)
            .add("radius", SerializableDataTypes.FLOAT, 3.0F)
            .add("radius_on_use", SerializableDataTypes.FLOAT, -0.5F)
            .add("duration", SerializableDataTypes.INT, 600)
            .add("duration_on_use", SerializableDataTypes.INT, 0),
        data -> new SpawnEffectCloudEntityActionType(
            data.get("effect_component"),
            data.get("wait_time"),
            data.get("radius"),
            data.get("radius_on_use"),
            data.get("duration"),
            data.get("duration_on_use")
        ),
        (actionType, serializableData) -> serializableData.instance()
            .set("effect_component", actionType.effectComponent)
            .set("wait_time", actionType.waitTime)
            .set("radius", actionType.radius)
            .set("radius_on_use", actionType.radiusOnUse)
            .set("duration", actionType.duration)
            .set("duration_on_use", actionType.durationOnUse)
    );

    private final PotionContents effectComponent;
    private final int waitTime;

    private final float radius;
    private final float radiusOnUse;

    private final int duration;
    private final int durationOnUse;

    public SpawnEffectCloudEntityActionType(PotionContents effectComponent, int waitTime, float radius, float radiusOnUse, int duration, int durationOnUse) {
        this.effectComponent = effectComponent;
        this.waitTime = waitTime;
        this.radius = radius;
        this.radiusOnUse = radiusOnUse;
        this.duration = duration;
        this.durationOnUse = durationOnUse;
    }

    @Override
    protected void execute(Entity entity) {

        if (!(entity.level() instanceof ServerLevel serverWorld)) {
            return;
        }

        AreaEffectCloud aec = new AreaEffectCloud(entity.level(), entity.getX(), entity.getY(), entity.getZ());
        if (entity instanceof LivingEntity living) {
            aec.setOwner(living);
        }

        aec.setPotionContents(effectComponent);
        aec.setRadius(radius);
        aec.setRadiusOnUse(radiusOnUse);
        aec.setDuration(duration);
        aec.setDurationOnUse(durationOnUse);
        aec.setWaitTime(waitTime);

        serverWorld.addFreshEntity(aec);

    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return EntityActionTypes.SPAWN_EFFECT_CLOUD;
    }

}
