package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.EntityAction;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerConfiguration;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;

public class ActionOverTimePowerType extends PowerType {

	public static final TypedDataObjectFactory<ActionOverTimePowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("entity_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
			.add("rising_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
			.add("falling_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
			.add("interval", SerializableDataTypes.POSITIVE_INT, 20),
		(data, condition) -> new ActionOverTimePowerType(
			data.get("entity_action"),
			data.get("rising_action"),
			data.get("falling_action"),
			data.get("interval"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("entity_action", powerType.entityAction)
			.set("rising_action", powerType.risingAction)
			.set("falling_action", powerType.fallingAction)
			.set("interval", powerType.interval)
	);

	private final Optional<EntityAction> entityAction;
	private final Optional<EntityAction> risingAction;
	private final Optional<EntityAction> fallingAction;

	private final int interval;

	private Integer startTicks = null;
	private Integer endTicks = null;

	private boolean wasActive = false;

	public ActionOverTimePowerType(Optional<EntityAction> entityAction, Optional<EntityAction> risingAction, Optional<EntityAction> fallingAction, int interval, Optional<EntityCondition> condition) {
		super(condition);
		this.interval = interval;
		this.entityAction = entityAction;
		this.risingAction = risingAction;
		this.fallingAction = fallingAction;
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.ACTION_OVER_TIME;
	}

	@Override
	public boolean shouldTick() {
		return true;
	}

	@Override
	public boolean shouldTickWhenInactive() {
		return shouldTick();
	}

	@Override
	public void serverTick() {

		if (isActive()) {

			if (startTicks == null) {

				startTicks = getHolder().tickCount % interval;
				endTicks = null;

				return;

			}

			if (getHolder().tickCount % interval != startTicks) {
				return;
			}

			if (!wasActive) {
				risingAction.ifPresent(action -> action.execute(getHolder()));
			}

			entityAction.ifPresent(action -> action.execute(getHolder()));
			wasActive = true;

		}

		else if (wasActive) {

			if (endTicks == null) {

				endTicks = getHolder().tickCount % interval;
				startTicks = null;

				return;

			}

			if (getHolder().tickCount % interval != endTicks) {
				return;
			}

			fallingAction.ifPresent(action -> action.execute(getHolder()));
			wasActive = false;

		}

	}

	@Override
	public Tag toTag() {
		return ByteTag.valueOf(wasActive);
	}

	@Override
	public void fromTag(Tag tag) {
		wasActive = tag.equals(ByteTag.ONE);
	}

}
