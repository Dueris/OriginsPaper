package io.github.dueris.originspaper.action.type.meta;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class AndActionType {

	public static <T> void action(T type, @NotNull Collection<Consumer<T>> actions) {
		actions.forEach(action -> action.accept(type));
	}

	public static <T> @NotNull ActionTypeFactory<T> getFactory(SerializableDataType<List<ActionTypeFactory<T>>> listDataType) {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("and"),
			new SerializableData()
				.add("actions", listDataType),
			(data, type) -> action(type,
				data.get("actions")
			)
		);
	}
}
