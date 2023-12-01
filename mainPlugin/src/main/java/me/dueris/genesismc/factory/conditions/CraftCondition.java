package me.dueris.genesismc.factory.conditions;


import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.conditions.biEntity.BiEntityCondition;
import me.dueris.genesismc.factory.conditions.biome.BiomeCondition;
import me.dueris.genesismc.factory.conditions.block.BlockCondition;
import me.dueris.genesismc.factory.conditions.damage.DamageCondition;
import me.dueris.genesismc.factory.conditions.entity.EntityCondition;
import me.dueris.genesismc.factory.conditions.fluid.FluidCondition;
import me.dueris.genesismc.factory.conditions.item.ItemCondition;

import java.util.ArrayList;

import org.bukkit.Bukkit;

public class CraftCondition {
    public static BiEntityCondition bientity = new BiEntityCondition();
    public static BiomeCondition biome = new BiomeCondition();
    public static BlockCondition blockCon = new BlockCondition();
    public static DamageCondition damage = new DamageCondition();
    public static EntityCondition entity = new EntityCondition();
    public static FluidCondition fluidCon = new FluidCondition();
    public static ItemCondition item = new ItemCondition();
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
    
    public static void registerCustomCondition(Class<? extends Condition> condition){
        customConditions.add(condition);
        Bukkit.getLogger().info("Origins Condition[%c] has been registered!".replace("%c", condition.getName()));
    }

    protected static ArrayList<Class<? extends Condition>> customConditions = new ArrayList<>();
}
