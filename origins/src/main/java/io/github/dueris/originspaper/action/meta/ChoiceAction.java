package io.github.dueris.originspaper.action.meta;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableDataBuilder;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.calio.util.FilterableWeightedList;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionTypeFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class ChoiceAction {

	public static <T> @NotNull ActionTypeFactory<T> getFactory(SerializableDataBuilder<ActionTypeFactory<T>> dataType) {
		return new ActionTypeFactory<T>(OriginsPaper.apoliIdentifier("choice"),
			SerializableData.serializableData()
				.add("actions", SerializableDataTypes.weightedList(dataType)),
			(data, t) -> {
				FilterableWeightedList<ActionTypeFactory<T>> actionList = data.get("actions");
				ActionTypeFactory<T> action = actionList.pickRandom(new Random());
				action.accept(t);
			}
		);
	}
}
