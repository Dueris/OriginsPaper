package me.dueris.originspaper.util;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ComponentUtil {

	@Contract(value = "_ -> new", pure = true)
	public static @NotNull Component stringToComponent(String string) {
		return Component.text(string);
	}

	public static @NotNull List<Component> stringListToComponent(@NotNull List<String> string) {
		List<Component> compList = new ArrayList<>();
		string.forEach((st) -> {
			compList.add(ComponentUtil.stringToComponent(st));
		});

		return compList;
	}
}
