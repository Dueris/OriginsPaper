package io.github.dueris.originspaper.action.type.meta;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataBuilder;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.util.ApoliScheduler;

import java.util.function.Consumer;

public class DelayActionType {

	private static final ApoliScheduler SCHEDULER = new ApoliScheduler();

	public static <T> void action(T type, Consumer<T> action, int ticks) {
		SCHEDULER.queue(server -> action.accept(type), ticks);
	}

	public static <T> ActionTypeFactory<T> getFactory(SerializableDataBuilder<ActionTypeFactory<T>> dataType) {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("delay"),
			new SerializableData()
				.add("action", dataType)
				.add("ticks", SerializableDataTypes.INT),
			(data, type) -> action(type,
				data.get("action"),
				data.get("ticks")
			)
		);
	}

}
