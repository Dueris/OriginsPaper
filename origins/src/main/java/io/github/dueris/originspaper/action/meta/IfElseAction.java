package io.github.dueris.originspaper.action.meta;

import io.github.dueris.calio.data.SerializableDataBuilder;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionTypeFactory;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class IfElseAction {

	public static <T, U> @NotNull ActionTypeFactory<T> getFactory(
		SerializableDataBuilder<ActionTypeFactory<T>> actionDataType,
		SerializableDataBuilder<ConditionTypeFactory<U>> conditionDataType,
		Function<T, U> actionToConditionTypeFunction) {
		return new ActionTypeFactory<T>(OriginsPaper.apoliIdentifier("if_else"),
			SerializableData.serializableData()
				.add("condition", conditionDataType)
				.add("if_action", actionDataType)
				.add("else_action", actionDataType, null),
			(data, t) -> {
				ConditionTypeFactory<U> condition = data.get("condition");
				ActionTypeFactory<T> ifAction = data.get("if_action");
				U u = actionToConditionTypeFunction.apply(t);
				if (condition.test(u)) {
					ifAction.accept(t);
				} else if (data.isPresent("else_action")) {
					ActionTypeFactory<T> elseAction = data.get("else_action");
					elseAction.accept(t);
				}
			}
		);
	}

	public static <T> @NotNull ActionTypeFactory<T> getFactory(
		SerializableDataBuilder<ActionTypeFactory<T>> actionDataType,
		SerializableDataBuilder<ConditionTypeFactory<T>> conditionDataType) {
		return getFactory(actionDataType, conditionDataType, t -> t);
	}
}
