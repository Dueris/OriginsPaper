package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Predicate;

public class ClimbingPowerType extends PowerType {

	private final Predicate<Entity> holdingCondition;
	private final boolean allowHolding;

	public ClimbingPowerType(Power power, LivingEntity entity, Predicate<Entity> holdingCondition, boolean allowHolding) {
		super(power, entity);
		this.holdingCondition = holdingCondition;
		this.allowHolding = allowHolding;
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("climbing"),
			new SerializableData()
				.add("hold_condition", ApoliDataTypes.ENTITY_CONDITION, null)
				.add("allow_holding", SerializableDataTypes.BOOLEAN, true),
			data -> (power, entity) -> new ClimbingPowerType(power, entity,
				data.get("hold_condition"),
				data.get("allow_holding")
			)
		).allowCondition();
	}

	public boolean canHold() {
		return allowHolding && this.shouldHold();
	}

	public boolean shouldHold() {
		return holdingCondition != null
			? holdingCondition.test(entity)
			: entity.isShiftKeyDown();
	}

}

