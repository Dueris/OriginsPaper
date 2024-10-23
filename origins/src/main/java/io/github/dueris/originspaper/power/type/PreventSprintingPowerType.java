package io.github.dueris.originspaper.power.type;

import io.github.dueris.originspaper.power.Power;
import net.minecraft.world.entity.LivingEntity;

public class PreventSprintingPowerType extends PowerType {

	public PreventSprintingPowerType(Power power, LivingEntity entity) {
		super(power, entity);
		setTicking();
	}

	@Override
	public void tick() {
		this.entity.setSprinting(false);
	}
}
