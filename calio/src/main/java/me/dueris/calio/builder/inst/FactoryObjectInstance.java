package me.dueris.calio.builder.inst;

import me.dueris.calio.CraftCalio;
import me.dueris.calio.util.IgnoreFactoryValidationCheck;
import me.dueris.calio.util.holders.TriPair;

public class FactoryObjectInstance extends TriPair {
    public FactoryObjectInstance(String objName, Class type, Object defaultVal) {
        super(objName, type, defaultVal);
        if (defaultVal != null) {
            if ((defaultVal.getClass() != type || !defaultVal.getClass().isAssignableFrom(type)) && !type.isAnnotationPresent(IgnoreFactoryValidationCheck.class)) {
                CraftCalio.INSTANCE.getLogger().severe("Provided FactoryObjectInstance({oN}) default is not an instanceof provided class type : {c}"
                        .replace("{oN}", objName)
                        .replace("{c}", type.getSimpleName())
                );
            }
        }
    }

    public Class getType() {
        return (Class) this.second;
    }

    public String getObjName() {
        return (String) this.first;
    }

    public Object getDefaultValue() {
        return this.third;
    }
}
