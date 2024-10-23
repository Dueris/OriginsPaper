package io.github.dueris.originspaper.action.type.meta;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import net.minecraft.world.entity.ai.behavior.ShufflingList;

import java.util.Iterator;
import java.util.function.Consumer;

public class ChoiceActionType {

	public static <T> void action(T type, ShufflingList<Consumer<T>> actions) {

		actions.shuffle();
		Iterator<Consumer<T>> actionIterator = actions.iterator();

		if (actionIterator.hasNext()) {
			actionIterator.next().accept(type);
		}

	}

	public static <T> ActionTypeFactory<T> getFactory(SerializableDataType<ActionTypeFactory<T>.Instance> dataType) {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("choice"),
			new SerializableData()
				.add("actions", SerializableDataType.weightedList(dataType)),
			(data, type) -> action(type,
				data.get("actions")
			)
		);
	}
}
