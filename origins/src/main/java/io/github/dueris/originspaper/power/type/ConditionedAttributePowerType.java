package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import io.github.dueris.originspaper.util.AttributedEntityAttributeModifier;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class ConditionedAttributePowerType extends AttributePowerType {

	private final int tickRate;

	public ConditionedAttributePowerType(Power power, LivingEntity entity, int tickRate, boolean updateHealth) {
		super(power, entity, updateHealth);
		this.tickRate = tickRate;
		this.setTicking(true);
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("conditioned_attribute"),
			new SerializableData()
				.add("modifier", ApoliDataTypes.ATTRIBUTED_ATTRIBUTE_MODIFIER, null)
				.add("modifiers", ApoliDataTypes.ATTRIBUTED_ATTRIBUTE_MODIFIERS, null)
				.add("tick_rate", SerializableDataTypes.POSITIVE_INT, 20)
				.add("update_health", SerializableDataTypes.BOOLEAN, true),
			data -> (power, entity) -> {

				ConditionedAttributePowerType conditionedAttributePower = new ConditionedAttributePowerType(
					power,
					entity,
					data.get("tick_rate"),
					data.get("update_health")
				);

				data.<AttributedEntityAttributeModifier>ifPresent("modifier", conditionedAttributePower::addModifier);
				data.<List<AttributedEntityAttributeModifier>>ifPresent("modifiers", mods -> mods.forEach(conditionedAttributePower::addModifier));

				return conditionedAttributePower;

			}
		).allowCondition();
	}

	@Override
	public void onAdded() {

	}

	@Override
	public void onRemoved() {

	}

	@Override
	public void onLost() {
		this.removeTempMods();
	}

	@Override
	public void tick() {

		if (entity.tickCount % tickRate != 0) {
			return;
		}

		if (this.isActive()) {
			this.applyTempMods();
		} else {
			this.removeTempMods();
		}

	}

}

