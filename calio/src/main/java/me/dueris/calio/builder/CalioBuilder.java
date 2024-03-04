package me.dueris.calio.builder;

import com.google.common.base.Preconditions;
import me.dueris.calio.CraftCalio;
import me.dueris.calio.builder.inst.FactoryInstance;

import java.util.HashMap;
import java.util.List;

public class CalioBuilder {
    public static CalioBuilder INSTANCE = new CalioBuilder();
    public HashMap<String, FactoryInstance> folderToFactory = new HashMap<>();

    public void registerType(List<String> types, FactoryInstance registerable){
        for(String type : types){
            Preconditions.checkArgument(registerable != null, "FactoryRegisterable cannot be null!");
            if(folderToFactory.containsKey(type)){
                CraftCalio.INSTANCE.getLogger().warning("Registered folder \"{}\" is already taken, overriding...".replace("{}", type));
            }
            folderToFactory.put(type, registerable);
        }
    }
}
