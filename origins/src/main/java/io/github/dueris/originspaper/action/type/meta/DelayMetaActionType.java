package io.github.dueris.originspaper.action.type.meta;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.AbstractAction;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.AbstractActionType;
import io.github.dueris.originspaper.util.Scheduler;
import io.github.dueris.originspaper.util.context.TypeActionContext;

import java.util.function.BiFunction;

public interface DelayMetaActionType<T extends TypeActionContext<?>, A extends AbstractAction<T, ? extends AbstractActionType<T, A>>> {

	Scheduler SCHEDULER = new Scheduler();

	static <T extends TypeActionContext<?>, A extends AbstractAction<T, AT>, AT extends AbstractActionType<T, A>, M extends AbstractActionType<T, A> & DelayMetaActionType<T, A>> ActionConfiguration<M> createConfiguration(SerializableDataType<A> actionDataType, BiFunction<A, Integer, M> constructor) {
		return ActionConfiguration.of(
			OriginsPaper.apoliIdentifier("delay"),
			new SerializableData()
				.add("action", actionDataType)
				.add("ticks", SerializableDataTypes.POSITIVE_INT),
			data -> constructor.apply(
				data.get("action"),
				data.get("ticks")
			),
			(m, serializableData) -> serializableData.instance()
				.set("action", m.action())
				.set("ticks", m.ticks())
		);
	}

	A action();

	int ticks();

	default void executeAction(T context) {
		SCHEDULER.queue(server -> action().accept(context), ticks());
	}

}
