package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerConfiguration;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ExhaustOverTimePowerType extends PowerType {

	public static final TypedDataObjectFactory<ExhaustOverTimePowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("interval", SerializableDataTypes.POSITIVE_INT, 20)
			.add("exhaustion", SerializableDataTypes.FLOAT),
		(data, condition) -> new ExhaustOverTimePowerType(
			data.get("interval"),
			data.get("exhaustion"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("interval", powerType.exhaustInterval)
			.set("exhaustion", powerType.exhaustion)
	);

	private final int exhaustInterval;
	private final float exhaustion;

	private Integer startTicks = null;
	private Integer endTicks = null;

	private boolean wasActive = false;

	public ExhaustOverTimePowerType(int exhaustInterval, float exhaustion, Optional<EntityCondition> condition) {
		super(condition);
		this.exhaustInterval = exhaustInterval;
		this.exhaustion = exhaustion;
		this.setTicking(true);
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.EXHAUST;
	}

	@Override
	public void serverTick() {

		if (!(getHolder() instanceof Player holderPlayer)) {
			return;
		}

		if (isActive()) {

			if (startTicks == null) {
				this.startTicks = holderPlayer.tickCount % exhaustInterval;
				this.endTicks = null;
			} else if (holderPlayer.tickCount % exhaustInterval == startTicks) {
				holderPlayer.causeFoodExhaustion(exhaustion);
				this.wasActive = true;
			}

		} else if (wasActive) {

			if (endTicks == null) {
				this.startTicks = null;
				this.endTicks = holderPlayer.tickCount % exhaustInterval;
			} else if (holderPlayer.tickCount % exhaustInterval == endTicks) {
				this.wasActive = false;
			}

		}

	}

}
