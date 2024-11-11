package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.util.Util;
import io.github.dueris.originspaper.util.modifier.Modifier;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.apache.commons.lang3.function.TriFunction;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public abstract class ValueModifyingPowerType extends PowerType {

	private final List<Modifier> modifiers;

	public ValueModifyingPowerType(List<Modifier> modifiers, Optional<EntityCondition> condition) {
		super(condition);
		this.modifiers = new ObjectArrayList<>(modifiers);
	}

	public ValueModifyingPowerType(Optional<EntityCondition> condition) {
		super(condition);
		this.modifiers = new ObjectArrayList<>();
	}

	public ValueModifyingPowerType() {
		this(Optional.empty());
	}

	protected void addModifier(Modifier modifier) {
		this.modifiers.add(modifier);
	}

	public List<Modifier> getModifiers() {
		return new ObjectArrayList<>(modifiers);
	}

	protected final SerializableData.Instance setField(SerializableData.Instance data) {
		return data.set("modifiers", getModifiers());
	}

	private static SerializableData addFields(SerializableData serializableData) {
		return serializableData
			.add("modifier", Modifier.DATA_TYPE, null)
			.addFunctionedDefault("modifiers", Modifier.LIST_TYPE, data -> Util.singletonListOrNull(data.get("modifier")))
			.validate(Util.validateAnyFieldsPresent("modifier", "modifiers"));
	}

	private static List<Modifier> getField(SerializableData.Instance data) {
		return data.get("modifiers");
	}

	public static <T extends ValueModifyingPowerType> TypedDataObjectFactory<T> createModifyingDataFactory(SerializableData serializableData, BiFunction<SerializableData.Instance, List<Modifier>, T> fromData, BiFunction<T, SerializableData, SerializableData.Instance> toData) {
		return TypedDataObjectFactory.simple(
			addFields(serializableData),
			data -> fromData.apply(
				data,
				getField(data)
			),
			(t, _serializableData) ->
				t.setField(toData.apply(t, _serializableData))
		);
	}

	public static <T extends ValueModifyingPowerType> TypedDataObjectFactory<T> createConditionedModifyingDataFactory(SerializableData serializableData, TriFunction<SerializableData.Instance, List<Modifier>, Optional<EntityCondition>, T> fromData, BiFunction<T, SerializableData, SerializableData.Instance> toData) {
		return PowerType.createConditionedDataFactory(
			addFields(serializableData),
			(data, condition) -> fromData.apply(
				data,
				getField(data),
				condition
			),
			(t, _serializableData) ->
				t.setField(toData.apply(t, _serializableData))
		);
	}

	public static <T extends ValueModifyingPowerType> PowerConfiguration<T> createModifyingConfiguration(ResourceLocation id, BiFunction<List<Modifier>, Optional<EntityCondition>, T> constructor) {
		return PowerConfiguration.dataFactory(id, createConditionedModifyingDataFactory(new SerializableData(), (data, modifiers, condition) -> constructor.apply(modifiers, condition), (t, serializableData) -> serializableData.instance()));
	}

}
