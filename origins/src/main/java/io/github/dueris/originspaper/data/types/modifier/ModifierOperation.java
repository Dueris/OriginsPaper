package io.github.dueris.originspaper.data.types.modifier;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.stream.Collectors;

public enum ModifierOperation implements IModifierOperation {

	ADD_BASE_EARLY(Phase.BASE, 0,
		(values, base, baseTotal) -> values.stream().reduce(baseTotal, Double::sum)),
	MULTIPLY_BASE_ADDITIVE(Phase.BASE, 100,
		(values, base, baseTotal) -> baseTotal + (base * values.stream().reduce(0.0, Double::sum))),
	MULTIPLY_BASE_MULTIPLICATIVE(Phase.BASE, 200,
		(values, base, baseTotal) -> baseTotal * (1.0 + values.stream().reduce(0.0, Double::sum))),
	ADD_BASE_LATE(Phase.BASE, 300,
		(values, base, baseTotal) -> values.stream().reduce(baseTotal, Double::sum)),
	MIN_BASE(Phase.BASE, 400,
		(values, base, baseTotal) -> values.stream().reduce(baseTotal, Math::max)),
	MAX_BASE(Phase.BASE, 500,
		(values, base, baseTotal) -> values.stream().reduce(baseTotal, Math::min)),
	SET_BASE(Phase.BASE, 600,
		(values, base, baseTotal) -> values.stream().reduce(baseTotal, (a, b) -> b)),
	ADD_TOTAL_EARLY(Phase.TOTAL, 0,
		(values, totalBase, total) -> values.stream().reduce(total, Double::sum)),
	MULTIPLY_TOTAL_ADDITIVE(Phase.TOTAL, 100,
		(values, totalBase, total) -> total + (totalBase * values.stream().reduce(0.0, Double::sum))),
	MULTIPLY_TOTAL_MULTIPLICATIVE(Phase.TOTAL, 200,
		(values, totalBase, total) -> total * (1.0 + values.stream().reduce(0.0, Double::sum))),
	ADD_TOTAL_LATE(Phase.TOTAL, 300,
		(values, totalBase, total) -> values.stream().reduce(total, Double::sum)),
	MIN_TOTAL(Phase.TOTAL, 400,
		(values, totalBase, total) -> values.stream().reduce(total, Math::max)),
	MAX_TOTAL(Phase.TOTAL, 500,
		(values, totalBase, total) -> values.stream().reduce(total, Math::min)),
	SET_TOTAL(Phase.TOTAL, 600,
		(values, totalBase, total) -> values.stream().reduce(total, (a, b) -> b));

	public static final SerializableData DATA = SerializableData.serializableData()
		.add("amount", SerializableDataTypes.DOUBLE)
		.add("modifier", Modifier.LIST_TYPE, null);

	private final Phase phase;
	private final int order;
	private final PropertyDispatch.TriFunction<List<Double>, Double, Double, Double> function;

	ModifierOperation(Phase phase, int order, PropertyDispatch.TriFunction<List<Double>, Double, Double, Double> function) {
		this.phase = phase;
		this.order = order;
		this.function = function;
	}

	@Override
	public Phase getPhase() {
		return phase;
	}

	@Override
	public int getOrder() {
		return order;
	}

	@Override
	public SerializableData getData() {
		return DATA;
	}

	@Override
	public double apply(Entity entity, List<SerializableData.Instance> instances, double base, double current) {
		return function.apply(
			instances.stream()
				.map(instance -> {
					double value = instance.get("amount");
					if (instance.isPresent("modifier")) {
						List<Modifier> modifiers = instance.get("modifier");
						value = ModifierUtil.applyModifiers(entity, modifiers, value);
					}
					return value;
				})
				.collect(Collectors.toList()),
			base, current);
	}

}
