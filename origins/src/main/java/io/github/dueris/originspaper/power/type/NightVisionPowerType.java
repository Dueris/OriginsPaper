package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class NightVisionPowerType extends PowerType {

	private final float strength;

	public NightVisionPowerType(Power power, LivingEntity entity, float strength) {
		super(power, entity);
		this.strength = strength;
		this.setTicking(true);
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("night_vision"),
			new SerializableData()
				.add("strength", SerializableDataTypes.FLOAT, 1.0F),
			data -> (power, entity) -> new NightVisionPowerType(power, entity,
				data.getFloat("strength")
			)
		).allowCondition();
	}

	@Override
	public void tick() {
		if (isActive()) {
			if (!entity.hasEffect(MobEffects.NIGHT_VISION)) {
				entity.addEffect(
					new MobEffectInstance(MobEffects.NIGHT_VISION, Integer.MAX_VALUE, 255, false, false, false)
				);
			}
		} else {
			if (entity.hasEffect(MobEffects.NIGHT_VISION)) {
				entity.removeEffect(MobEffects.NIGHT_VISION);
			}
		}
	}

	@Override
	public void onRemoved() {
		this.entity.removeEffect(MobEffects.NIGHT_VISION);
	}

	public float getStrength() {
		return strength;
	}

}
