package io.github.dueris.originspaper.action.meta;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionTypeFactory;
import org.jetbrains.annotations.NotNull;

public class NothingAction {

	public static <T> @NotNull ActionTypeFactory<T> getFactory() {
		return new ActionTypeFactory<T>(OriginsPaper.apoliIdentifier("nothing"),
			SerializableData.serializableData(),
			(inst, t) -> {
			}
		);
	}
}
