package io.github.dueris.originspaper.action.meta;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableDataBuilder;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class SideAction {

	public static <T> void action(@NotNull SerializableData.Instance data, T t, @NotNull Function<T, Boolean> serverCheck) {
		ActionFactory<T> action = data.get("action");
		Side side = data.get("side");
		boolean isServer = serverCheck.apply(t);
		if ((side == Side.CLIENT) != isServer) {
			action.accept(t);
		}
	}

	public static <T> @NotNull ActionFactory<T> getFactory(SerializableDataBuilder<ActionFactory<T>> dataType, Function<T, Boolean> serverCheck) {
		return new ActionFactory<T>(OriginsPaper.apoliIdentifier("side"),
			SerializableData.serializableData()
				.add("side", SerializableDataTypes.enumValue(Side.class))
				.add("action", dataType),
			(data, t) -> SideAction.action(data, t, serverCheck)
		);
	}

	public enum Side {
		CLIENT, SERVER
	}
}
