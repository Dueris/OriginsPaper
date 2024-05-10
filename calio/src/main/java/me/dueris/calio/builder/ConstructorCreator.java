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

public class ConstructorCreator {
    public static FactoryHolder invoke(Constructor<? extends FactoryHolder> constructor, FactoryData data, JsonObject getter) throws InvocationTargetException, InstantiationException, IllegalAccessException {
		List<Object> invoker = new ArrayList<>();
		for (FactoryDataDefiner provider : data.getProviders()) {
			if (getter.has(provider.getObjName())) {
				Object o = getOrCreate(provider.getType(), getter.get(provider.getObjName()));
				if (o != null) {
					invoker.add(o);
				} else {
					CraftCalio.INSTANCE.getLogger().severe("Created value was null when creating factory data! Bug?: {a} | {b}"
						.replace("{a}", provider.getObjName())
						.replace("{b}", provider.getType().getSimpleName())
					);
				}
			} else if (provider.getDefaultValue() != null) {
				invoker.add(provider.getDefaultValue());
			} else {
				CraftCalio.INSTANCE.getLogger().severe("Provided default value was null when creating factory data! Please provide an instance of that type: {a} | {b}"
					.replace("{a}", provider.getObjName())
					.replace("{b}", provider.getType().getSimpleName())
				);
				return null;
			}
		}
		return constructor.newInstance(invoker.toArray(new Object[0]));
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
			if (provided.isJsonPrimitive()) {
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