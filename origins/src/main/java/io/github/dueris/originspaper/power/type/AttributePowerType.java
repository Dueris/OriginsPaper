package io.github.dueris.originspaper.power.type;

import com.mojang.datafixers.util.Pair;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.util.AttributedEntityAttributeModifier;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public class AttributePowerType extends PowerType implements AttributeModifying {

	public static final TypedDataObjectFactory<AttributePowerType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("modifier", ApoliDataTypes.ATTRIBUTED_ATTRIBUTE_MODIFIER, null)
			.addFunctionedDefault("modifiers", ApoliDataTypes.ATTRIBUTED_ATTRIBUTE_MODIFIERS, data -> Util.singletonListOrNull(data.get("modifier")))
			.add("update_health", SerializableDataTypes.BOOLEAN, true)
			.validate(Util.validateAnyFieldsPresent("modifier", "modifiers")),
		data -> new AttributePowerType(
			data.get("modifiers"),
			data.get("update_health")
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("modifiers", powerType.attributedModifiers)
			.set("update_health", powerType.updateHealth)
	);

	private final List<AttributedEntityAttributeModifier> attributedModifiers;
	private final boolean updateHealth;

	public AttributePowerType(List<AttributedEntityAttributeModifier> attributedModifiers, boolean updateHealth, Optional<EntityCondition> condition) {
		super(condition);
		this.attributedModifiers = attributedModifiers;
		this.updateHealth = updateHealth;
	}

	public AttributePowerType(List<AttributedEntityAttributeModifier> attributedModifiers, boolean updateHealth) {
		this(attributedModifiers, updateHealth, Optional.empty());
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.ATTRIBUTE;
	}

	@Override
	public void onAdded() {
		this.applyTempModifiers(getHolder());
	}

	@Override
	public void onRemoved() {
		this.removeTempModifiers(getHolder());
	}

	@Override
	public List<AttributedEntityAttributeModifier> attributedModifiers() {
		return attributedModifiers;
	}

	@Override
	public boolean shouldUpdateHealth() {
		return updateHealth;
	}

	public static <T extends AttributePowerType> TypedDataObjectFactory<T> createAttributeModifyingDataFactory(SerializableData serializableData, TriFunction<SerializableData.Instance, List<AttributedEntityAttributeModifier>, Boolean, T> fromData, BiFunction<T, SerializableData, SerializableData.Instance> toData) {
		return TypedDataObjectFactory.simple(
			serializableData
				.add("modifier", ApoliDataTypes.ATTRIBUTED_ATTRIBUTE_MODIFIER, null)
				.addFunctionedDefault("modifiers", ApoliDataTypes.ATTRIBUTED_ATTRIBUTE_MODIFIERS, data -> Util.singletonListOrNull(data.get("modifier")))
				.add("update_health", SerializableDataTypes.BOOLEAN, true)
				.validate(Util.validateAnyFieldsPresent("modifier", "modifiers")),
			data -> fromData.apply(
				data,
				data.get("modifiers"),
				data.get("update_health")
			),
			(t, _serializableData) -> toData.apply(t, _serializableData)
				.set("modifier", t.attributedModifiers())
		);
	}

}
