package me.dueris.genesismc.factory.powers;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.apoli.provider.OriginSimpleContainer;
import me.dueris.genesismc.factory.powers.apoli.provider.origins.*;
import me.dueris.genesismc.registry.Registries;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public abstract class CraftPower implements ApoliPower {

    protected static List<Class<CraftPower>> findCraftPowerClasses() throws IOException {
        CompletableFuture<List<Class<CraftPower>>> future = CompletableFuture.supplyAsync(() -> {
            List<Class<CraftPower>> classes = new ArrayList<>();
            try(ScanResult result = new ClassGraph().whitelistPackages("me.dueris.genesismc.factory.powers").enableClassInfo().scan()){
                for(Class<CraftPower> power : result.getSubclasses(CraftPower.class).loadClasses(CraftPower.class)){
                    if (!power.isInterface() && !power.isEnum() && !(power.isAssignableFrom(DontRegister.class) || DontRegister.class.isAssignableFrom(power))){
                        tryPreloadClass(power);
                        classes.add(power);
                    }
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

    public static void tryPreloadClass(Class<?> clz){
        try {
            Class.forName(clz.getName());
        } catch (Exception e){
            // Silence
        }
    }

    private static void registerBuiltinPowers() {
        try {
            for (Class<CraftPower> c : CraftPower.findCraftPowerClasses()) {
                if (CraftPower.class.isAssignableFrom(c)) {
                    CraftPower instance = c.newInstance();
                    GenesisMC.getPlugin().registry.retrieve(Registries.CRAFT_POWER).register(instance);
                    if (instance instanceof Listener || Listener.class.isAssignableFrom(c)) {
                        Bukkit.getServer().getPluginManager().registerEvents((Listener) instance, GenesisMC.getPlugin());
                    }
                }
            }
            OriginSimpleContainer.registerPower(BounceSlimeBlock.class);
            OriginSimpleContainer.registerPower(MimicWarden.class);
            OriginSimpleContainer.registerPower(PiglinNoAttack.class);
            OriginSimpleContainer.registerPower(ScareCreepers.class);
            OriginSimpleContainer.registerPower(NoCobWebSlowdown.class);
            OriginSimpleContainer.registerPower(LikeWater.class);
            OriginSimpleContainer.registerPower(Bioluminescent.class);
        } catch (IOException | ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    public static void registerNewPower(Class<? extends CraftPower> c) throws InstantiationException, IllegalAccessException {
        if (CraftPower.class.isAssignableFrom(c)) {
            CraftPower instance = c.newInstance();
            GenesisMC.getPlugin().registry.retrieve(Registries.CRAFT_POWER).register(instance);
            if (instance instanceof Listener || Listener.class.isAssignableFrom(c)) {
                Bukkit.getServer().getPluginManager().registerEvents((Listener) instance, GenesisMC.getPlugin());
            }
        }
    }
}
