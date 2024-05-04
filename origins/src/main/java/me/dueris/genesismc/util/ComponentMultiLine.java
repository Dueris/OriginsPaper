package me.dueris.genesismc.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.ArrayList;
import java.util.List;

public class ComponentMultiLine {

    public static Component apply(String string) {
	return stringToComponent(string);
    }

    public static Component stringToComponent(String string) {
	return Component.text(string);
    }

    public static List<Component> apply(List<String> string) {
	List<Component> compList = new ArrayList<>();
	string.forEach((st) -> {
	    compList.add(ComponentMultiLine.apply(st).decorate(TextDecoration.ITALIC.withState(false).decoration()));
	});

	return compList;
    }
}
