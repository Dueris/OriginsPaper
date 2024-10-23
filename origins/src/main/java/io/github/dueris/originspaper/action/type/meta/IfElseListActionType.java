package io.github.dueris.originspaper.action.type.meta;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import net.minecraft.util.Tuple;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class IfElseListActionType {

	public static <T, U> void action(T type, Collection<Tuple<Consumer<T>, Predicate<U>>> actions, Function<T, U> actionToConditionTypeFunction) {

		U convertedType = actionToConditionTypeFunction.apply(type);

		for (Tuple<Consumer<T>, Predicate<U>> action : actions) {

			if (action.getB().test(convertedType)) {
				action.getA().accept(type);
				break;
			}

		}

	}

	public static <T, U> ActionTypeFactory<T> getFactory(SerializableDataType<ActionTypeFactory<T>.Instance> actionDataType, SerializableDataType<ConditionTypeFactory<U>.Instance> conditionDataType, Function<T, U> actionToConditionTypeFunction) {

		SerializableDataType<Tuple<ActionTypeFactory<T>.Instance, ConditionTypeFactory<T>.Instance>> dataType = SerializableDataType.compound(
			new SerializableData()
				.add("action", actionDataType)
				.add("condition", conditionDataType),
			data -> new Tuple<>(
				data.get("action"),
				data.get("condition")
			),
			(pair, serializableData) -> serializableData.instance()
				.set("action", pair.getA())
				.set("condition", pair.getB())
		);

		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("if_else_list"),
			new SerializableData()
				.add("actions", dataType.list()),
			(data, t) -> action(t,
				data.get("actions"),
				actionToConditionTypeFunction
			)
		);

	}

	public static <T> ActionTypeFactory<T> getFactory(SerializableDataType<ActionTypeFactory<T>.Instance> actionDataType, SerializableDataType<ConditionTypeFactory<T>.Instance> conditionDataType) {
		return getFactory(actionDataType, conditionDataType, t -> t);
	}

}
