package me.dueris.originspaper.util;

import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;

public class ComponentUtil {

	public static Component apply(String string) {
		return stringToComponent(string);
	}

	public static Component stringToComponent(String string) {
		return Component.text(string);
	}

	public static List<Component> apply(List<String> string) {
		List<Component> compList = new ArrayList<>();
		string.forEach((st) -> {
			compList.add(ComponentUtil.apply(st));
		});

		return compList;
	}
}
