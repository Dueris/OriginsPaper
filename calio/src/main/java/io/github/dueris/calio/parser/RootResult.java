package io.github.dueris.calio.parser;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.util.Util;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

public class RootResult<T> {
	private final Constructor<T> constructor;
	private final SerializableData.Instance instance;
	private final Consumer<T> wrapTask;

	public RootResult(Constructor<T> constructor, SerializableData.Instance instance, Consumer<T> wrapTask) {
		this.constructor = constructor;
		this.instance = instance;
		this.wrapTask = wrapTask;
	}

	public T apply(ResourceLocation key) {
		try {
			return Util.instantiate(constructor, key, instance);
		} catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public void finishWrap(T instance) {
		wrapTask.accept(instance);
	}
}
