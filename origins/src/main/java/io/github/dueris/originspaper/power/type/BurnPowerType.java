package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerConfiguration;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class BurnPowerType extends PowerType {

	public static final TypedDataObjectFactory<BurnPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("burn_duration", SerializableDataTypes.POSITIVE_FLOAT)
			.add("interval", SerializableDataTypes.POSITIVE_INT),
		(data, condition) -> new BurnPowerType(
			data.get("burn_duration"),
			data.get("interval"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("burn_duration", powerType.burnDuration)
			.set("interval", powerType.interval)
	);

	private final float burnDuration;
	private final int interval;

	private Integer startTicks = null;
	private Integer endTicks = null;

	private boolean wasActive = false;

	public BurnPowerType(float burnDuration, int interval, Optional<EntityCondition> condition) {
		super(condition);
		this.interval = interval;
		this.burnDuration = burnDuration;
		this.setTicking(true);
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.BURN;
	}

	@Override
	public void serverTick() {

		if (isActive()) {

			if (startTicks == null) {
				this.startTicks = getHolder().tickCount % interval;
				this.endTicks = null;
			}

			else if (getHolder().tickCount % interval == startTicks) {
				getHolder().igniteForSeconds(burnDuration);
				this.wasActive = true;
			}

		}

		else if (wasActive) {

			if (endTicks == null) {
				this.startTicks = null;
				this.endTicks = getHolder().tickCount % interval;
			}

			else if (getHolder().tickCount % interval == endTicks) {
				this.wasActive = false;
			}

		}

	}

}
