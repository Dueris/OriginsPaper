package me.dueris.genesismc.factory.powers;

import javassist.NotFoundException;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.powers.simple.*;
import me.dueris.genesismc.factory.powers.simple.origins.*;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import me.dueris.genesismc.utils.exception.DuplicateCraftPowerException;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public abstract class CraftPower implements Power {

    protected static ArrayList<Class<? extends CraftPower>> registered = new ArrayList<>();
    protected static HashMap<String, Class<? extends CraftPower>> registeredFromKey = new HashMap<>();

    public static void freezeRegistry(){
        registered = (ArrayList<Class<? extends CraftPower>>) Collections.unmodifiableList(registered);
        registeredFromKey = (HashMap<String, Class<? extends CraftPower>>) Collections.unmodifiableMap(registeredFromKey);
    }

    protected static List<Class<? extends CraftPower>> findCraftPowerClasses() throws IOException {
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

    private static void registerBuiltinPowers() {
        try {
            for (Class<? extends CraftPower> c : CraftPower.findCraftPowerClasses()) {
                if (CraftPower.class.isAssignableFrom(c)) {
                    CraftPower instance = c.newInstance();
                    if(CraftPower.getKeyedRegistry().containsKey(instance.getPowerFile()) && instance.getPowerFile() != null){
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
