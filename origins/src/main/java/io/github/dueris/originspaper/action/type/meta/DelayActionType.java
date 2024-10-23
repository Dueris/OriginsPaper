package io.github.dueris.originspaper.action.type.meta;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.util.Scheduler;

import java.util.function.Consumer;

public class DelayActionType {

	private static final Scheduler SCHEDULER = new Scheduler();

	public static <T> void action(T type, Consumer<T> action, int ticks) {
		SCHEDULER.queue(server -> action.accept(type), ticks);
	}

	public static <T> ActionTypeFactory<T> getFactory(SerializableDataType<ActionTypeFactory<T>.Instance> dataType) {
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
