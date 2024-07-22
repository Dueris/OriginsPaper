package me.dueris.calio.util;

@FunctionalInterface
public interface ConsumerWithReturn<T, R> {
	R accept(T var1);
}
