package io.github.dueris.originspaper.data.types.modifier;

import io.github.dueris.calio.data.SerializableData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ModifierUtil {

	public static @NotNull Modifier fromAttributeModifier(@NotNull AttributeModifier attributeModifier) {

		ModifierOperation operation = switch (attributeModifier.operation()) {
			case ADD_VALUE -> ModifierOperation.ADD_BASE_EARLY;
			case ADD_MULTIPLIED_BASE -> ModifierOperation.MULTIPLY_BASE_MULTIPLICATIVE;
			case ADD_MULTIPLIED_TOTAL -> ModifierOperation.MULTIPLY_TOTAL_MULTIPLICATIVE;
		};

		return Modifier.of(operation, attributeModifier.amount());

	}

	public static @NotNull Map<IModifierOperation, List<SerializableData.Instance>> sortModifiers(@NotNull List<Modifier> modifiers) {
		Map<IModifierOperation, List<SerializableData.Instance>> buckets = new HashMap<>();
		for (Modifier modifier : modifiers) {
			List<SerializableData.Instance> list = buckets.computeIfAbsent(modifier.getOperation(), op -> new LinkedList<>());
			list.add(modifier.getData());
		}
		return buckets;
	}

	public static double applyModifiers(Entity entity, List<Modifier> modifiers, double baseValue) {
		return applyModifiers(entity, sortModifiers(modifiers), baseValue);
	}

	public static double applyModifiers(Entity entity, @NotNull Map<IModifierOperation, List<SerializableData.Instance>> modifiers, double baseValue) {
		double currentBase = baseValue;
		double currentValue = baseValue;
		List<IModifierOperation> operations = new LinkedList<>(modifiers.keySet());
		operations.sort(((o1, o2) -> {
			if (o1 == o2) {
				return 0;
			} else if (o1.getPhase() == o2.getPhase()) {
				return o1.getOrder() - o2.getOrder();
			} else {
				return o1.getPhase().ordinal() - o2.getPhase().ordinal();
			}
		}));
		IModifierOperation.Phase lastPhase = IModifierOperation.Phase.BASE;
		for (IModifierOperation op : operations) {
			List<SerializableData.Instance> data = modifiers.get(op);
			if (op.getPhase() != lastPhase) {
				currentBase = currentValue;
			}
			currentValue = op.apply(entity, data, currentBase, currentValue);
		}
		return currentValue;
	}
}
