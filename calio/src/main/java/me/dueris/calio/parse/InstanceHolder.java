package me.dueris.calio.parse;

import me.dueris.calio.builder.inst.FactoryInstance;

/**
 * Holds an instnace via a DIRECTORY within a namespace
 * data/<namespace>/YOUR_HOLDER/YOUR_OBJECTS.json
 */
public class InstanceHolder {
    private String directory;
    private FactoryInstance holderObject;

    public InstanceHolder(String directory, FactoryInstance holderObject){
        this.directory = directory;
        this.holderObject = holderObject;
    }
}
