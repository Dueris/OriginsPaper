package io.github.dueris.calio.parser;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.util.Util;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class RootResult<T> {
	private final Constructor<T> constructor;
	private final SerializableData.Instance instance;

	public RootResult(Constructor<T> constructor, SerializableData.Instance instance) {
		this.constructor = constructor;
		this.instance = instance;
	}

	public T apply(ResourceLocation key) {
		try {
			return Util.instantiate(constructor, key, instance);
		} catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
