package me.dueris.genesismc.factory.powers;

import me.dueris.genesismc.GenesisMC;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.reflections.Reflections;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class CraftPower extends BukkitRunnable implements Power {

    public static ArrayList<Class<? extends CraftPower>> registered = new ArrayList<>();

    public static void register(Class<? extends CraftPower> c) {
        if (CraftPower.class.isAssignableFrom(c)) {
            getRegistered().add(c);
            try {
                initializePower(c);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("Unable to register CraftPower[" + c.getSimpleName() + "] because class doesn't extend CraftPower.class");
        }
    }

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

    private static void initializePower(Class<? extends CraftPower> c) throws InstantiationException, IllegalAccessException {
        CraftPower instance = c.newInstance();
        new BukkitRunnable() {
            @Override
            public void run() {
                instance.run();
            }
        }.runTaskTimer(GenesisMC.getPlugin(), 1, 1);

        if (instance instanceof Listener) {
            Bukkit.getPluginManager().registerEvents((Listener) instance, GenesisMC.getPlugin());
        }
    }

    public static ArrayList<Class<? extends CraftPower>> getRegistered() {
        return registered;
    }

    public static boolean isCraftPower(Class<?> c) {
        return getRegistered().contains(c);
    }
}
