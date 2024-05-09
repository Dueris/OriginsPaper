package me.dueris.calio.builder.inst;

import me.dueris.calio.CraftCalio;
import me.dueris.calio.util.IgnoreFactoryValidationCheck;
import me.dueris.calio.util.holders.TriPair;

public class FactoryDataDefiner extends TriPair {
    public <T> FactoryDataDefiner(String objName, Class<T> type, T defaultVal) {
        super(objName, type, defaultVal);
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
