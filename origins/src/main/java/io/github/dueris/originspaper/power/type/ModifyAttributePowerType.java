package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import io.github.dueris.originspaper.util.modifier.Modifier;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.List;

public class ModifyAttributePowerType extends ValueModifyingPowerType {

	private final Holder<Attribute> attribute;

	public ModifyAttributePowerType(Power power, LivingEntity entity, Holder<Attribute> attribute, Modifier modifier, List<Modifier> modifiers) {
		super(power, entity);

		this.attribute = attribute;
		if (modifier != null) {
			this.addModifier(modifier);
		}

		if (modifiers != null) {
			modifiers.forEach(this::addModifier);
		}

	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("modify_attribute"),
			new SerializableData()
				.add("attribute", SerializableDataTypes.ATTRIBUTE_ENTRY)
				.add("modifier", Modifier.DATA_TYPE, null)
				.add("modifiers", Modifier.LIST_TYPE, null),
			data -> (power, entity) -> new ModifyAttributePowerType(power, entity,
				data.get("attribute"),
				data.get("modifier"),
				data.get("modifiers")
			)
		).allowCondition();
	}

	public Holder<Attribute> getAttribute() {
		return attribute;
	}

}
