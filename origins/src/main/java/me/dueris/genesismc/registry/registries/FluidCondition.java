package me.dueris.genesismc.registry.registries;

import me.dueris.calio.builder.inst.FactoryInstance;
import me.dueris.calio.util.FactoryObjectInstance;
import me.dueris.genesismc.GenesisMC;
import org.bukkit.NamespacedKey;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.function.Predicate;

public class FluidCondition implements FactoryInstance {

    @Override
    public NamespacedKey getKey() {
        return GenesisMC.apoliIdentifier("fluid");
    }

    @Override
    public List<FactoryObjectInstance> getValidInstances() {
        return List.of(new FactoryObjectInstance("fluid", NamespacedKey.class, null));
    }
}
