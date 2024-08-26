package io.github.dueris.originspaper.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionTypeFactory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ApplyEffectAction {

	public static @NotNull ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(OriginsPaper.apoliIdentifier("apply_effect"),
			SerializableData.serializableData()
				.add("effect", SerializableDataTypes.STATUS_EFFECT_INSTANCE, null)
				.add("effects", SerializableDataTypes.list(SerializableDataTypes.STATUS_EFFECT_INSTANCE), null),
			(data, entity) -> {
				if (entity instanceof LivingEntity le && !entity.level().isClientSide) {
					if (data.isPresent("effect")) {
						MobEffectInstance effect = data.get("effect");
						le.addEffect(new MobEffectInstance(effect));
					}
					if (data.isPresent("effects")) {
						((List<MobEffectInstance>) data.get("effects")).forEach(e -> le.addEffect(new MobEffectInstance(e)));
					}
				}
			}
		);
	}
}
