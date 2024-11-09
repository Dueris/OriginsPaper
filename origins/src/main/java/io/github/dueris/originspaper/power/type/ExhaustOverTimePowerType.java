package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class ExhaustOverTimePowerType extends PowerType {

	private final int exhaustInterval;
	private final float exhaustion;

	public ExhaustOverTimePowerType(Power power, LivingEntity entity, int exhaustInterval, float exhaustion) {
		super(power, entity);
		this.exhaustInterval = exhaustInterval;
		this.exhaustion = exhaustion;
		this.setTicking();
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("exhaust"),
			new SerializableData()
				.add("interval", SerializableDataTypes.POSITIVE_INT, 20)
				.add("exhaustion", SerializableDataTypes.FLOAT),
			data -> (power, entity) -> new ExhaustOverTimePowerType(power, entity,
				data.getInt("interval"),
				data.getFloat("exhaustion")
			)
		).allowCondition();
	}

	public void tick() {

		if (entity instanceof Player playerEntity && entity.tickCount % exhaustInterval == 0) {
			playerEntity.causeFoodExhaustion(exhaustion);
		}

	}
}
