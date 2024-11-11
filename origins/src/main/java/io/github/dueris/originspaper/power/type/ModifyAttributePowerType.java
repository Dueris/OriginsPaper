package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.util.modifier.Modifier;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class ModifyAttributePowerType extends ValueModifyingPowerType {

	public static final TypedDataObjectFactory<ModifyAttributePowerType> DATA_FACTORY = createConditionedModifyingDataFactory(
		new SerializableData()
			.add("attribute", SerializableDataTypes.ATTRIBUTE_ENTRY),
		(data, modifiers, condition) -> new ModifyAttributePowerType(
			data.get("attribute"),
			modifiers,
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("attribute", powerType.attribute)
	);

	private final Holder<Attribute> attribute;

	public ModifyAttributePowerType(Holder<Attribute> attribute, List<Modifier> modifiers, Optional<EntityCondition> condition) {
		super(modifiers, condition);
		this.attribute = attribute;
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.MODIFY_ATTRIBUTE;
	}

	public Holder<Attribute> getAttribute() {
		return attribute;
	}

}
