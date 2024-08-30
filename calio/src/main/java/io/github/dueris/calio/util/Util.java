package io.github.dueris.calio.util;

import io.github.dueris.calio.data.DataBuildDirective;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataBuilder;
import io.github.dueris.calio.util.holder.ObjectTiedEnumState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Util {
	public static boolean pathMatchesAccessor(@NotNull String path, DataBuildDirective<?> dataBuildDirective) {
		if (!path.contains("data/")) return false;
		if (!dataBuildDirective.modids().isEmpty()) {
			try {
				String[] parts = path.split("/");
				if (parts.length < 4) return false;
				String modid = parts[2];
				String typeFolder = parts[3];
				return dataBuildDirective.folder().equalsIgnoreCase(typeFolder) && dataBuildDirective.modids().contains(modid);
			} catch (ArrayIndexOutOfBoundsException e) {
				return false;
			}
		} else {
			try {
				String[] parts = path.split("/");
				if (parts.length < 3) return false;
				String typeFolder = parts[2];
				return dataBuildDirective.folder().equalsIgnoreCase(typeFolder);
			} catch (ArrayIndexOutOfBoundsException e) {
				return false;
			}
		}
	}

	public static @Nullable ResourceLocation buildResourceLocationFromPath(@NotNull String path, DataBuildDirective<?> dataBuildDirective) {
		if (!path.contains("data/")) return null;
		if (!dataBuildDirective.modids().isEmpty()) {
			try {
				String[] parts = path.split("/");
				if (parts.length < 4) return null;

				StringBuilder nameBuilder = new StringBuilder(parts[4].replace(".json", ""));
				for (int i = 5; i < parts.length; i++) {
					nameBuilder.append('/').append(parts[i].replace(".json", ""));
				}
				String name = nameBuilder.toString();
				String resourceString = parts[1] + ":" + name;
				return ResourceLocation.read(resourceString).getOrThrow();
			} catch (Exception e) {
				return null;
			}
		} else {
			try {
				String[] parts = path.split("/");
				if (parts.length < 3) return null;

				StringBuilder nameBuilder = new StringBuilder(parts[3].replace(".json", ""));
				for (int i = 5; i < parts.length; i++) {
					nameBuilder.append('/').append(parts[i].replace(".json", ""));
				}
				String name = nameBuilder.toString();
				String resourceString = parts[1] + ":" + name;
				return ResourceLocation.read(resourceString).getOrThrow();
			} catch (Exception e) {
				return null;
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> Class<T> castClass(Class<?> aClass) {
		return (Class<T>) aClass;
	}

	public static <T> @NotNull Constructor<T> generateConstructor(Class<T> targetClass, @NotNull SerializableData serializableData) throws NoSuchMethodException, IllegalAccessException {
		List<Class<?>> paramTypes = new LinkedList<>();
		paramTypes.add(ResourceLocation.class);

		for (Map.Entry<String, ObjectTiedEnumState<SerializableDataBuilder<?>>> entry : serializableData.dataMap().entrySet()) {
			Class<?> paramType = entry.getValue().object().type();
			paramTypes.add(getPrimitiveType(paramType));
		}

		Class<?>[] paramArray = paramTypes.toArray(new Class<?>[0]);
		Constructor<T> constructor = targetClass.getConstructor(paramArray);

		if (Modifier.isPrivate(constructor.getModifiers())) {
			throw new IllegalAccessException("Cannot access private constructor");
		}

		return constructor;
	}

	public static <T> @NotNull T instantiate(@NotNull Constructor<T> constructor, ResourceLocation resourceLocation, SerializableData.@NotNull Instance instance) throws InstantiationException, IllegalAccessException, InvocationTargetException {
		Object[] params = new Object[constructor.getParameterCount()];
		params[0] = resourceLocation;

		int i = 1;
		for (String key : instance.data().keySet()) {
			Object value = instance.get(key);
			Class<?> paramType = constructor.getParameterTypes()[i];

			if (value == null) {
				if (paramType.isPrimitive()) {
					value = getDefaultValue(paramType);
				}
			}

			params[i++] = value;
		}

		return constructor.newInstance(params);
	}

	private static Object getDefaultValue(Class<?> type) {
		if (type == boolean.class) return false;
		if (type == char.class) return '\u0000';
		if (type == byte.class) return (byte) 0;
		if (type == short.class) return (short) 0;
		if (type == int.class) return 0;
		if (type == long.class) return 0L;
		if (type == float.class) return 0f;
		if (type == double.class) return 0d;
		throw new IllegalArgumentException("Type " + type + " is not a supported primitive type.");
	}

	private static Class<?> getPrimitiveType(Class<?> clazz) {
		if (clazz == Integer.class) return int.class;
		if (clazz == Boolean.class) return boolean.class;
		if (clazz == Float.class) return float.class;
		if (clazz == Double.class) return double.class;
		if (clazz == Long.class) return long.class;
		if (clazz == Byte.class) return byte.class;
		if (clazz == Short.class) return short.class;
		if (clazz == Character.class) return char.class;
		return clazz;
	}
}
