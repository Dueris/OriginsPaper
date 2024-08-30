package io.github.dueris.originspaper.action.type.meta;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataBuilder;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class IfElseActionType {

	public static <T, U> void action(T type, @NotNull Predicate<U> condition, Consumer<T> ifAction, Consumer<T> elseAction, @NotNull Function<T, U> actionToConditionTypeFunction) {

		U convertedType = actionToConditionTypeFunction.apply(type);

		if (condition.test(convertedType)) {
			ifAction.accept(type);
		} else {
			elseAction.accept(type);
		}

	}

	public static <T, U> @NotNull ActionTypeFactory<T> getFactory(SerializableDataBuilder<ActionTypeFactory<T>> actionDataType, SerializableDataBuilder<ConditionTypeFactory<U>> conditionDataType, Function<T, U> actionToConditionTypeFunction) {
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

	public static <T> @NotNull ActionTypeFactory<T> getFactory(SerializableDataBuilder<ActionTypeFactory<T>> actionDataType, SerializableDataBuilder<ConditionTypeFactory<T>> conditionDataType) {
		return getFactory(actionDataType, conditionDataType, t -> t);
	}

}
