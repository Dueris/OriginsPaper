package me.dueris.genesismc.factory.conditions;

import org.reflections.Reflections;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CraftCondition {
    public static List<Class<? extends Condition>> findCraftConditionClasses() throws IOException {
        List<Class<? extends Condition>> classes = new ArrayList<>();
        Reflections reflections = new Reflections("me.dueris.genesismc.factory.conditions");

        Set<Class<? extends Condition>> subTypes = reflections.getSubTypesOf(Condition.class);
        for (Class<? extends Condition> subType : subTypes) {
            if (!subType.isInterface() && !subType.isEnum()) {
                classes.add(subType);
            }
        }

        return classes;
    }

    public static ArrayList<Class<? extends Condition>> conditionClasses = new ArrayList<>();
}
