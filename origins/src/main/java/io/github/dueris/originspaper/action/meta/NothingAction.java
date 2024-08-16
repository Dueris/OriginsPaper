package io.github.dueris.originspaper.action.meta;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import org.jetbrains.annotations.NotNull;

public class NothingAction {

	public static <T> @NotNull ActionFactory<T> getFactory() {
		return new ActionFactory<T>(OriginsPaper.apoliIdentifier("nothing"),
				SerializableData.serializableData(),
				(inst, t) -> {
				}
		);
	}
}
