package me.dueris.genesismc.factory.powers;

import org.reflections.Reflections;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class CraftPower implements Power {

    public static ArrayList<Class<? extends CraftPower>> registered = new ArrayList<>();

    public static List<Class<? extends CraftPower>> findCraftPowerClasses() throws IOException {
        List<Class<? extends CraftPower>> classes = new ArrayList<>();
        Reflections reflections = new Reflections("me.dueris.genesismc.factory.powers");

        Set<Class<? extends CraftPower>> subTypes = reflections.getSubTypesOf(CraftPower.class);
        for (Class<? extends CraftPower> subType : subTypes) {
            if (!subType.isInterface() && !subType.isEnum()) {
                classes.add(subType);
            }
        }

        return classes;
    }

    public static ArrayList<Class<? extends CraftPower>> getRegistered() {
        return registered;
    }

    public static boolean isCraftPower(Class<?> c) {
        return getRegistered().contains(c);
    }
}
