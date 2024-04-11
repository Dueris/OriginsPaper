package me.dueris.genesismc.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

public class Reflector {
    public static Optional<Class<?>> getClass(String name) {
        try {
            return Optional.of(Class.forName(name));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static Method accessMethod(String name, Class sourceClass, Class... paramTypes) {
        try {
            Method method = sourceClass.getDeclaredMethod(name, paramTypes);
            if (!method.isAccessible()) method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Method accessMethod(String name, Class sourceClass) {
        try {
            Method method = sourceClass.getDeclaredMethod(name);
            if (!method.isAccessible()) method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void accessMethod$Invoke(String name, Class sourceClass, Object invoker, Class[] paramTypes, Object... args) {
        try {
            if (args != null && args.length > 0) {
                accessMethod(name, sourceClass, paramTypes).invoke(invoker, args);
                return;
            }
            accessMethod(name, sourceClass, paramTypes).invoke(invoker);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void accessMethod$Invoke(String name, Class sourceClass, Object invoker, Object... args) {
        try {
            if (args != null && args.length > 0) {
                accessMethod(name, sourceClass).invoke(invoker, args);
                return;
            }
            accessMethod(name, sourceClass).invoke(invoker);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static <T> T accessField(String name, Class sourceClass, Object invoker, Class<T> type) {
        try {
            Field field = sourceClass.getDeclaredField(name);
            if (!field.isAccessible()) field.setAccessible(true);
            return (T) field.get(invoker);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }
}
