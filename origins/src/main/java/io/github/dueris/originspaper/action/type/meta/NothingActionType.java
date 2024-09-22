package io.github.dueris.originspaper.action.type.meta;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import org.jetbrains.annotations.NotNull;

public class NothingActionType {

	public static <T> @NotNull ActionTypeFactory<T> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("nothing"),
			new SerializableData(),
			(data, t) -> {
			}
		);
	}

}
