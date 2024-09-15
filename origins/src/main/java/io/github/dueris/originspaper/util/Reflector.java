package io.github.dueris.originspaper.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

public class Reflector {
	public static Optional<Class<?>> getClass(String name) {
		try {
			return Optional.of(Class.forName(name));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}

	@SuppressWarnings("deprecation")
	public static @Nullable Method accessMethod(String name, @NotNull Class<?> sourceClass, Class<?>... paramTypes) {
		try {
			Method method = sourceClass.getDeclaredMethod(name, paramTypes);
			if (!method.isAccessible()) {
				method.setAccessible(true);
			}
			return method;
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("deprecation")
	public static @Nullable Method accessMethod(String name, @NotNull Class<?> sourceClass) {
		try {
			Method method = sourceClass.getDeclaredMethod(name);
			if (!method.isAccessible()) {
				method.setAccessible(true);
			}
			return method;
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static @Nullable Object accessMethod$Invoke(String name, Class<?> sourceClass, Object invoker, Class<?>[] paramTypes, Object... args) {
		try {
			Method method = accessMethod(name, sourceClass, paramTypes);
			if (method != null) {
				return method.invoke(invoker, args);
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static @Nullable Object accessMethod$Invoke(String name, Class<?> sourceClass, Object invoker, Object... args) {
		try {
			Method method = accessMethod(name, sourceClass);
			if (method != null) {
				return method.invoke(invoker, args);
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	public static <T> @Nullable T accessField(String name, @NotNull Class<?> sourceClass, Object invoker, Class<T> type) {
		try {
			Field field = sourceClass.getDeclaredField(name);
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			return type.cast(field.get(invoker));
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("deprecation")
	public static void setField(String name, @NotNull Class<?> sourceClass, Object invoker, Object value) {
		try {
			Field field = sourceClass.getDeclaredField(name);
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			field.set(invoker, value);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public static @Nullable Constructor<?> accessConstructor(@NotNull Class<?> sourceClass, Class<?>... paramTypes) {
		try {
			Constructor<?> constructor = sourceClass.getDeclaredConstructor(paramTypes);
			if (!constructor.isAccessible()) {
				constructor.setAccessible(true);
			}
			return constructor;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> @Nullable T createInstance(Class<T> sourceClass, Class<?>[] paramTypes, Object... args) {
		try {
			Constructor<T> constructor = (Constructor<T>) accessConstructor(sourceClass, paramTypes);
			if (constructor != null) {
				return constructor.newInstance(args);
			}
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

}
