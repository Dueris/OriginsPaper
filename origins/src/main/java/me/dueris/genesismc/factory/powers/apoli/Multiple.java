package me.dueris.genesismc.factory.powers.apoli;

import org.bukkit.NamespacedKey;

import com.google.gson.JsonObject;

import me.dueris.calio.builder.inst.AccessorKey;
import me.dueris.calio.builder.inst.FactoryData;
import me.dueris.calio.builder.inst.annotations.ProvideJsonConstructor;
import me.dueris.calio.builder.inst.annotations.Register;
import me.dueris.calio.builder.inst.factory.FactoryJsonObject;
import me.dueris.calio.parse.CalioJsonParser;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import me.dueris.genesismc.registry.Registries;
import oshi.util.tuples.Pair;

@ProvideJsonConstructor
public class Multiple extends PowerType {
    private final JsonObject source;

    @Register
    public Multiple(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, JsonObject source) {
        super(name, description, hidden, condition, loading_priority);
        this.source = source;
    }

    public static FactoryData registerComponents(FactoryData data) {
        return PowerType.registerComponents(data)
            .ofNamespace(GenesisMC.apoliIdentifier("multiple"));
    }

    public JsonObject getSource() {
        return this.source;
    }

    @Override
    public void bootstrap() {
        source.keySet().forEach(k -> {
            if (source.get(k).isJsonObject()) {
                CalioJsonParser.initilize(
                    new Pair<JsonObject,NamespacedKey>(source.get(k).getAsJsonObject(), NamespacedKey.fromString(this.getKey().asString() + "_" + k.toLowerCase())),
                    new AccessorKey("powers", this.getLoadingPriority(), true, Registries.CRAFT_POWER, PowerType.class)
                );
            }
        });
    }

    @Override
    public NamespacedKey ofResourceLocation(NamespacedKey key) {
        super.ofResourceLocation(key);
        return key;
    }
    
}
