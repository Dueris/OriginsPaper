package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import net.minecraft.world.entity.LivingEntity;

public class BurnPowerType extends PowerType {

	private final int interval;
	private final int burnDuration;

	public BurnPowerType(Power power, LivingEntity entity, int interval, int burnDuration) {
		super(power, entity);
		this.interval = interval;
		this.burnDuration = burnDuration;
		this.setTicking();
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("burn"),
			new SerializableData()
				.add("interval", SerializableDataTypes.POSITIVE_INT)
				.add("burn_duration", SerializableDataTypes.POSITIVE_INT),
			data -> (power, entity) -> new BurnPowerType(power, entity,
				data.getInt("interval"),
				data.getInt("burn_duration")
			)
		).allowCondition();
	}

	public void tick() {

		if (entity.tickCount % interval == 0) {
			entity.igniteForSeconds(burnDuration);
		}

	}

}
