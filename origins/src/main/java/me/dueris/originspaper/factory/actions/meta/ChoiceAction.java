package me.dueris.originspaper.factory.actions.meta;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableDataBuilder;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.util.FilterableWeightedList;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.actions.ActionFactory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class ChoiceAction {

	@Contract("_ -> new")
	public static <T> @NotNull ActionFactory<T> getFactory(SerializableDataBuilder<ActionFactory<T>> dataType) {
		return new ActionFactory<T>(OriginsPaper.apoliIdentifier("choice"),
			InstanceDefiner.instanceDefiner()
				.add("actions", SerializableDataTypes.weightedList(dataType)),
			(data, t) -> {
				FilterableWeightedList<ActionFactory<T>> actionList = data.get("actions");
				ActionFactory<T> action = actionList.pickRandom(new Random());
				action.accept(t);
			}
		);
	}
}
