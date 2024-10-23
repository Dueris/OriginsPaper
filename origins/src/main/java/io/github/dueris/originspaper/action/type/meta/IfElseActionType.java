package io.github.dueris.originspaper.action.type.meta;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class IfElseActionType {

	public static <T, U> void action(T type, Predicate<U> condition, Consumer<T> ifAction, Consumer<T> elseAction, Function<T, U> actionToConditionTypeFunction) {

		U convertedType = actionToConditionTypeFunction.apply(type);

		if (condition.test(convertedType)) {
			ifAction.accept(type);
		} else {
			elseAction.accept(type);
		}

	}

	public static <T, U> ActionTypeFactory<T> getFactory(SerializableDataType<ActionTypeFactory<T>.Instance> actionDataType, SerializableDataType<ConditionTypeFactory<U>.Instance> conditionDataType, Function<T, U> actionToConditionTypeFunction) {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("if_else"),
			new SerializableData()
				.add("condition", conditionDataType)
				.add("if_action", actionDataType)
				.add("else_action", actionDataType, null),
			(data, t) -> action(t,
				data.get("condition"),
				data.get("if_action"),
				data.getOrElse("else_action", _t -> {
				}),
				actionToConditionTypeFunction
			)
		);
	}

	public static <T> ActionTypeFactory<T> getFactory(SerializableDataType<ActionTypeFactory<T>.Instance> actionDataType, SerializableDataType<ConditionTypeFactory<T>.Instance> conditionDataType) {
		return getFactory(actionDataType, conditionDataType, t -> t);
	}

}
