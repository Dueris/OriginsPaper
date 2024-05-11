package me.dueris.calio.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.dueris.calio.CraftCalio;
import me.dueris.calio.data.annotations.Register;
import me.dueris.calio.data.factory.FactoryElement;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import org.bukkit.NamespacedKey;
import oshi.util.tuples.Pair;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ConstructorCreator {
	public static FactoryHolder invoke(Constructor<? extends FactoryHolder> constructor, FactoryData data, Pair<JsonObject, NamespacedKey> pair) throws InvocationTargetException, InstantiationException, IllegalAccessException {
		JsonObject getter = pair.getA();
		NamespacedKey tag = pair.getB();
		List<Object> invoker = new ArrayList<>();
		if (!constructor.isAnnotationPresent(Register.class)) {
			CraftCalio.INSTANCE.getLogger().severe("@Register annotation must be present in constructor annotation : " + tag.asString());
			return null;
		}
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
		if (constructor.getParameters()[constructor.getParameters().length - 1].getType().equals(JsonObject.class)) {
			invoker.add(getter);
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
		} else if (CalioDataTypes.test(ofType, provided) != null) {
			return CalioDataTypes.test(ofType, provided);
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