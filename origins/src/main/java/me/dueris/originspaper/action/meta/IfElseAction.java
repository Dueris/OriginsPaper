package me.dueris.originspaper.action.meta;

import io.github.dueris.calio.data.SerializableDataBuilder;
import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.action.ActionFactory;
import me.dueris.originspaper.condition.ConditionFactory;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class IfElseAction {

	public static <T, U> @NotNull ActionFactory<T> getFactory(
		SerializableDataBuilder<ActionFactory<T>> actionDataType,
		SerializableDataBuilder<ConditionFactory<U>> conditionDataType,
		Function<T, U> actionToConditionTypeFunction) {
		return new ActionFactory<T>(OriginsPaper.apoliIdentifier("if_else"),
			InstanceDefiner.instanceDefiner()
				.add("condition", conditionDataType)
				.add("if_action", actionDataType)
				.add("else_action", actionDataType, null),
			(data, t) -> {
				ConditionFactory<U> condition = data.get("condition");
				ActionFactory<T> ifAction = data.get("if_action");
				U u = actionToConditionTypeFunction.apply(t);
				if (condition.test(u)) {
					ifAction.accept(t);
				} else if (data.isPresent("else_action")) {
					ActionFactory<T> elseAction = data.get("else_action");
					elseAction.accept(t);
				}
			}
		);
	}

	public static <T> @NotNull ActionFactory<T> getFactory(
		SerializableDataBuilder<ActionFactory<T>> actionDataType,
		SerializableDataBuilder<ConditionFactory<T>> conditionDataType) {
		return getFactory(actionDataType, conditionDataType, t -> t);
	}
}
