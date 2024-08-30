package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataBuilder;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;

public class ApplyEffectActionType {

	public static void action(@NotNull Entity entity, Collection<MobEffectInstance> effects) {

		if (!entity.level().isClientSide && entity instanceof LivingEntity living) {
			effects.forEach(living::addEffect);
		}

	}

	public static @NotNull ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("apply_effect"),
			new SerializableData()
				.add("effect", SerializableDataTypes.STATUS_EFFECT_INSTANCE, null)
				.add("effects", SerializableDataBuilder.of(SerializableDataTypes.STATUS_EFFECT_INSTANCE.listOf()), null),
			(data, entity) -> {

				Collection<MobEffectInstance> effects = new LinkedList<>();

				data.ifPresent("effect", effects::add);
				data.ifPresent("effects", effects::addAll);

				action(entity, effects);

			}
		);
	}

}
