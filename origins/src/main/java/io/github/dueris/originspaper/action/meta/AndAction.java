package io.github.dueris.originspaper.action.meta;

import io.github.dueris.calio.data.SerializableDataBuilder;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AndAction {

	public static <T> @NotNull ActionFactory<T> getFactory(SerializableDataBuilder<List<ActionFactory<T>>> listDataType) {
		return new ActionFactory<T>(OriginsPaper.apoliIdentifier("and"),
				SerializableData.serializableData()
						.add("actions", listDataType),
				(data, t) -> {
					List<ActionFactory<T>> actions = data.get("actions");
					actions.forEach(a -> a.accept(t));
				}
		);
	}
}
