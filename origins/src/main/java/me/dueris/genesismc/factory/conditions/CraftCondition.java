package me.dueris.genesismc.factory.conditions;


import me.dueris.genesismc.factory.conditions.types.*;
import org.bukkit.Bukkit;

import java.util.ArrayList;

public class CraftCondition {
    public static BiEntityConditions bientity;
    public static BiomeConditions biome;
    public static BlockConditions blockCon;
    public static DamageConditions damage;
    public static EntityConditions entity;
    public static FluidConditions fluidCon;
    public static ItemConditions item;
    // Depreciated lol
    // public static List<Class<? extends Condition>> findCraftConditionClasses() throws IOException {
    //     List<Class<? extends Condition>> classes = new ArrayList<>();
    //     Reflections reflections = new Reflections("me.dueris.genesismc.factory.conditions");

    //     Set<Class<? extends Condition>> subTypes = reflections.getSubTypesOf(Condition.class);
    //     for (Class<? extends Condition> subType : subTypes) {
    //         if (!subType.isInterface() && !subType.isEnum()) {
    //             classes.add(subType);
    //         }
    //     }

    //     return classes;
    // }
    protected static ArrayList<Class<? extends Condition>> customConditions = new ArrayList<>();

    public static void registerCustomCondition(Class<? extends Condition> condition) {
        customConditions.add(condition);
        Bukkit.getLogger().info("Origins Condition[%c] has been registered!".replace("%c", condition.getName()));
    }
}
