package me.dueris.originspaper.data.types.modifier;

import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ModifierUtil {

	public static @NotNull Map<IModifierOperation, List<DeserializedFactoryJson>> sortModifiers(@NotNull List<Modifier> modifiers) {
		Map<IModifierOperation, List<DeserializedFactoryJson>> buckets = new HashMap<>();
		for (Modifier modifier : modifiers) {
			List<DeserializedFactoryJson> list = buckets.computeIfAbsent(modifier.getOperation(), op -> new LinkedList<>());
			list.add(modifier.getData());
		}
		return buckets;
	}

	public static double applyModifiers(Entity entity, List<Modifier> modifiers, double baseValue) {
		return applyModifiers(entity, sortModifiers(modifiers), baseValue);
	}

	public static double applyModifiers(Entity entity, @NotNull Map<IModifierOperation, List<DeserializedFactoryJson>> modifiers, double baseValue) {
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
			List<DeserializedFactoryJson> data = modifiers.get(op);
			if (op.getPhase() != lastPhase) {
				currentBase = currentValue;
			}
			currentValue = op.apply(entity, data, currentBase, currentValue);
		}
		return currentValue;
	}
}
