package me.dueris.calio.util;

public class TriPair {
    private String objName;
    private Class type;
    private Object defaultValue;

    public TriPair(String objName, Class type, Object defaultVal){
        this.objName = objName;
        this.type = type;
        this.defaultValue = defaultVal;
    }

    public Class getType() {
        return type;
    }

    public String getObjName() {
        return objName;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }
}
