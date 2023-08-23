package me.dueris.genesismc.factory.powers;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.files.GenesisDataFiles;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.eclipse.sisu.space.ClassFinder;
import org.reflections.Reflections;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public abstract class CraftPower extends BukkitRunnable implements Power {

    public static void register(Class<? extends CraftPower> c){
        if(CraftPower.class.isAssignableFrom(c)){
            getRegistered().add(c);
            try {
                initializePower(c);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }else{
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
                Bukkit.getConsoleSender().sendMessage(subType.getSimpleName());
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
        }.runTaskTimer(GenesisMC.getPlugin(), 0L, 1L);

        if (instance instanceof Listener) {
            Bukkit.getPluginManager().registerEvents((Listener) instance, GenesisMC.getPlugin());
        }
    }

    public static ArrayList<Class<? extends CraftPower>> registered = new ArrayList<>();

    public static ArrayList<Class<? extends CraftPower>> getRegistered(){
        return registered;
    }

    public static boolean isCraftPower(Class<?> c){
        return getRegistered().contains(c);
    }
}
