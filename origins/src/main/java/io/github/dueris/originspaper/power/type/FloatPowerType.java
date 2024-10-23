package io.github.dueris.originspaper.power.type;

import io.github.dueris.originspaper.power.Power;
import net.minecraft.world.entity.LivingEntity;

public class FloatPowerType extends PowerType {

	public final float value;

	public FloatPowerType(Power power, LivingEntity entity, float value) {
		super(power, entity);
		this.value = value;
	}

}
