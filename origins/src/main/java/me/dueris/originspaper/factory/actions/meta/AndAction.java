package me.dueris.originspaper.factory.actions.meta;

import io.github.dueris.calio.data.SerializableDataBuilder;
import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.actions.ActionFactory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AndAction {

	@Contract("_ -> new")
	public static <T> @NotNull ActionFactory<T> getFactory(SerializableDataBuilder<List<ActionFactory<T>>> listDataType) {
		return new ActionFactory<T>(OriginsPaper.apoliIdentifier("and"),
			InstanceDefiner.instanceDefiner()
				.add("actions", listDataType),
			(data, t) -> {
				List<ActionFactory<T>> actions = data.get("actions");
				actions.forEach(a -> a.accept(t));
			}
		);
	}
}
