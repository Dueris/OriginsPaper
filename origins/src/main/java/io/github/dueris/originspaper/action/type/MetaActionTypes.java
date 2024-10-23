package io.github.dueris.originspaper.action.type;

import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.action.type.meta.*;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;

import java.util.function.Consumer;
import java.util.function.Function;

public class MetaActionTypes {

	public static <T, U> void register(SerializableDataType<ActionTypeFactory<T>.Instance> actionDataType, SerializableDataType<ConditionTypeFactory<U>.Instance> conditionDataType, Function<T, U> actionToCondition, Consumer<ActionTypeFactory<T>> registrant) {
		registrant.accept(AndActionType.getFactory(actionDataType.list()));
		registrant.accept(ChanceActionType.getFactory(actionDataType));
		registrant.accept(IfElseActionType.getFactory(actionDataType, conditionDataType, actionToCondition));
		registrant.accept(ChoiceActionType.getFactory(actionDataType));
		registrant.accept(IfElseListActionType.getFactory(actionDataType, conditionDataType, actionToCondition));
		registrant.accept(DelayActionType.getFactory(actionDataType));
		registrant.accept(SideActionType.getFactory(actionDataType));
		registrant.accept(NothingActionType.getFactory());
	}

}
