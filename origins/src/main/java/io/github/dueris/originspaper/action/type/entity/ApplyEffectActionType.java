package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collection;
import java.util.LinkedList;

public class ApplyEffectActionType {

	public static void action(Entity entity, Collection<MobEffectInstance> effects) {

		if (!entity.level().isClientSide && entity instanceof LivingEntity living) {
			effects.forEach(living::addEffect);
		}

	}

	public static ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("apply_effect"),
			new SerializableData()
				.add("effect", SerializableDataTypes.STATUS_EFFECT_INSTANCE, null)
				.add("effects", SerializableDataTypes.STATUS_EFFECT_INSTANCES, null),
			(data, entity) -> {

				Collection<MobEffectInstance> effects = new LinkedList<>();

				data.ifPresent("effect", effects::add);
				data.ifPresent("effects", effects::addAll);

				action(entity, effects);

			}
		);
	}

}
