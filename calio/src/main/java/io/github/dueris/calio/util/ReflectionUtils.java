package io.github.dueris.calio.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

public class ReflectionUtils {

	public static @NotNull Optional<Field> getField(@NotNull Class<?> clazz, String fieldName) {
		return Arrays.stream(clazz.getDeclaredFields())
			.filter(f -> f.getName().equals(fieldName))
			.findFirst();
	}

	public static @NotNull Optional<Method> getMethod(@NotNull Class<?> clazz, String methodName, Class<?>... parameterTypes) {
		return Arrays.stream(clazz.getDeclaredMethods())
			.filter(m -> m.getName().equals(methodName) &&
				Arrays.equals(m.getParameterTypes(), parameterTypes))
			.findFirst();
	}

	public static @NotNull Optional<Constructor<?>> getConstructor(@NotNull Class<?> clazz, Class<?>... parameterTypes) {
		return Arrays.stream(clazz.getDeclaredConstructors())
			.filter(c -> Arrays.equals(c.getParameterTypes(), parameterTypes))
			.findFirst();
	}

	@SuppressWarnings("unchecked")
	public static <T> T getFieldValue(Object instance, @NotNull Field field) throws IllegalAccessException {
		field.setAccessible(true);
		return (T) field.get(instance);
	}

	public static void setFieldValue(Object instance, @NotNull Field field, Object value) throws IllegalAccessException {
		field.setAccessible(true);
		field.set(instance, value);
	}

	public static boolean invokeBooleanMethod(Object instance, @NotNull String methodName, Object @NotNull ... args) {
		try {
			Class<?>[] argTypes = new Class<?>[args.length];
			for (int i = 0; i < args.length; i++) {
				argTypes[i] = args[i].getClass();
			}

			Method method = instance.getClass().getDeclaredMethod(methodName, argTypes);
			method.setAccessible(true);
			return (boolean) method.invoke(instance, args);
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			return true;
		}
	}

	public static void invokeMethod(Object instance, @NotNull String methodName) {
		try {
			Method method = instance.getClass().getDeclaredMethod(methodName);
			method.setAccessible(true);
			method.invoke(instance);
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {
		}
	}

	public static <T> @NotNull T newInstance(@NotNull Constructor<T> constructor, Object... args) throws InvocationTargetException, InstantiationException, IllegalAccessException {
		constructor.setAccessible(true);
		return constructor.newInstance(args);
	}

	public static <A extends Annotation> Optional<A> getAnnotation(@NotNull AnnotatedElement element, Class<A> annotationClass) {
		return Optional.ofNullable(element.getAnnotation(annotationClass));
	}

	@Contract(pure = true)
	public static boolean hasAnnotation(@NotNull AnnotatedElement element, Class<? extends Annotation> annotationClass) {
		return element.isAnnotationPresent(annotationClass);
	}

	@SuppressWarnings("unchecked")
	public static <T> @NotNull T createProxy(@NotNull Class<T> interfaceClass, InvocationHandler handler) {
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, handler);
	}

	public static @NotNull Optional<Field> findFieldByPredicate(@NotNull Class<?> clazz, Predicate<Field> predicate) {
		return Arrays.stream(clazz.getDeclaredFields())
			.filter(predicate)
			.findFirst();
	}

	public static @NotNull Optional<Method> findMethodByPredicate(@NotNull Class<?> clazz, Predicate<Method> predicate) {
		return Arrays.stream(clazz.getDeclaredMethods())
			.filter(predicate)
			.findFirst();
	}

	public static @NotNull Optional<Method> getStaticMethod(@NotNull Class<?> clazz, String methodName) {
		return Arrays.stream(clazz.getDeclaredMethods())
			.filter(m -> Modifier.isStatic(m.getModifiers()) && m.getName().equals(methodName) && m.getParameterCount() == 0)
			.findFirst();
	}

	@SuppressWarnings("unchecked")
	public static <T> T invokeStaticMethod(Class<?> clazz, String methodName) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
		Optional<Method> methodOpt = getStaticMethod(clazz, methodName);
		if (methodOpt.isPresent()) {
			Method method = methodOpt.get();
			method.setAccessible(true);
			return (T) method.invoke(null);
		}
		throw new NoSuchMethodException("No such static method: " + methodName);
	}

	public static boolean hasMethod(@NotNull Class<?> clazz, String methodName, boolean isStatic, Class<?>... parameterTypes) {
		return Arrays.stream(clazz.getDeclaredMethods())
			.anyMatch(m -> Modifier.isStatic(m.getModifiers()) == isStatic &&
				m.getName().equals(methodName) &&
				Arrays.equals(m.getParameterTypes(), parameterTypes));
	}

	public static boolean hasField(@NotNull Class<?> clazz, String fieldName, boolean isStatic) {
		return Arrays.stream(clazz.getDeclaredFields())
			.anyMatch(f -> Modifier.isStatic(f.getModifiers()) == isStatic &&
				f.getName().equals(fieldName));
	}

	public static boolean hasFieldWithAnnotation(@NotNull Class<?> clazz, Class<?> fieldType, Class<? extends Annotation> annotationType) {
		while (clazz != null) {
			if (Arrays.stream(clazz.getDeclaredFields())
				.anyMatch(field -> fieldType.isAssignableFrom(field.getType()) && field.isAnnotationPresent(annotationType))) {
				return true;
			}
			clazz = clazz.getSuperclass();
		}
		return false;
	}

	public static <T> void setFieldWithAnnotation(@NotNull Object target, Class<? extends Annotation> annotationType, T value) {
		Class<?> clazz = target.getClass();
		Field fieldToSet = null;

		while (clazz != null) {
			for (Field field : clazz.getDeclaredFields()) {
				if (field.isAnnotationPresent(annotationType)) {
					fieldToSet = field;
					break;
				}
			}
			if (fieldToSet != null) {
				break;
			}
			clazz = clazz.getSuperclass();
		}

		if (fieldToSet != null) {
			try {
				boolean accessible = fieldToSet.canAccess(target);
				if (!accessible) {
					fieldToSet.setAccessible(true);
				}
				fieldToSet.set(target, value);
				if (!accessible) {
					fieldToSet.setAccessible(false);
				}
			} catch (IllegalAccessException e) {
				throw new RuntimeException("Failed to set field value", e);
			}
		} else {
			throw new IllegalArgumentException("No field with the specified annotation found");
		}
	}

	public static Object getStaticFieldValue(@NotNull Class<?> clazz, String fieldName) {
		try {
			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(null);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException("Failed to get static field value", e);
		}
	}

}
