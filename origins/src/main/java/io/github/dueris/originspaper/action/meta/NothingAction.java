package io.github.dueris.originspaper.action.meta;

import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import org.jetbrains.annotations.NotNull;

public class NothingAction {

	public static <T> @NotNull ActionFactory<T> getFactory() {
		return new ActionFactory<T>(OriginsPaper.apoliIdentifier("nothing"),
			InstanceDefiner.instanceDefiner(),
			(inst, t) -> {
			}
		);
	}
}
