package io.github.dueris.originspaper.action.type.meta;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.AbstractAction;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.AbstractActionType;
import io.github.dueris.originspaper.condition.AbstractCondition;
import io.github.dueris.originspaper.condition.type.AbstractConditionType;
import io.github.dueris.originspaper.util.context.TypeActionContext;
import io.github.dueris.originspaper.util.context.TypeConditionContext;

import java.util.List;
import java.util.function.Function;

public interface IfElseListMetaActionType<AX extends TypeActionContext<CX>, CX extends TypeConditionContext, A extends AbstractAction<AX, ?>, C extends AbstractCondition<CX, ?>> {

	static <AX extends TypeActionContext<CX>, CX extends TypeConditionContext, A extends AbstractAction<AX, AT>, AT extends AbstractActionType<AX, A>, C extends AbstractCondition<CX, CT>, CT extends AbstractConditionType<CX, C>, M extends AbstractActionType<AX, A> & IfElseListMetaActionType<AX, CX, A, C>> ActionConfiguration<M> createConfiguration(SerializableDataType<A> actionDataType, SerializableDataType<C> conditionDataType, Function<List<ConditionedAction<A, C>>, M> constructor) {

		SerializableDataType<ConditionedAction<A, C>> conditionedActionDataType = SerializableDataType.compound(
			new SerializableData()
				.add("action", actionDataType)
				.add("condition", conditionDataType),
			data -> new ConditionedAction<>(
				data.get("action"),
				data.get("condition")
			),
			(conditionedAction, serializableData) -> serializableData.instance()
				.set("action", conditionedAction.action())
				.set("condition", conditionedAction.condition())
		);

		return ActionConfiguration.of(
			OriginsPaper.apoliIdentifier("if_else_list"),
			new SerializableData()
				.add("actions", conditionedActionDataType.list()),
			data -> constructor.apply(
				data.get("actions")
			),
			(m, serializableData) -> serializableData.instance()
				.set("actions", m.conditionedActions())
		);

	}

	List<ConditionedAction<A, C>> conditionedActions();

	default void executeActions(AX actionContext) {

		CX convertedContext = actionContext.forCondition();

		for (ConditionedAction<A, C> conditionedAction : conditionedActions()) {

			if (conditionedAction.condition().test(convertedContext)) {
				conditionedAction.action().accept(actionContext);
				break;
			}

		}

	}

	record ConditionedAction<A extends AbstractAction<?, ?>, C extends AbstractCondition<?, ?>>(A action, C condition) {

	}

}
