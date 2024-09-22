package io.github.dueris.originspaper.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class ComponentUtil {

	@Contract(value = "_ -> new", pure = true)
	public static @NotNull Component stringToComponent(String string) {
		return Component.text(string);
	}

	@Contract("_ -> new")
	public static @NotNull TextComponent nmsToKyori(net.minecraft.network.chat.@NotNull Component component) {
		return Component.text(component.getString());
	}

	public static @NotNull List<Component> stringListToComponent(@NotNull List<String> string) {
		List<Component> compList = new LinkedList<>();
		string.forEach((st) -> {
			compList.add(ComponentUtil.stringToComponent(st));
		});

		return compList;
	}
}
