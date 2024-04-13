package me.dueris.genesismc.registry.registries;

import me.dueris.calio.CraftCalio;
import me.dueris.calio.builder.inst.FactoryInstance;
import me.dueris.calio.builder.inst.FactoryObjectInstance;
import me.dueris.calio.builder.inst.factory.FactoryBuilder;
import me.dueris.calio.builder.inst.factory.FactoryElement;
import me.dueris.calio.builder.inst.factory.FactoryJsonArray;
import me.dueris.calio.builder.inst.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registerable;
import me.dueris.calio.registry.Registrar;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;

import java.io.File;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Layer extends FactoryJsonObject implements Serializable, FactoryInstance {
    @Serial
    private static final long serialVersionUID = 4L;

    NamespacedKey tag;
    List<Origin> origins;
    FactoryJsonObject factory;

    public Layer(boolean toRegistry) {
        super(null);
        if (!toRegistry) {
            throw new RuntimeException("Invalid constructor used.");
        }
    }

    public Layer(NamespacedKey tag, List<Origin> origins, FactoryJsonObject factoryJsonObject) {
        super(factoryJsonObject.handle);
        this.tag = tag;
        this.origins = origins;
        this.factory = factoryJsonObject;
    }

    /**
     * @return The LayerContainer formatted for debugging, not to be used in other circumstances.
     */
    @Override
    public String toString() {
        return "Tag = " + tag;
    }

    public List<Origin> testChoosable(Entity entity) {
        List<Origin> tested = new ArrayList<Origin>();
        for (Origin origin : this.origins) {
            if (origin.getUsesCondition()) {
                if (ConditionExecutor.testEntity(origin.choosingCondition, (CraftEntity) entity)) {
                    tested.add(origin);
                }
            } else {
                tested.add(origin);
            }
        }
        return tested;
    }

    @Override
    public NamespacedKey getKey() {
        return this.tag;
    }

    /**
     * @return The tag associated with this layer
     */
    public String getTag() {
        return tag.asString();
    }

    /**
     * @return The name of the layer file or tag if null
     */
    public String getName() {
        return getStringOrDefault("name", "No Name");
    }

    /**
     * @return The name of the layer file or tag if null
     */
    public boolean getReplace() {
        return getBooleanOrDefault("replace", false);
    }

    /**
     * @return An array list of the loaded origins tags
     */
    public List<String> getOrigins() {
        return origins.stream().map(Origin::getTag).toList();
    }

    @Override
    public List<FactoryObjectInstance> getValidObjectFactory() {
        return List.of(
            new FactoryObjectInstance("origins", FactoryJsonArray.class, null),
            new FactoryObjectInstance("enabled", Boolean.class, true),
            new FactoryObjectInstance("replace", Boolean.class, false),
            new FactoryObjectInstance("allow_random", Boolean.class, true),
            new FactoryObjectInstance("name", String.class, "No Name"),
            new FactoryObjectInstance("hidden", Boolean.class, false)
        );
    }

    @Override
    public void createInstance(FactoryBuilder obj, File rawFile, Registrar<? extends Registerable> registry, NamespacedKey namespacedTag) {
        Registrar<Layer> registrar = (Registrar<Layer>) registry;
        AtomicBoolean merge = new AtomicBoolean(!obj.getRoot().getBooleanOrDefault("replace", false) && registry.rawRegistry.containsKey(namespacedTag));
        if (merge.get()) {
            List<Origin> originList = new ArrayList<>();
            for (FactoryElement element : obj.getRoot().getJsonArray("origins").asList()) {
                Origin origin = CraftApoli.getOrigin(element.getString());
                if (!origin.equals(CraftApoli.nullOrigin())) {
                    originList.add(origin);
                } else {
                    CraftCalio.INSTANCE.getLogger().severe("Origin not found inside layer");
                }
            }
            registrar.get(namespacedTag).getOrigins().stream().forEach(tag -> originList.add(CraftApoli.getOrigin(tag)));
            registrar.replaceEntry(namespacedTag, new Layer(namespacedTag, originList, obj.getRoot()));
        } else {
            List<Origin> list = new ArrayList<>();
            for (FactoryElement element : obj.getRoot().getJsonArray("origins").asList()) {
                if (element.isJsonObject()) {
                    FactoryJsonObject jsonObject = element.toJsonObject();
                    for (String elementString : jsonObject.getJsonArray("origins").asList().stream().map(FactoryElement::getString).toList()) {
                        Origin origin = CraftApoli.getOrigin(elementString);
                        if (!origin.equals(CraftApoli.nullOrigin())) {
                            origin.setUsesCondition(jsonObject.getJsonObject("condition"));
                        } else {
                            CraftCalio.INSTANCE.getLogger().severe("Origin(%a%) not found inside layer".replace("%a%", elementString));
                        }
                    }
                } else if (element.isString()) {
                    Origin origin = CraftApoli.getOrigin(element.getString());
                    if (!origin.equals(CraftApoli.nullOrigin())) {
                        list.add(origin);
                    } else {
                        CraftCalio.INSTANCE.getLogger().severe("Origin(%a%) not found inside layer".replace("%a%", element.getString()));
                    }
                } else {
                    CraftCalio.INSTANCE.getLogger().severe("Unknown type \"{t}\" was provided in the \"powers\" field for the OriginLayer!".replace("{t}", element.getClass().getSimpleName()));
                }
            }
            registrar.register(new Layer(namespacedTag, list, obj.getRoot()));
        }
    }
}
