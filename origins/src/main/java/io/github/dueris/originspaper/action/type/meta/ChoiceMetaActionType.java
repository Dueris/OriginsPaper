package io.github.dueris.originspaper.action.type.meta;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.AbstractAction;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.AbstractActionType;
import io.github.dueris.originspaper.util.context.TypeActionContext;
import net.minecraft.world.entity.ai.behavior.ShufflingList;

import java.util.function.Function;

public interface ChoiceMetaActionType<T extends TypeActionContext<?>, A extends AbstractAction<T, ? extends AbstractActionType<T, A>>> {

	static <T extends TypeActionContext<?>, A extends AbstractAction<T, AT>, AT extends AbstractActionType<T, A>, M extends AbstractActionType<T, A> & ChoiceMetaActionType<T, A>> ActionConfiguration<M> createConfiguration(SerializableDataType<A> actionDataType, Function<ShufflingList<A>, M> constructor) {
		return ActionConfiguration.of(
			OriginsPaper.apoliIdentifier("choice"),
			new SerializableData()
				.add("actions", SerializableDataType.weightedList(actionDataType)),
			data -> constructor.apply(
				data.get("actions")
			),
			(m, serializableData) -> serializableData.instance()
				.set("actions", m.actions())
		);
	}

	ShufflingList<A> actions();

	default void executeActions(T context) {

		actions().shuffle();

		for (A a : actions()) {
			a.accept(context);
		}

	}

}
