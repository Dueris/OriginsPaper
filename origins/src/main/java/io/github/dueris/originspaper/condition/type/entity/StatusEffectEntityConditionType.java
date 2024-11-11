package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class StatusEffectEntityConditionType extends EntityConditionType {

    public static final TypedDataObjectFactory<StatusEffectEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("effect", SerializableDataTypes.STATUS_EFFECT_ENTRY)
            .add("min_amplifier", SerializableDataTypes.INT, 0)
            .add("max_amplifier", SerializableDataTypes.INT, Integer.MAX_VALUE)
            .add("min_duration", SerializableDataTypes.INT, -1)
            .add("max_duration", SerializableDataTypes.INT, Integer.MAX_VALUE),
        data -> new StatusEffectEntityConditionType(
            data.get("effect"),
            data.get("min_amplifier"),
            data.get("max_amplifier"),
            data.get("min_duration"),
            data.get("max_duration")
        ),
        (conditionType, serializableData) -> serializableData.instance()
            .set("effect", conditionType.effect)
            .set("min_amplifier", conditionType.minAmplifier)
            .set("max_amplifier", conditionType.maxAmplifier)
            .set("min_duration", conditionType.minDuration)
            .set("max_duration", conditionType.maxDuration)
    );

    private final Holder<MobEffect> effect;

    private final int minAmplifier;
    private final int maxAmplifier;

    private final int minDuration;
    private final int maxDuration;

    public StatusEffectEntityConditionType(Holder<MobEffect> effect, int minAmplifier, int maxAmplifier, int minDuration, int maxDuration) {
        this.effect = effect;
        this.minAmplifier = minAmplifier;
        this.maxAmplifier = maxAmplifier;
        this.minDuration = minDuration;
        this.maxDuration = maxDuration;
    }

    @Override
    public boolean test(Entity entity) {

        if (entity instanceof LivingEntity livingEntity) {

            MobEffectInstance effectInstance = livingEntity.getEffect(effect);

            if (effectInstance != null) {

                int duration = effectInstance.getDuration();
                int amplifier = effectInstance.getAmplifier();

                return (duration <= maxDuration && duration >= minDuration)
                    && (amplifier <= maxAmplifier && amplifier >= minAmplifier);

            }

            else {
                return false;
            }

        }

        else {
            return false;
        }

    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return EntityConditionTypes.STATUS_EFFECT;
    }

}
