package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import net.minecraft.world.entity.LivingEntity;

public class CreativeFlightPowerType extends PowerType {

	public CreativeFlightPowerType(Power power, LivingEntity entity) {
		super(power, entity);
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("creative_flight"),
			new SerializableData(),
			data -> CreativeFlightPowerType::new
		).allowCondition();
	}
}
