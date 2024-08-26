package io.github.dueris.originspaper.action.meta;

import io.github.dueris.calio.data.SerializableDataBuilder;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionTypeFactory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AndAction {

	public static <T> @NotNull ActionTypeFactory<T> getFactory(SerializableDataBuilder<List<ActionTypeFactory<T>>> listDataType) {
		return new ActionTypeFactory<T>(OriginsPaper.apoliIdentifier("and"),
			SerializableData.serializableData()
				.add("actions", listDataType),
			(data, t) -> {
				List<ActionTypeFactory<T>> actions = data.get("actions");
				actions.forEach(a -> a.accept(t));
			}
		);
	}
}
