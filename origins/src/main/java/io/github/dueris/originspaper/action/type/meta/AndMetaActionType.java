package io.github.dueris.originspaper.action.type.meta;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.AbstractAction;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.AbstractActionType;
import io.github.dueris.originspaper.util.context.TypeActionContext;

import java.util.List;
import java.util.function.Function;

public interface AndMetaActionType<T extends TypeActionContext<?>, A extends AbstractAction<T, ? extends AbstractActionType<T, A>>> {

	static <T extends TypeActionContext<?>, A extends AbstractAction<T, AT>, AT extends AbstractActionType<T, A>, M extends AbstractActionType<T, A> & AndMetaActionType<T, A>> ActionConfiguration<M> createConfiguration(SerializableDataType<A> actionDataType, Function<List<A>, M> constructor) {
		return ActionConfiguration.of(
			OriginsPaper.apoliIdentifier("and"),
			new SerializableData()
				.add("actions", actionDataType.list()),
			data -> constructor.apply(
				data.get("actions")
			),
			(m, serializableData) -> serializableData.instance()
				.set("actions", m.actions())
		);
	}

	List<A> actions();

	default void executeActions(T context) {
		actions().forEach(action -> action.accept(context));
	}

}
