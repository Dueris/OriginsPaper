package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import io.github.dueris.originspaper.util.AdditionalEntityAttributesHolder;
import io.github.dueris.originspaper.util.AttributedEntityAttributeModifier;
import io.github.dueris.originspaper.util.modifier.ModifierUtil;
import net.minecraft.world.entity.LivingEntity;

import java.util.stream.Collectors;

public class ModifySwimSpeedPowerType extends ConditionedAttributePowerType {

	public ModifySwimSpeedPowerType(Power power, LivingEntity entity) {
		super(power, entity, 10, false);
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("modify_swim_speed"),
			new SerializableData()
				.add("modifier", SerializableDataTypes.ATTRIBUTE_MODIFIER, null)
				.add("modifiers", SerializableDataTypes.ATTRIBUTE_MODIFIERS, null),
			data -> ModifySwimSpeedPowerType::new
		).allowCondition();
	}

	@Override
	public void tick() {
		AdditionalEntityAttributesHolder holder = AdditionalEntityAttributesHolder.getOrCreateHolder(entity);

		if (entity.tickCount % 10 != 0) {
			if (holder.has(AdditionalEntityAttributesHolder.EntityAttribute.SWIM_SPEED)) {
				if (entity.getBukkitLivingEntity().isSwimming()) {
					entity.getBukkitEntity().setVelocity(entity.getBukkitEntity().getLocation().getDirection().multiply(holder.get(AdditionalEntityAttributesHolder.EntityAttribute.SWIM_SPEED)));
				}
			}
			return;
		}

		if (this.isActive()) {
			holder.set(AdditionalEntityAttributesHolder.EntityAttribute.SWIM_SPEED, (float) ModifierUtil.applyModifiers(entity, this.modifiers.stream().map(AttributedEntityAttributeModifier::modifier).map(ModifierUtil::fromAttributeModifier).collect(Collectors.toSet()), 0.4D));
		} else {
			holder.clear(AdditionalEntityAttributesHolder.EntityAttribute.SWIM_SPEED);
		}
	}
}
