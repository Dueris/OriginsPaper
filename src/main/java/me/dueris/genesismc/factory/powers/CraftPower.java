package me.dueris.genesismc.factory.powers;

import javassist.NotFoundException;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.powers.apoli.provider.OriginSimpleContainer;
import me.dueris.genesismc.factory.powers.apoli.provider.origins.*;
import me.dueris.genesismc.registry.OriginContainer;
import me.dueris.genesismc.registry.PowerContainer;
import me.dueris.genesismc.util.exception.DuplicateCraftPowerException;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.reflections.Reflections;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public abstract class CraftPower implements Power {

    protected static ArrayList<Class<? extends CraftPower>> registered = new ArrayList<>();
    protected static HashMap<String, Class<? extends CraftPower>> registeredFromKey = new HashMap<>();

    protected static List<Class<CraftPower>> findCraftPowerClasses() throws IOException {
        CompletableFuture<List<Class<CraftPower>>> future = CompletableFuture.supplyAsync(() -> {
            List<Class<CraftPower>> classes = new ArrayList<>();
            try(ScanResult result = new ClassGraph().whitelistPackages("me.dueris.genesismc.factory.powers").enableClassInfo().scan()){
                for(Class<CraftPower> power : result.getSubclasses(CraftPower.class).loadClasses(CraftPower.class)){
                    if (!power.isInterface() && !power.isEnum() && !(power.isAssignableFrom(DontRegister.class) || DontRegister.class.isAssignableFrom(power))){
                        classes.add(power);
                    }
                }
            }

            // Set<Class<? extends CraftPower>> subTypes = reflections.getSubTypesOf(CraftPower.class);
            // for (Class<? extends CraftPower> subType : subTypes) {
            //     if (!subType.isInterface() && !subType.isEnum() && !(subType.isAssignableFrom(DontRegister.class) || DontRegister.class.isAssignableFrom(subType))) {
            //         classes.add(subType);
            //     }
            // }
            return classes;
        });

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void registerBuiltinPowers() {
        try {
            for (Class<CraftPower> c : CraftPower.findCraftPowerClasses()) {
                if (CraftPower.class.isAssignableFrom(c)) {
                    CraftPower instance = c.newInstance();
                    if (CraftPower.getKeyedRegistry().containsKey(instance.getPowerFile()) && instance.getPowerFile() != null) {
                        DuplicateCraftPowerException dcpe = new DuplicateCraftPowerException(CraftPower.getCraftPowerFromKey(instance.getPowerFile()), c);
                        dcpe.printStackTrace();
                        continue;
                    }
                    CraftPower.getRegistry().add(c);
                    registeredFromKey.put(instance.getPowerFile(), c);
                    if (instance instanceof Listener || Listener.class.isAssignableFrom(c)) {
                        Bukkit.getServer().getPluginManager().registerEvents((Listener) instance, GenesisMC.getPlugin());
                    }
                }
            }
            for (OriginContainer origin : CraftApoli.getOrigins()) {
                for (PowerContainer powerContainer : origin.getPowerContainers()) {
                    CraftApoli.getPowers().add(powerContainer);
                }
            }
            OriginSimpleContainer.registerPower(BounceSlimeBlock.class);
            OriginSimpleContainer.registerPower(MimicWarden.class);
            OriginSimpleContainer.registerPower(PiglinNoAttack.class);
            OriginSimpleContainer.registerPower(ScareCreepers.class);
            OriginSimpleContainer.registerPower(NoCobWebSlowdown.class);
            OriginSimpleContainer.registerPower(LikeWater.class);
        } catch (IOException | ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    public static void registerNewPower(Class<? extends CraftPower> c) throws InstantiationException, IllegalAccessException {
        if (CraftPower.class.isAssignableFrom(c)) {
            CraftPower instance = c.newInstance();
            if (CraftPower.getKeyedRegistry().containsKey(instance.getPowerFile()) && instance.getPowerFile() != null) {
                DuplicateCraftPowerException dcpe = new DuplicateCraftPowerException(CraftPower.getCraftPowerFromKey(instance.getPowerFile()), c);
                dcpe.printStackTrace();
            }
            CraftPower.getRegistry().add(c);
            CraftPower.getKeyedRegistry().put(instance.getPowerFile(), c);
            if (instance instanceof Listener || Listener.class.isAssignableFrom(c)) {
                Bukkit.getServer().getPluginManager().registerEvents((Listener) instance, GenesisMC.getPlugin());
            }
        }
    }

    public static HashMap<String, Class<? extends CraftPower>> getKeyedRegistry() {
        return registeredFromKey;
    }

    public static ArrayList<Class<? extends CraftPower>> getRegistry() {
        return registered;
    }

    public static Class<? extends CraftPower> getCraftPowerFromKey(String key) {
        return registeredFromKey.getOrDefault(key, null);
    }

    public static Class<? extends CraftPower> getCraftPowerFromKeyOrThrow(String key) throws NotFoundException {
        if (registeredFromKey.get(key) != null) {
            return registeredFromKey.get(key);
        } else {
            throw new NotFoundException("Unable to find power: " + key);
        }
    }

    public static boolean isRegisteredCraftPower(Class<?> c) {
        return getRegistry().contains(c);
    }

    public static boolean isCraftPower(Class<?> c) {
        return CraftPower.class.isAssignableFrom(c);
    }
}
