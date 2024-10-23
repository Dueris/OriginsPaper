package io.github.dueris.originspaper.action.type.meta;

import com.google.common.base.Suppliers;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

public class NothingActionType {

	public static <T> ActionTypeFactory<T> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("nothing"),
			new SerializableData(),
			(data, t) -> {
			}
		);
	}

	public static <T> Supplier<ActionTypeFactory<T>.Instance> create(Registry<ActionTypeFactory<T>> registry) {
		return Suppliers.memoize(() -> {

			ActionTypeFactory<T> nothingFactory = registry.getOrThrow(ResourceKey.create(registry.key(), OriginsPaper.apoliIdentifier("nothing")));
			SerializableData serializableData = nothingFactory.getSerializableData();

			return nothingFactory.fromData(serializableData.instance());

		});
	}

}
