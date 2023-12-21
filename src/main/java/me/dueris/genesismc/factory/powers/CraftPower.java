package me.dueris.genesismc.factory.powers;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public abstract class CraftPower implements Power {

    public static ArrayList<Class<? extends CraftPower>> registered = new ArrayList<>();

    public static List<Class<? extends CraftPower>> findCraftPowerClasses() throws IOException {
        CompletableFuture<List<Class<? extends CraftPower>>> future = CompletableFuture.supplyAsync(() -> {
            List<Class<? extends CraftPower>> classes = new ArrayList<>();
            ConfigurationBuilder config = new ConfigurationBuilder();
            config.setScanners(new SubTypesScanner(false));
            config.addUrls(ClasspathHelper.forPackage("me.dueris.genesismc.factory.powers"));
    
            Reflections reflections = new Reflections(config);
    
            Set<Class<? extends CraftPower>> subTypes = reflections.getSubTypesOf(CraftPower.class);
            for (Class<? extends CraftPower> subType : subTypes) {
                if (!subType.isInterface() && !subType.isEnum()) {
                    classes.add(subType);
                }
            }
            return classes;
        });
        
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static ArrayList<Class<? extends CraftPower>> getRegistered() {
        return registered;
    }

    public static boolean isCraftPower(Class<?> c) {
        return getRegistered().contains(c);
    }
}
