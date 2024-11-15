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

import java.util.Optional;

public interface IfElseMetaActionType<AX extends TypeActionContext<CX>, CX extends TypeConditionContext, A extends AbstractAction<AX, ?>, C extends AbstractCondition<CX, ?>> {

	static <AX extends TypeActionContext<CX>, CX extends TypeConditionContext, A extends AbstractAction<AX, AT>, AT extends AbstractActionType<AX, A>, C extends AbstractCondition<CX, CT>, CT extends AbstractConditionType<CX, C>, M extends AbstractActionType<AX, A> & IfElseMetaActionType<AX, CX, A, C>> ActionConfiguration<M> createConfiguration(SerializableDataType<A> actionDataType, SerializableDataType<C> conditionDataType, Constructor<AX, CX, A, C, M> constructor) {
		return ActionConfiguration.of(
			OriginsPaper.apoliIdentifier("if_else"),
			new SerializableData()
				.add("condition", conditionDataType)
				.add("if_action", actionDataType)
				.add("else_action", actionDataType.optional(), Optional.empty()),
			data -> constructor.create(
				data.get("condition"),
				data.get("if_action"),
				data.get("else_action")
			),
			(m, serializableData) -> serializableData.instance()
				.set("condition", m.condition())
				.set("if_action", m.ifAction())
				.set("else_action", m.elseAction())
		);
	}

	C condition();

	A ifAction();

	Optional<A> elseAction();

	default void executeAction(AX actionContext) {

		if (condition().test(actionContext.forCondition())) {
			ifAction().accept(actionContext);
		} else {
			elseAction().ifPresent(action -> action.accept(actionContext));
		}

	}

	interface Constructor<AX extends TypeActionContext<CX>, CX extends TypeConditionContext, A extends AbstractAction<AX, ?>, C extends AbstractCondition<CX, ?>, M extends AbstractActionType<AX, ?> & IfElseMetaActionType<AX, CX, A, C>> {
		M create(C condition, A ifAction, Optional<A> elseAction);
	}

}
