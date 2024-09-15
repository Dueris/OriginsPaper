package io.github.dueris.originspaper.action.type.meta;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import net.minecraft.world.entity.ai.behavior.ShufflingList;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.function.Consumer;

public class ChoiceActionType {

	public static <T> void action(T type, @NotNull ShufflingList<Consumer<T>> actions) {

		actions.shuffle();
		Iterator<Consumer<T>> actionIterator = actions.iterator();

		if (actionIterator.hasNext()) {
			actionIterator.next().accept(type);
		}

	}

	public static <T> @NotNull ActionTypeFactory<T> getFactory(SerializableDataType<ActionTypeFactory<T>> dataType) {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("choice"),
			new SerializableData()
				.add("actions", SerializableDataTypes.weightedList(dataType)),
			(data, type) -> action(type,
				data.get("actions")
			)
		);
	}
}
