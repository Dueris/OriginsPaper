package me.dueris.genesismc.factory.powers.simple;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.NamespacedKey;
import org.mineskin.com.google.common.base.Preconditions;

import javassist.NotFoundException;
import me.dueris.genesismc.factory.powers.CraftPower;

public class OriginSimpleContainer {
    protected static ArrayList<Class<? extends CraftPower>> simpleRegistry = new ArrayList<>();
    protected static HashMap<String, Class<? extends CraftPower>> keyedRegistry = new HashMap();

    public static boolean registerPower(Class<? extends CraftPower> clz){
        try {
            Preconditions.checkArgument(clz.newInstance() instanceof PowerProvider, "CraftPower isnt an instance of a PowerProvider power. This is required to make it so that its marked as able to be its own originPower");
            Preconditions.checkArgument(clz.getDeclaredField("powerReference") != null, "Unable to access required field \"powerReference\" inside CraftPower. This is required to point to what powerFile this PowerProvider will use");
            
            Field field = clz.getDeclaredField("powerReference");
            field.setAccessible(true);
            
            NamespacedKey key = (NamespacedKey) field.get(clz.newInstance());
            simpleRegistry.add(clz);
            keyedRegistry.put(key.asString(), clz);
        } catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static Class<? extends CraftPower> getFromRegistry(String powerTag){
        return keyedRegistry.getOrDefault(powerTag, null);
    } 

    public static Class<? extends CraftPower> getFromRegistryOrThrow(String powerTag) throws NotFoundException{
        if (keyedRegistry.containsKey(powerTag)) {
            return keyedRegistry.get(powerTag);
        } else {throw new NotFoundException("CraftPower not found");}
    }
}
