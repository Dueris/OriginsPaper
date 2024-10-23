package io.github.dueris.originspaper.action.type.meta;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;

import java.util.function.Function;

public class SideActionType {

	public static <T> void action(T type, ActionTypeFactory<T>.Instance action, Side side, Function<T, Boolean> serverCheck) {

		if ((side == Side.CLIENT) != serverCheck.apply(type)) {
			action.accept(type);
		}

	}

	public static <T> ActionTypeFactory<T> getFactory(SerializableDataType<ActionTypeFactory<T>.Instance> dataType, Function<T, Boolean> serverCheck) {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("side"),
			new SerializableData()
				.add("action", dataType)
				.add("side", SerializableDataType.enumValue(Side.class)),
			(data, type) -> SideActionType.action(type,
				data.get("action"),
				data.get("side"),
				serverCheck
			)
		);
	}

	public static <T> ActionTypeFactory<T> getFactory(SerializableDataType<ActionTypeFactory<T>.Instance> dataType) {
		return getFactory(dataType, t -> true);
	}

	public enum Side {
		CLIENT, SERVER
	}

}
