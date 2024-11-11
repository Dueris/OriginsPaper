package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

public class PlaySoundEntityActionType extends EntityActionType {

    public static final TypedDataObjectFactory<PlaySoundEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("sound", SerializableDataTypes.SOUND_EVENT)
            .add("category", SerializableDataType.enumValue(SoundSource.class).optional(), Optional.empty())
            .add("volume", SerializableDataTypes.FLOAT, 1.0F)
            .add("pitch", SerializableDataTypes.FLOAT, 1.0F),
        data -> new PlaySoundEntityActionType(
            data.get("sound"),
            data.get("category"),
            data.get("volume"),
            data.get("pitch")
        ),
        (actionType, serializableData) -> serializableData.instance()
            .set("sound", actionType.sound)
            .set("category", actionType.category)
            .set("volume", actionType.volume)
            .set("pitch", actionType.pitch)
    );

    private final SoundEvent sound;
    private final Optional<SoundSource> category;

    private final float volume;
    private final float pitch;

    public PlaySoundEntityActionType(SoundEvent sound, Optional<SoundSource> category, float volume, float pitch) {
        this.sound = sound;
        this.category = category;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    protected void execute(Entity entity) {
        entity.level().playSound(null, entity.blockPosition(), sound, category.orElseGet(entity::getSoundSource), volume, pitch);
    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return EntityActionTypes.PLAY_SOUND;
    }

}
