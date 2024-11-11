package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class ClearEffectEntityActionType extends EntityActionType {

    public static final TypedDataObjectFactory<ClearEffectEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("effect", SerializableDataTypes.STATUS_EFFECT_ENTRY.optional(), Optional.empty())
            .add("effects", SerializableDataTypes.STATUS_EFFECT_ENTRIES.optional(), Optional.empty()),
        data -> new ClearEffectEntityActionType(
            data.get("effect"),
            data.get("effects")
        ),
        (actionType, serializableData) -> serializableData.instance()
            .set("effect", actionType.effect)
            .set("effects", actionType.effects)
    );

    private final Optional<Holder<MobEffect>> effect;
    private final Optional<List<Holder<MobEffect>>> effects;

    private final List<Holder<MobEffect>> allEffects;

    public ClearEffectEntityActionType(Optional<Holder<MobEffect>> effect, Optional<List<Holder<MobEffect>>> effects) {

        this.effect = effect;
        this.effects = effects;

        this.allEffects = new ObjectArrayList<>();

        this.effect.ifPresent(this.allEffects::add);
        this.effects.ifPresent(this.allEffects::addAll);

    }

    @Override
    protected void execute(Entity entity) {

        if (entity instanceof LivingEntity living) {

            if (!allEffects.isEmpty()) {
                allEffects.forEach(living::removeEffect);
            }

            else {
                living.removeAllEffects();
            }

        }

    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return EntityActionTypes.CLEAR_EFFECT;
    }

}
