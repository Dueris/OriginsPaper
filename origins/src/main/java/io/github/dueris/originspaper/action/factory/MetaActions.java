package io.github.dueris.originspaper.action.factory;

import io.github.dueris.calio.data.SerializableDataBuilder;
import io.github.dueris.originspaper.action.type.meta.*;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;

import java.util.function.Consumer;
import java.util.function.Function;

public class MetaActions {

	public static <T, U> void register(SerializableDataBuilder<ActionTypeFactory<T>> actionDataType, SerializableDataBuilder<ConditionTypeFactory<U>> conditionDataType, Function<T, U> actionToCondition, Consumer<ActionTypeFactory<T>> registrant) {
		registrant.accept(AndActionType.getFactory(SerializableDataBuilder.of(actionDataType.listOf())));
		registrant.accept(ChanceActionType.getFactory(actionDataType));
		registrant.accept(IfElseActionType.getFactory(actionDataType, conditionDataType, actionToCondition));
		registrant.accept(ChoiceActionType.getFactory(actionDataType));
		registrant.accept(IfElseListActionType.getFactory(actionDataType, conditionDataType, actionToCondition));
		registrant.accept(DelayActionType.getFactory(actionDataType));
		registrant.accept(SideActionType.getFactory(actionDataType));
		registrant.accept(NothingActionType.getFactory());
	}

}
