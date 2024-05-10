package me.dueris.calio.builder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.dueris.calio.CraftCalio;
import me.dueris.calio.builder.inst.FactoryData;
import me.dueris.calio.builder.inst.FactoryDataDefiner;
import me.dueris.calio.builder.inst.FactoryHolder;
import me.dueris.calio.builder.inst.factory.FactoryElement;
import me.dueris.calio.builder.inst.factory.FactoryJsonArray;
import me.dueris.calio.builder.inst.factory.FactoryJsonObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConstructorCreator {
    public static FactoryHolder invoke(Constructor<? extends FactoryHolder> constructor, FactoryData data, JsonObject getter) throws InvocationTargetException, InstantiationException, IllegalAccessException {
		List<Object> invoker = new ArrayList<>();
		for (FactoryDataDefiner provider : data.getProviders()) {
			if (getter.has(provider.getObjName())) {
				System.out.println(getOrCreate(provider.getType(), getter));
				Object o = getOrCreate(provider.getType(), getter);
				if (o != null) {
					invoker.add(o);
				} else {
					CraftCalio.INSTANCE.getLogger().severe("Created value was null when creating factory data! Bug?: {a} | {b} | {c}"
						.replace("{a}", provider.getObjName())
						.replace("{b}", provider.getType().getSimpleName())
					);
				}
			} else if (provider.getDefaultValue() != null) {
				System.out.println(provider.getDefaultValue());
				invoker.add(provider.getDefaultValue());
			} else {
				System.out.println("b");
				CraftCalio.INSTANCE.getLogger().severe("Provided default value was null when creating factory data! Please provide an instance of that type: {a} | {b}"
					.replace("{a}", provider.getObjName())
					.replace("{b}", provider.getType().getSimpleName())
				);
			}
		}
		return constructor.newInstance(invoker.stream().filter(Objects::nonNull));
	}

	private static Object getOrCreate(Class<?> ofType, JsonElement provided) {
		if (ofType.equals(boolean.class)) {
			if (provided.isJsonPrimitive() && provided.getAsJsonPrimitive().isBoolean()) {
				return provided.getAsJsonPrimitive().getAsBoolean();
			}
		} else if (ofType.equals(int.class)) {
			if (isNumber(provided)) {
				return provided.getAsJsonPrimitive().getAsNumber().intValue();
			}
		} else if (ofType.equals(short.class)) {
			if (isNumber(provided)) {
				return provided.getAsJsonPrimitive().getAsNumber().shortValue();
			}
		} else if (ofType.equals(float.class)) {
			if (isNumber(provided)) {
				return provided.getAsJsonPrimitive().getAsNumber().floatValue();
			}
		} else if (ofType.equals(double.class)) {
			if (isNumber(provided)) {
				return provided.getAsJsonPrimitive().getAsNumber().doubleValue();
			}
		} else if (ofType.equals(long.class)) {
			if (isNumber(provided)) {
				return provided.getAsJsonPrimitive().getAsNumber().longValue();
			}
		} else if (ofType.equals(String.class)) {
			if (provided.isJsonPrimitive() && provided.getAsJsonPrimitive().isString()) {
				return provided.getAsJsonPrimitive().getAsString();
			}
		} else if (ofType.equals(FactoryJsonObject.class)) {
			if (provided.isJsonObject()) {
				return new FactoryJsonObject(provided.getAsJsonObject());
			}
		} else if (ofType.equals(FactoryElement.class)) {
			return new FactoryElement(provided);
		} else if (ofType.equals(FactoryJsonArray.class)) {
			if (provided.isJsonArray()) {
				return new FactoryJsonArray(provided.getAsJsonArray());
			}
		}
		CraftCalio.INSTANCE.getLogger().severe("Unable to create instance {a}! Bug?"
			.replace("{a}", ofType.getSimpleName())
		);
		return null;
	}

	private static boolean isNumber(JsonElement element) {
		return element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber();
	}
}