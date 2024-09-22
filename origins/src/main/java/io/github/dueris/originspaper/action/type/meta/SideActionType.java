package io.github.dueris.originspaper.action.type.meta;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class SideActionType {

	public static <T> void action(T type, ActionTypeFactory<T> action, Side side, @NotNull Function<T, Boolean> serverCheck) {

		if ((side == Side.CLIENT) != serverCheck.apply(type)) {
			action.accept(type);
		}

	}

	public static <T> @NotNull ActionTypeFactory<T> getFactory(SerializableDataType<ActionTypeFactory<T>> dataType, Function<T, Boolean> serverCheck) {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("side"),
			new SerializableData()
				.add("action", dataType)
				.add("side", SerializableDataTypes.enumValue(Side.class)),
			(data, type) -> SideActionType.action(type,
				data.get("action"),
				data.get("side"),
				serverCheck
			)
		);
	}

	public static <T> @NotNull ActionTypeFactory<T> getFactory(SerializableDataType<ActionTypeFactory<T>> dataType) {
		return getFactory(dataType, t -> true);
	}

	public enum Side {
		CLIENT, SERVER
	}

}
