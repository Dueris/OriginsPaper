package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import io.github.dueris.originspaper.util.modifier.Modifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;

public class ValueModifyingPowerType extends PowerType {

	private final List<Modifier> modifiers = new LinkedList<>();

	public ValueModifyingPowerType(Power power, LivingEntity entity) {
		super(power, entity);
	}

	public static <T extends ValueModifyingPowerType> PowerTypeFactory<T> createValueModifyingFactory(ResourceLocation id, BiFunction<Power, LivingEntity, T> powerConstructor) {
		return new PowerTypeFactory<>(id,
			new SerializableData()
				.add("modifier", Modifier.DATA_TYPE, null)
				.add("modifiers", Modifier.LIST_TYPE, null),
			data -> (power, entity) -> {

				T powerType = powerConstructor.apply(power, entity);

				data.ifPresent("modifier", powerType::addModifier);
				data.<List<Modifier>>ifPresent("modifiers", mods -> mods.forEach(powerType::addModifier));

				return powerType;

			}
		).allowCondition();
	}

	public void addModifier(Modifier modifier) {
		this.modifiers.add(modifier);
	}

	public List<Modifier> getModifiers() {
		return modifiers;
	}

}

