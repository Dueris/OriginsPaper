package io.github.dueris.originspaper.data.types.modifier;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.originspaper.registry.ApoliRegistries;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.Locale;

public interface IModifierOperation {

	SerializableDataType<IModifierOperation> STRICT_DATA_TYPE =
		SerializableDataTypes.registry(IModifierOperation.class, ApoliRegistries.MODIFIER_OPERATION, "apoli");

	SerializableDataType<IModifierOperation> DATA_TYPE = SerializableDataType.of(
		(jsonElement) -> {

			if (!jsonElement.isJsonPrimitive()) {
				return STRICT_DATA_TYPE.deserialize(jsonElement);
			}

			String operation = jsonElement.getAsString().toLowerCase(Locale.ROOT);
			return switch (operation) {
				case "addition" -> ModifierOperation.ADD_BASE_EARLY;
				case "multiply_base" -> ModifierOperation.MULTIPLY_BASE_ADDITIVE;
				case "multiply_total" -> ModifierOperation.MULTIPLY_TOTAL_MULTIPLICATIVE;
				default -> STRICT_DATA_TYPE.deserialize(jsonElement);
			};

		}, IModifierOperation.class
	);

	/**
	 * Specifies which value is modified by this modifier, which can either be the base value or the total value.
	 * All {@link Phase#BASE} modifiers are applied before {@link Phase#TOTAL} modifiers.
	 *
	 * @return the {@link Phase} of this modifier.
	 */
	Phase getPhase();

	/**
	 * The order of when this modifier is applied in relation to other modifiers with the same {@link Phase}. Higher
	 * values means the modifier will be applied later.
	 *
	 * @return the order of this modifier.
	 */
	int getOrder();

	/**
	 * @return the serializable data of the modifier instance that this operation needs to operate.
	 */
	SerializableData getData();

	/**
	 * Applies all instances of this modifier operation to the specified value.
	 *
	 * @param entity    the entity these modifiers are on.
	 * @param instances the serializable data instances of each modifier with this operation.
	 * @param base      the base value to operate on. With {@link Phase#BASE}, it refers to the original base value, while in
	 *                  {@link Phase#TOTAL}, it's the <b>total</b> base value.
	 * @param current   the current value, which differs from the base value only if prior modifiers have modified it in the same phase.
	 * @return the new current value after applying all modifier instances with this operation.
	 */
	double apply(Entity entity, List<SerializableData.Instance> instances, double base, double current);

	enum Phase {
		BASE, TOTAL
	}

}
