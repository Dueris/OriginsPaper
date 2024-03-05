package me.dueris.calio.parse.verification;

import java.util.List;

import org.bukkit.NamespacedKey;

import me.dueris.calio.CraftCalio;
import me.dueris.calio.builder.inst.FactoryObjectInstance;
import me.dueris.calio.builder.inst.FactoryProvider;

public class JsonFactoryValidator {
    public static FactoryProvider validateFactory(FactoryProvider provider, List<FactoryObjectInstance> valid, NamespacedKey factoryKey){
        FactoryProvider cloned = (FactoryProvider) provider.clone();
        for(FactoryObjectInstance instance : valid){
            if(provider.containsKey(instance.getObjName())){
                Object obj = provider.retrive(instance.getObjName(), instance.getType());
                if(obj == null){
                    CraftCalio.INSTANCE.getLogger().severe("Instance is null? Bug?? - " + instance.getObjName());
                    return null;
                }
                if(!(obj.getClass().equals(instance.getType()) || obj.getClass().isAssignableFrom(instance.getType()) || obj.getClass().isInstance(instance.getType()))){
                    CraftCalio.INSTANCE.getLogger().severe("Provided FactoryProvider({b}) has provided an invalid instance for object \"{a}\""
                        .replace("{a}", instance.getObjName())
                        .replace("{b}", factoryKey.asString())
                    );
                    CraftCalio.INSTANCE.getLogger().severe("Object must be an instanceof \"{c}\""
                        .replace("{c}", instance.getType().getSimpleName())
                    );
                    return null;
                }
            }else{
                if(instance.getDefaultValue() != null){
                    cloned.put(instance.getObjName(), instance.getDefaultValue());
                }else{
                    CraftCalio.INSTANCE.getLogger().severe("Provided FactoryProvider({b}) is missing instance: {a}"
                        .replace("{a}", instance.getObjName())
                        .replace("{b}", factoryKey.asString())
                    );
                    return null;
                }
            }
        }
        return cloned;
    }
}
