package me.dueris.calio.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.dueris.calio.CraftCalio;
import me.dueris.calio.data.annotations.SourceProvider;
import me.dueris.calio.data.factory.FactoryElement;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.data.types.OptionalInstance;
import me.dueris.calio.data.types.RequiredInstance;
import me.dueris.calio.util.holders.Pair;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.rmi.NoSuchObjectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConstructorCreator {

	public static @Nullable FactoryHolder invoke(Constructor<? extends FactoryHolder> constructor, @NotNull FactoryData data, @NotNull Pair<JsonObject, ResourceLocation> pair) throws InvocationTargetException, InstantiationException, IllegalAccessException {
		JsonObject getter = pair.first();
		ResourceLocation tag = pair.second();
		List<Object> invoker = new ArrayList<>();

		for (FactoryDataDefiner provider : data.getProviders()) {
			if (getter.has(provider.getObjName())) {
				Object o = getOrCreate(provider.getType(), getter.get(provider.getObjName()));
				if (o == null) {
					CraftCalio.INSTANCE
						.getLogger()
						.severe(
							"Created value was null when creating factory data! Bug?: {a} | {b}"
								.replace("{a}", provider.getObjName())
								.replace("{b}", provider.getType().getSimpleName())
						);
					return null;
				}

				invoker.add(o);
			} else {
				if (provider.getDefaultValue() == null) {
					CraftCalio.INSTANCE
						.getLogger()
						.severe(
							"Provided default value was null when creating factory data! Please provide an instance of that type: {a} | {b}"
								.replace("{a}", provider.getObjName())
								.replace("{b}", provider.getType().getSimpleName())
						);
					return null;
				}

				if (provider.getDefaultValue() instanceof RequiredInstance) {
					throw new IllegalArgumentException(
						"Instance of \"{a}\" is required in registerable: {b}".replace("{a}", provider.getObjName()).replace("{b}", tag.toString())
					);
				}

				if (provider.getDefaultValue() instanceof OptionalInstance) {
					invoker.add(null);
				} else {
					invoker.add(
						provider.getDefaultValue() instanceof String
							? ((String) provider.getDefaultValue()).replace("$namespace", tag.getNamespace()).replace("$path", tag.getPath())
							: provider.getDefaultValue()
					);
				}
			}
		}

		if (constructor.getParameters()[constructor.getParameters().length - 1].getType().equals(JsonObject.class)) {
			invoker.add(getter);
		}

		FactoryHolder created = constructor.newInstance(invoker.toArray(new Object[0]));

		try {
			setAnnotatedField(created, getter);
		} catch (NoSuchObjectException var11) {
		}

		return created;
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
		} else {
			if (ofType.equals(FactoryElement.class)) {
				return new FactoryElement(provided);
			}

			if (ofType.equals(FactoryJsonArray.class)) {
				if (provided.isJsonArray()) {
					return new FactoryJsonArray(provided.getAsJsonArray());
				}

				if (provided.isJsonObject()) {
					JsonArray array = new JsonArray();
					array.add(provided.getAsJsonObject());
					return new FactoryJsonArray(array);
				}
			} else {
				if (ofType.equals(Optional.class)) {
					return Optional.of(provided);
				}

				if (CalioDataTypes.test(ofType, provided) != null) {
					return CalioDataTypes.test(ofType, provided);
				}
			}
		}

		CraftCalio.INSTANCE.getLogger().severe("Unable to create instance {a}! Bug?".replace("{a}", ofType.getSimpleName()));
		return null;
	}

	private static boolean isNumber(@NotNull JsonElement element) {
		return element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber();
	}

	public static void setAnnotatedField(@NotNull Object target, Object value) throws NoSuchObjectException {
		Class<?> clazz = target.getClass();
		Field field = findAnnotatedField(clazz, SourceProvider.class);
		if (field != null) {
			field.setAccessible(true);

			try {
				field.set(target, value);
			} catch (IllegalAccessException var5) {
				throw new NoSuchObjectException("No such element!");
			}
		}
	}

	private static Field findAnnotatedField(Class<?> clazz, Class<? extends Annotation> annotationClass) {
		while (clazz != null) {
			for (Field field : clazz.getDeclaredFields()) {
				if (field.isAnnotationPresent(annotationClass)) {
					return field;
				}
			}

			clazz = clazz.getSuperclass();
		}

		return null;
	}
}
