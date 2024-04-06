package me.dueris.genesismc.factory.conditions;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.conditions.types.*;
import org.bukkit.NamespacedKey;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

public enum ConditionTypes {
    BIENTITY,
    BIOME,
    BLOCK,
    DAMAGE,
    ENTITY,
    FLUID,
    ITEM;

    public static class ConditionFactory {
        public static void addMetaConditions() {
            List<Class> classes = new ArrayList<>();
            String[] names = {"and", "or", "chance", "constant", "not"};
            classes.addAll(List.of(BiEntityConditions.class, BiomeConditions.class, BlockConditions.class, DamageConditions.class, EntityConditions.class, FluidConditions.class, ItemConditions.class));
            classes.forEach(c -> {
                try {
                    Class factoryInstance = Class.forName(c.getName() + "$ConditionFactory");
                    for (String name : names) {
                        Object inst = factoryInstance.getConstructor(c, NamespacedKey.class, BiPredicate.class).newInstance(c.newInstance(), GenesisMC.apoliIdentifier(name), new BiPredicate() {
                            @Override
                            public boolean test(Object o, Object o2) {
                                throw new IllegalStateException("Executor should not be here right now! Report to Dueris!");
                            }
                        });
                        Method registerMethod = c.getDeclaredMethod("register", inst.getClass());
                        registerMethod.setAccessible(true);
                        registerMethod.invoke(c.newInstance(), inst);
                    }
                } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException |
                         IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
