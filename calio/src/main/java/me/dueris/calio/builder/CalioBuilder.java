package me.dueris.calio.builder;

import com.google.common.base.Preconditions;
import me.dueris.calio.CraftCalio;
import me.dueris.calio.builder.inst.AccessorRoot;
import me.dueris.calio.builder.inst.FactoryInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.NamespacedKey;

public class CalioBuilder {
    public static CalioBuilder INSTANCE = new CalioBuilder();
    public List<AccessorRoot> accessorRoots = new ArrayList<>();

    public void addAccessorRoot(String dirPath, NamespacedKey putRegistry, FactoryInstance inst, int priority){
        accessorRoots.add(new AccessorRoot(dirPath, putRegistry, inst, priority));
    }
}
