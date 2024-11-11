package io.github.dueris.calio;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.function.Function;

public class SerializationHelper {

	public static <T extends Enum<T>> @NotNull HashMap<String, T> buildEnumMap(@NotNull Class<T> enumClass, Function<T, String> enumToString) {
		HashMap<String, T> map = new HashMap<>();
		for (T enumConstant : enumClass.getEnumConstants()) {
			map.put(enumToString.apply(enumConstant), enumConstant);
		}
		return map;
	}

}
