package io.github.dueris.originspaper.action.type.meta;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import net.minecraft.util.RandomSource;

import java.util.function.Consumer;

public class ChanceActionType {

	public static <T> void action(T type, Consumer<T> successAction, Consumer<T> failAction, float chance) {

		if (RandomSource.create().nextFloat() < chance) {
			successAction.accept(type);
		} else {
			failAction.accept(type);
		}

	}

	public static <T> ActionTypeFactory<T> getFactory(SerializableDataType<ActionTypeFactory<T>.Instance> dataType) {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("chance"),
			new SerializableData()
				.add("success_action", dataType)
				.add("fail_action", dataType, null)
				.add("chance", SerializableDataType.boundNumber(SerializableDataTypes.FLOAT, 0F, 1F)),
			(data, type) -> action(type,
				data.get("success_action"),
				data.getOrElse("fail_action", t -> {
				}),
				data.get("chance")
			)
		);
	}
}
