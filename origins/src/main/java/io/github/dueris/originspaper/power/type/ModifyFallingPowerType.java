package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import io.github.dueris.originspaper.util.modifier.Modifier;
import io.github.dueris.originspaper.util.modifier.ModifierOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class ModifyFallingPowerType extends ValueModifyingPowerType {

	private final boolean takeFallDamage;

	public ModifyFallingPowerType(Power power, LivingEntity entity, @Nullable Double velocity, boolean takeFallDamage, Modifier modifier, List<Modifier> modifiers) {
		super(power, entity);
		this.takeFallDamage = takeFallDamage;

		if (velocity != null) {
			this.addModifier(Modifier.of(ModifierOperation.SET_TOTAL, velocity));
		} else {

			if (modifier != null) {
				this.addModifier(modifier);
			}

			if (modifiers != null) {
				modifiers.forEach(this::addModifier);
			}

		}

	}

	public static boolean shouldNegateFallDamage(Entity entity) {
		return PowerHolderComponent.hasPowerType(entity, ModifyFallingPowerType.class, Predicate.not(ModifyFallingPowerType::shouldTakeFallDamage));
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("modify_falling"),
			new SerializableData()
				.add("velocity", SerializableDataTypes.DOUBLE, null)
				.add("take_fall_damage", SerializableDataTypes.BOOLEAN, true)
				.add("modifier", Modifier.DATA_TYPE, null)
				.add("modifiers", Modifier.LIST_TYPE, null),
			data -> (power, entity) -> new ModifyFallingPowerType(power, entity,
				data.get("velocity"),
				data.get("take_fall_damage"),
				data.get("modifier"),
				data.get("modifiers")
			)
		).allowCondition();
	}

	public boolean shouldTakeFallDamage() {
		return takeFallDamage;
	}

}
