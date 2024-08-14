package io.github.dueris.originspaper.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class PlaySoundAction {

	public static void action(@NotNull DeserializedFactoryJson data, @NotNull Entity entity) {
		SoundSource category = data.isPresent("category") ? data.get("category") : entity.getSoundSource();
		entity.level().playSound(null, entity.blockPosition(), data.get("sound"), category, data.getFloat("volume"), data.getFloat("pitch"));
	}

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(
			OriginsPaper.apoliIdentifier("play_sound"),
			InstanceDefiner.instanceDefiner()
				.add("sound", SerializableDataTypes.SOUND_EVENT)
				.add("category", SerializableDataTypes.enumValue(SoundSource.class), null)
				.add("volume", SerializableDataTypes.FLOAT, 1.0f)
				.add("pitch", SerializableDataTypes.FLOAT, 1.0f),
			PlaySoundAction::action
		);
	}
}
