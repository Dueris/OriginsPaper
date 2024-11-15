package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.util.AdditionalEntityAttributesHolder;
import io.github.dueris.originspaper.util.Util;
import io.github.dueris.originspaper.util.modifier.ModifierUtil;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ModifySwimSpeedPowerType extends ConditionedAttributePowerType {

	public static final TypedDataObjectFactory<ModifySwimSpeedPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("modifier", SerializableDataTypes.ATTRIBUTE_MODIFIER, null)
			.addFunctionedDefault("modifiers", SerializableDataTypes.ATTRIBUTE_MODIFIER.list(1, Integer.MAX_VALUE), data -> Util.singletonListOrNull(data.get("modifier")))
			.validate(Util.validateAnyFieldsPresent("modifier", "modifiers")),
		(data, condition) -> new ModifySwimSpeedPowerType(
			data.get("modifiers"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("modifiers", powerType.attributeModifiers)
	);

	private final List<AttributeModifier> attributeModifiers;

	public ModifySwimSpeedPowerType(List<AttributeModifier> attributeModifiers, Optional<EntityCondition> condition) {
		super(List.of(), false, 10, condition);
		this.attributeModifiers = attributeModifiers;
	}

	@Override
	public void serverTick() {
		AdditionalEntityAttributesHolder holder = AdditionalEntityAttributesHolder.getOrCreateHolder(getHolder());

		if (getHolder().tickCount % 10 != 0) {
			if (holder.has(AdditionalEntityAttributesHolder.EntityAttribute.SWIM_SPEED)) {
				if (getHolder().getBukkitLivingEntity().isSwimming()) {
					getHolder().getBukkitEntity().setVelocity(getHolder().getBukkitEntity().getLocation().getDirection().multiply(holder.get(AdditionalEntityAttributesHolder.EntityAttribute.SWIM_SPEED)));
				}
			}
			return;
		}

		if (this.isActive()) {
			holder.set(AdditionalEntityAttributesHolder.EntityAttribute.SWIM_SPEED, (float) ModifierUtil.applyModifiers(getHolder(), attributeModifiers.stream().map(ModifierUtil::fromAttributeModifier).collect(Collectors.toSet()), 0.4D));
		} else {
			holder.clear(AdditionalEntityAttributesHolder.EntityAttribute.SWIM_SPEED);
		}
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.MODIFY_SWIM_SPEED;
	}

}
