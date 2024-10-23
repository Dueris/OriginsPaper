package io.github.dueris.originspaper.condition.type.meta;

import com.google.common.base.Suppliers;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

public class ConstantConditionType {

	public static boolean condition(boolean value) {
		return value;
	}

	public static <T> ConditionTypeFactory<T> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("constant"),
			new SerializableData()
				.add("value", SerializableDataTypes.BOOLEAN),
			(data, type) -> condition(
				data.get("value")
			)
		);
	}

	public static <T> Supplier<ConditionTypeFactory<T>.Instance> create(Registry<ConditionTypeFactory<T>> registry, boolean value) {
		return Suppliers.memoize(() -> {

			ConditionTypeFactory<T> constantFactory = registry.getOrThrow(ResourceKey.create(registry.key(), OriginsPaper.apoliIdentifier("constant")));
			SerializableData serializableData = constantFactory.getSerializableData();

			return constantFactory.fromData(serializableData.instance().set("value", value));

		});
	}

}
