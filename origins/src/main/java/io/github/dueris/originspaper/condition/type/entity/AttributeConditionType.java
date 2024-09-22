package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.Comparison;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.jetbrains.annotations.NotNull;

public class AttributeConditionType {

	public static boolean condition(Entity entity, Holder<Attribute> attributeEntry, Comparison comparison, double compareTo) {

		double attributeValue = 0.0D;

		if (entity instanceof LivingEntity livingEntity) {

			AttributeInstance attributeInstance = livingEntity.getAttribute(attributeEntry);

			if (attributeInstance != null) {
				attributeValue = attributeInstance.getValue();
			}

		}

		return comparison.compare(attributeValue, compareTo);

	}

	public static @NotNull ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("attribute"),
			new SerializableData()
				.add("attribute", SerializableDataTypes.ATTRIBUTE_ENTRY)
				.add("comparison", ApoliDataTypes.COMPARISON)
				.add("compare_to", SerializableDataTypes.DOUBLE),
			(data, entity) -> condition(
				entity,
				data.get("attribute"),
				data.get("comparison"),
				data.getDouble("compare_to")
			)
		);
	}

}
