package me.dueris.calio.parse.verification;

import me.dueris.calio.CraftCalio;
import me.dueris.calio.builder.inst.FactoryObjectInstance;
import me.dueris.calio.builder.inst.factory.FactoryBuilder;
import me.dueris.calio.builder.inst.factory.FactoryElement;
import me.dueris.calio.builder.inst.factory.FactoryJsonArray;
import me.dueris.calio.builder.inst.factory.FactoryJsonObject;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class JsonFactoryValidator {
    /**
     * Validate the FactoryProvider and return a cloned instance with validated objects.
     *
     * @param provider   the original FactoryProvider to be validated
     * @param valid      the list of valid FactoryObjectInstance to be validated against the provider
     * @param factoryKey the NamespacedKey representing the factory
     * @return a cloned and validated FactoryProvider
     */
    public static FactoryBuilder validateFactory(FactoryBuilder provider, List<FactoryObjectInstance> valid, NamespacedKey factoryKey) {
        FactoryBuilder cloned = (FactoryBuilder) provider.cloneFactory();
        for (FactoryObjectInstance instance : valid) {
            if (provider.getRoot().isPresent(instance.getObjName())) {
                Object obj = retriveSpecificType(provider.getRoot(), instance.getObjName(), instance.getType());
                if (obj == null) {
                    CraftCalio.INSTANCE.getLogger().severe("Instance is null? Bug?? - " + instance.getObjName());
                    return null;
                }
                if (!(obj.getClass().equals(instance.getType()) || obj.getClass().isAssignableFrom(instance.getType()) || obj.getClass().isInstance(instance.getType()))) {
                    CraftCalio.INSTANCE.getLogger().severe("Provided FactoryProvider({b}) has provided an invalid instance for object \"{a}\""
                        .replace("{a}", instance.getObjName())
                        .replace("{b}", factoryKey.asString())
                    );
                    CraftCalio.INSTANCE.getLogger().severe("Object must be an instanceof \"{c}\""
                        .replace("{c}", instance.getType().getSimpleName())
                    );
                    return null;
                }
            } else {
                if (instance.getDefaultValue() != null) {
                    cloned.putDefault(instance.getObjName(), instance.getDefaultValue());
                } else {
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

    private static Object retriveSpecificType(FactoryJsonObject element, String type, Class objType) {
        if (element.isPresent(type)) {
            if (objType.equals(ItemStack.class)) {
                return element.getItemStack(type);
            } else if (objType.equals(NamespacedKey.class)) {
                return element.getNamespacedKey(type);
            } else if (objType.equals(FactoryJsonObject.class)) {
                return element.getJsonObject(type);
            }

            return getFromGson(element, type);
        }
        return null;
    }

    private static Object getFromGson(FactoryJsonObject element, String type) {
        if (element.isJsonArray(type)) {
            return element.getJsonArray(type);
        }
        if (element.isPresent(type)) {
            FactoryElement rawElement = element.getElement(type);
            if (element.isGsonPrimative(type)) {
                if (rawElement.isString()) {
                    return rawElement.getString();
                }
                if (rawElement.isBoolean()) {
                    return rawElement.getBoolean();
                }
                if (rawElement.isNumber()) {
                    Number number = rawElement.getNumber().asNumber();
                    if (number.toString().contains(".")) {
                        return rawElement.getNumber().getFloat();
                    } else {
                        return rawElement.getNumber().getInt();
                    }
                }
            }
        }
        return null;
    }
}
