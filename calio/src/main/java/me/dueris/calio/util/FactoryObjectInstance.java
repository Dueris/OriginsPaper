package me.dueris.calio.util;

import me.dueris.calio.CraftCalio;

public class FactoryObjectInstance extends TriPair{
    public FactoryObjectInstance(String objName, Class type, Object defaultVal){
        super(objName, type, defaultVal);
        if(defaultVal.getClass() != type || !defaultVal.getClass().isAssignableFrom(type)){
            CraftCalio.INSTANCE.getLogger().severe("Provided FactoryObjectInstance default is not an instanceof provided class type");
        }
    }

    public Class getType() {
        return (Class) this.first;
    }

    public String getObjName() {
        return (String) this.second;
    }

    public Object getDefaultValue() {
        return this.third;
    }
}
