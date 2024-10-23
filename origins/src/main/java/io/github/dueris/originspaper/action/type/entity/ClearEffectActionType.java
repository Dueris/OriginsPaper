package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collection;
import java.util.LinkedList;

public class ClearEffectActionType {

	public static void action(Entity entity, Collection<Holder<MobEffect>> effects) {

		if (entity instanceof LivingEntity living) {

			if (!effects.isEmpty()) {
				effects.forEach(living::removeEffect);
			} else {
				living.removeAllEffects();
			}

		}

	}

	public static ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("clear_effect"),
			new SerializableData()
				.add("effect", SerializableDataTypes.STATUS_EFFECT_ENTRY, null)
				.add("effects", SerializableDataTypes.STATUS_EFFECT_ENTRIES, null),
			(data, entity) -> {

				Collection<Holder<MobEffect>> effects = new LinkedList<>();

				data.ifPresent("effect", effects::add);
				data.ifPresent("effects", effects::addAll);

				action(entity, effects);

			}
		);
	}

}
