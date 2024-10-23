package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.ClassDataRegistry;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import io.github.dueris.originspaper.util.modifier.Modifier;
import io.github.dueris.originspaper.util.modifier.ModifierUtil;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AttributeModifyTransferPowerType extends PowerType {

	private final Class<?> modifyClass;
	private final Holder<Attribute> attribute;

	private final double valueMultiplier;

	public AttributeModifyTransferPowerType(Power power, LivingEntity entity, Class<?> modifyClass, Holder<Attribute> attribute, double valueMultiplier) {
		super(power, entity);
		this.modifyClass = modifyClass;
		this.attribute = attribute;
		this.valueMultiplier = valueMultiplier;
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("attribute_modify_transfer"),
			new SerializableData()
				.add("class", ClassDataRegistry.get(PowerType.class).orElseThrow().getDataType())
				.add("attribute", SerializableDataTypes.ATTRIBUTE_ENTRY)
				.add("multiplier", SerializableDataTypes.DOUBLE, 1.0),
			data -> (power, entity) -> new AttributeModifyTransferPowerType(power, entity,
				data.get("class"),
				data.get("attribute"),
				data.getDouble("multiplier")
			)
		).allowCondition();
	}

	public boolean doesApply(@NotNull Class<?> cls) {
		return cls.equals(modifyClass);
	}

	public void addModifiers(List<Modifier> modifiers) {

		AttributeMap attributeContainer = entity.getAttributes();
		if (!attributeContainer.hasAttribute(attribute)) {
			return;
		}

		AttributeInstance attributeInstance = attributeContainer.getInstance(attribute);
		if (attributeInstance != null) {
			attributeInstance.getModifiers()
				.stream()
				.map(mod -> new AttributeModifier(mod.id(), mod.amount() * valueMultiplier, mod.operation()))
				.map(ModifierUtil::fromAttributeModifier)
				.forEach(modifiers::add);
		}

	}

	@Deprecated(forRemoval = true)
	public void apply(List<AttributeModifier> modifiers) {

		AttributeMap attributeContainer = entity.getAttributes();
		if (!attributeContainer.hasAttribute(attribute)) {
			return;
		}

		AttributeInstance attributeInstance = attributeContainer.getInstance(attribute);
		if (attributeInstance != null) {
			attributeInstance.getModifiers()
				.stream()
				.map(mod -> new AttributeModifier(mod.id(), mod.amount() * valueMultiplier, mod.operation()))
				.forEach(modifiers::add);
		}

	}

}

