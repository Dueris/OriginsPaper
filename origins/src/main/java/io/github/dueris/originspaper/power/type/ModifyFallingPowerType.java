package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.util.modifier.Modifier;
import io.github.dueris.originspaper.util.modifier.ModifierOperation;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class ModifyFallingPowerType extends ValueModifyingPowerType {

	public static final TypedDataObjectFactory<ModifyFallingPowerType> DATA_FACTORY = createConditionedDataFactory(
		ValueModifyingPowerType.addModifierFields(new SerializableData()
			.add("velocity", SerializableDataTypes.DOUBLE.optional(), Optional.empty())
			.add("take_fall_damage", SerializableDataTypes.BOOLEAN, true)),
		(data, condition) -> new ModifyFallingPowerType(
			data.get("velocity"),
			data.get("take_fall_damage"),
			data.get("modifiers"),
			condition
		),
		(powerType, serializableData) -> powerType.setModifiersField(serializableData.instance())
			.set("velocity", powerType.velocity)
			.set("take_fall_damage", powerType.takeFallDamage)
	);

	protected final Optional<Double> velocity;
	protected final boolean takeFallDamage;

	private final Optional<List<Modifier>> velocityModifier;

	public ModifyFallingPowerType(Optional<Double> velocity, boolean takeFallDamage, List<Modifier> modifiers, Optional<EntityCondition> condition) {
		super(modifiers, condition);
		this.velocity = velocity;
		this.takeFallDamage = takeFallDamage;
		this.velocityModifier = velocity
			.map(value -> Modifier.of(ModifierOperation.SET_TOTAL, value))
			.map(ObjectArrayList::of);
	}

	public static boolean shouldNegateFallDamage(Entity entity) {
		return PowerHolderComponent.hasPowerType(entity, ModifyFallingPowerType.class, Predicate.not(ModifyFallingPowerType::shouldTakeFallDamage));
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.MODIFY_FALLING;
	}

	@Override
	public List<Modifier> getModifiers() {
		return velocityModifier.orElseGet(super::getModifiers);
	}

	public boolean shouldTakeFallDamage() {
		return takeFallDamage;
	}

}
