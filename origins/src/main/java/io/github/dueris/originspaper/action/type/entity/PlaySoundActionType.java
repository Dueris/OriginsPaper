package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlaySoundActionType {

	public static void action(@NotNull Entity entity, SoundEvent sound, @Nullable SoundSource category, float volume, float pitch) {
		entity.level().playSound(null, entity.blockPosition(), sound, category != null ? category : entity.getSoundSource(), volume, pitch);
	}

	public static @NotNull ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("play_sound"),
			new SerializableData()
				.add("sound", SerializableDataTypes.SOUND_EVENT)
				.add("category", SerializableDataTypes.enumValue(SoundSource.class), null)
				.add("volume", SerializableDataTypes.FLOAT, 1.0f)
				.add("pitch", SerializableDataTypes.FLOAT, 1.0f),
			(data, entity) -> action(entity,
				data.get("sound"),
				data.get("category"),
				data.get("volume"),
				data.get("pitch")
			)
		);
	}

}
