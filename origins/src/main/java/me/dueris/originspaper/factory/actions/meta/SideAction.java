package me.dueris.originspaper.factory.actions.meta;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableDataBuilder;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.actions.ActionFactory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class SideAction {

	public static <T> void action(@NotNull DeserializedFactoryJson data, T t, @NotNull Function<T, Boolean> serverCheck) {
		ActionFactory<T> action = data.get("action");
		Side side = data.get("side");
		boolean isServer = serverCheck.apply(t);
		if ((side == Side.CLIENT) != isServer) {
			action.accept(t);
		}
	}

	@Contract("_, _ -> new")
	public static <T> @NotNull ActionFactory<T> getFactory(SerializableDataBuilder<ActionFactory<T>> dataType, Function<T, Boolean> serverCheck) {
		return new ActionFactory<T>(OriginsPaper.apoliIdentifier("side"),
			InstanceDefiner.instanceDefiner()
				.add("side", SerializableDataTypes.enumValue(Side.class))
				.add("action", dataType),
			(data, t) -> SideAction.action(data, t, serverCheck)
		);
	}

	public enum Side {
		CLIENT, SERVER
	}
}
