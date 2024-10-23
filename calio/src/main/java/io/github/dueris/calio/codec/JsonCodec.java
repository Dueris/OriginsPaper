package io.github.dueris.calio.codec;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class JsonCodec<T> implements StrictCodec<T> {

	private final Function<JsonElement, T> fromJson;
	private final Function<T, JsonElement> toJson;

	public JsonCodec(Function<JsonElement, T> fromJson, Function<T, JsonElement> toJson) {
		this.fromJson = fromJson;
		this.toJson = toJson;
	}

	@Override
	public <I> Pair<T, I> strictDecode(@NotNull DynamicOps<I> ops, I input) {

		JsonElement jsonElement = ops.convertTo(JsonOps.INSTANCE, input);
		T output = fromJson.apply(jsonElement);

		return Pair.of(output, input);

	}

	@Override
	public <I> I strictEncode(T input, DynamicOps<I> ops, I prefix) {
		return JsonOps.INSTANCE.convertTo(ops, toJson.apply(input));
	}

}
