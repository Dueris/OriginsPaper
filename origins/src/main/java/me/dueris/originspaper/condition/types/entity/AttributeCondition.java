package me.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.condition.ConditionFactory;
import me.dueris.originspaper.data.ApoliDataTypes;
import me.dueris.originspaper.data.types.Comparison;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.jetbrains.annotations.NotNull;

public class AttributeCondition {

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

	public static @NotNull ConditionFactory<Entity> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("attribute"),
			InstanceDefiner.instanceDefiner()
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
