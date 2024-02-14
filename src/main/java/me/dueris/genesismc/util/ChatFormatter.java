package me.dueris.genesismc.util;

import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;

public class ChatFormatter {

    public static Component apply(String string) {
        Component component = stringToComponent(string);
        return component;
    }

    public static Component stringToComponent(String string) {
        return Component.text(string);
    }

    public static List<Component> apply(List<String> string) {
        List<Component> compList = new ArrayList();
        string.forEach((st) -> {
            compList.add(ChatFormatter.apply(st));
        });

        return compList;
    }
}
