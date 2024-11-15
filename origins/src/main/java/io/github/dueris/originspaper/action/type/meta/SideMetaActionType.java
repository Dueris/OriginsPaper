package io.github.dueris.originspaper.action.type.meta;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.AbstractAction;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.AbstractActionType;
import io.github.dueris.originspaper.util.context.TypeActionContext;

import java.util.function.BiFunction;

public interface SideMetaActionType<T extends TypeActionContext<?>, A extends AbstractAction<T, ?>> {

	static <T extends TypeActionContext<?>, A extends AbstractAction<T, AT>, AT extends AbstractActionType<T, A>, M extends AbstractActionType<T, A> & SideMetaActionType<T, A>> ActionConfiguration<M> createConfiguration(SerializableDataType<A> actionDataType, BiFunction<A, Side, M> constructor) {
		return ActionConfiguration.of(
			OriginsPaper.apoliIdentifier("side"),
			new SerializableData()
				.add("action", actionDataType)
				.add("side", SerializableDataType.enumValue(Side.class)),
			data -> constructor.apply(
				data.get("action"),
				data.get("side")
			),
			(m, serializableData) -> serializableData.instance()
				.set("action", m.action())
				.set("side", m.side())
		);
	}

	A action();

	Side side();

	default void executeAction(T context) {

		if (!((side() == Side.CLIENT))) {
			action().accept(context);
		}

	}

	enum Side {
		CLIENT, SERVER
	}

}
