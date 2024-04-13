package me.dueris.genesismc.registry.registries;

import com.google.gson.JsonElement;
import me.dueris.calio.CraftCalio;
import me.dueris.calio.builder.inst.FactoryInstance;
import me.dueris.calio.builder.inst.FactoryObjectInstance;
import me.dueris.calio.builder.inst.factory.*;
import me.dueris.calio.registry.Registrar;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.data.types.Modifier;
import me.dueris.genesismc.factory.powers.ApoliPower;
import me.dueris.genesismc.registry.Registries;
import org.bukkit.NamespacedKey;

import java.io.File;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Power extends FactoryJsonObject implements Serializable, FactoryInstance {
    @Serial
    private static final long serialVersionUID = 2L;
    public static List<NamespacedKey> allowedSkips = new ArrayList<>();
    public static List<NamespacedKey> notPossibleTypes = new ArrayList<>();

    static {
        allowedSkips.add(new NamespacedKey("apoli", "simple"));
        allowedSkips.add(new NamespacedKey("apoli", "model_color"));
        allowedSkips.add(new NamespacedKey("apoli", "cooldown"));
        notPossibleTypes.add(new NamespacedKey("apoli", "lava_vision")); // Not possible
        notPossibleTypes.add(new NamespacedKey("apoli", "shader")); // Not possible
        allowedSkips.add(new NamespacedKey("apoli", "modify_attribute")); // Not planned, use origins:attribute
        notPossibleTypes.add(new NamespacedKey("apoli", "prevent_feature_render")); // Not possible
        notPossibleTypes.add(new NamespacedKey("apoli", "modify_insomnia_ticks")); // Not possible
        notPossibleTypes.add(new NamespacedKey("apoli", "modify_slipperiness"));
    }

    public FactoryBuilder rawAccessor;
    NamespacedKey powerTag;
    boolean originMultiple;
    boolean originMultipleParent;
    JsonElement jsonData;
    Power powerParent;

    public Power(boolean toRegistry) {
        super(null);
        if (!toRegistry) {
            throw new RuntimeException("Invalid constructor used.");
        }
    }

    /**
     * @param powerTag             The power tag.
     * @param jsonData             The JSON data.
     * @param originMultiple       Tells the plugin if it's an instance of an origins:multiple sub-power.
     * @param originMultipleParent Tells the plugin if it's an origins:multiple parent power.
     * @param powerParent          Tells the plugin what to use as an "Inheritance" for values like the name.
     */
    public Power(NamespacedKey powerTag, FactoryJsonObject jsonData, boolean originMultiple, boolean originMultipleParent, Power powerParent, FactoryBuilder accessor) {
        super(jsonData.handle);
        this.powerTag = powerTag;
        this.originMultiple = originMultiple;
        this.originMultipleParent = originMultipleParent;
        this.powerParent = powerParent;
        this.rawAccessor = accessor;
    }

    /**
     * Constructor with defaults for originMultiple and originMultipleParent.
     *
     * @param powerTag The power tag.
     * @param jsonData The JSON data.
     * @param accessor The FactoryBuilder accessor.
     */
    public Power(NamespacedKey powerTag, FactoryJsonObject jsonData, FactoryBuilder accessor) {
        this(powerTag, jsonData, false, false, null, accessor);
    }

    /**
     * Constructor with defaults for originMultipleParent and powerParent.
     *
     * @param powerTag             The power tag.
     * @param jsonData             The JSON data.
     * @param originMultiple       Tells the plugin if it's an instance of an origins:multiple sub-power.
     * @param accessor             The FactoryBuilder accessor.
     * @param originMultipleParent Tells the plugin if it's an origins:multiple parent
     */
    public Power(NamespacedKey powerTag, FactoryJsonObject jsonData, boolean originMultiple, boolean originMultipleParent, FactoryBuilder accessor) {
        this(powerTag, jsonData, originMultiple, originMultipleParent, null, accessor);
    }


    @Override
    public NamespacedKey getKey() {
        return this.powerTag;
    }

    public boolean isOriginMultipleSubPower() {
        return this.originMultiple;
    }

    public String getJsonData() {
        return this.jsonData.toString();
    }

    // Used for origins:multiple purposes to try and add an "inheritance" feature for cooldowns
    public Power getPowerParent() {
        return powerParent;
    }

    /**
     * @return The power tag.
     */
    public String getTag() {
        return this.powerTag.asString();
    }

    /**
     * @return If the power is an origins:multiple parent
     */
    public boolean isOriginMultipleParent() {
        return this.originMultipleParent;
    }

    /**
     * @return The powerContainer formatted for debugging, not to be used in other circumstances.
     */
    @Override
    public String toString() {
        return "powerTag: " + this.powerTag;
    }

    /**
     * @return The name of the power. Will return "No Name" if there is no power name present.
     */
    public String getName() {
        return getStringOrDefault("name", "No Name");
    }

    /**
     * @return The description of the power. Will return "No Description" if there is no description present.
     */
    public String getDescription() {
        return this.getStringOrDefault("description", "No Description");
    }

    /**
     * @return Whether the power should be displayed. Will return false if "hidden" is not present.
     */
    public boolean isHidden() {
        return this.getBooleanOrDefault("hidden", this.isOriginMultipleSubPower());
    }

    /**
     * @return The type from the power file. Will return "" if there is no type present.
     */
    public String getType() {
        return this.getString("type");
    }

    /**
     * @return Modifiers in the power file or null if not found
     */
    public List<Modifier> getModifiers() {
        return getList$SingularPlural("modifier", "modifiers").stream().map(FactoryElement::toJsonObject).map(Modifier::new).toList();
    }

    public List<String> getStringList(String key) {
        return getJsonArray(key).asList().stream().map(FactoryElement::getString).toList();
    }

    public List<Long> getLongList(String key) {
        return getJsonArray(key).asList().stream().map(FactoryElement::getNumber).map(FactoryNumber::getLong).toList();
    }

    /**
     * @return The singular or plural of a FactoryJsonObject
     */
    public List<FactoryElement> getList$SingularPlural(String singular, String plural) {
        FactoryElement obj = isPresent(singular) ? getElement(singular) : getElement(plural);
        List<FactoryElement> result = new ArrayList<>();

        if (obj.isJsonArray()) {
            FactoryJsonArray jsonArray = obj.toJsonArray();
            for (FactoryElement item : jsonArray.asList()) {
                result.add(item);
            }
        } else {
            result.add(obj);
        }
        return result;
    }

    @Override
    public List<FactoryObjectInstance> getValidObjectFactory() {
        return List.of(
            new FactoryObjectInstance("type", NamespacedKey.class, null),
            new FactoryObjectInstance("name", String.class, "No Name"),
            new FactoryObjectInstance("description", String.class, "No Description"),
            new FactoryObjectInstance("hidden", Boolean.class, false)
        );
    }

    @Override
    public void createInstance(FactoryBuilder root, File rawFile, Registrar registry, NamespacedKey namespacedTag) {
        Registrar<Power> registrar = (Registrar<Power>) registry;
        List<NamespacedKey> validTypes = ((Registrar<ApoliPower>) GenesisMC.getPlugin().registry.retrieve(Registries.CRAFT_POWER)).rawRegistry.keySet().stream().toList();
        if (!validTypes.contains(root.getRoot().getNamespacedKey("type")) && !allowedSkips.contains(root.getRoot().getNamespacedKey("type"))) {
            if (notPossibleTypes.contains(root.getRoot().getNamespacedKey("type"))) {
                CraftCalio.INSTANCE.getLogger().warning("Provided type({t}) is not possible with GenesisMC due to limitations of the ServerSide, power({p}) will not function correctly.".replace("{t}", root.getRoot().getString("type")).replace("{p}", namespacedTag.asString()));
            } else {
                CraftCalio.INSTANCE.getLogger().severe("Unknown type({t}) was provided when registering new Power: ".replace("{t}", root.getRoot().getString("type")) + namespacedTag.asString());
            }
        }
        try {
            Power newPower = new Power(namespacedTag, root.getRoot(), false, root.getRoot().getString("type").equalsIgnoreCase("apoli:multiple"), null, root);
            registrar.register(newPower);
            if (root.getRoot().getString("type").equalsIgnoreCase("apoli:multiple")) {
                CraftApoli.processNestedPowers(
                    newPower,
                    new ArrayList(),
                    namespacedTag.getNamespace(),
                    namespacedTag.getKey(),
                    rawFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
