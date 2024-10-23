package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ActionOverTimePowerType extends PowerType {

	private final Consumer<Entity> entityAction;
	private final Consumer<Entity> risingAction;
	private final Consumer<Entity> fallingAction;

	private final int interval;

	private Integer startTicks = null;
	private Integer endTicks = null;

	private boolean wasActive = false;

	public ActionOverTimePowerType(Power power, LivingEntity entity, Consumer<Entity> entityAction, Consumer<Entity> risingAction, Consumer<Entity> fallingAction, int interval) {
		super(power, entity);
		this.interval = interval;
		this.entityAction = entityAction;
		this.risingAction = risingAction;
		this.fallingAction = fallingAction;
		this.setTicking(true);
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("action_over_time"),
			new SerializableData()
				.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
				.add("rising_action", ApoliDataTypes.ENTITY_ACTION, null)
				.add("falling_action", ApoliDataTypes.ENTITY_ACTION, null)
				.add("interval", SerializableDataTypes.POSITIVE_INT, 20),
			data -> (power, entity) -> new ActionOverTimePowerType(power, entity,
				data.get("entity_action"),
				data.get("rising_action"),
				data.get("falling_action"),
				data.get("interval")
			)
		).allowCondition();
	}

	@Override
	public void tick() {

		if (isActive()) {

			if (startTicks == null) {

				startTicks = entity.tickCount % interval;
				endTicks = null;

				return;

			}

			if (entity.tickCount % interval != startTicks) {
				return;
			}

			if (!wasActive && risingAction != null) {
				risingAction.accept(entity);
			}

			if (entityAction != null) {
				entityAction.accept(entity);
			}

			wasActive = true;

		} else if (wasActive) {

			if (endTicks == null) {

				endTicks = entity.tickCount % interval;
				startTicks = null;

				return;

			}

			if (entity.tickCount % interval != endTicks) {
				return;
			}

			if (fallingAction != null) {
				fallingAction.accept(entity);
			}

			wasActive = false;

		}

	}

	@Override
	public Tag toTag() {
		return ByteTag.valueOf(wasActive);
	}

	@Override
	public void fromTag(@NotNull Tag tag) {
		wasActive = tag.equals(ByteTag.ONE);
	}

}

