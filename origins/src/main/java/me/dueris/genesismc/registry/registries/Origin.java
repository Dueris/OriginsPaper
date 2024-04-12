package me.dueris.genesismc.registry.registries;

import me.dueris.calio.builder.inst.FactoryInstance;
import me.dueris.calio.builder.inst.FactoryObjectInstance;
import me.dueris.calio.builder.inst.factory.FactoryBuilder;
import me.dueris.calio.registry.Registerable;
import me.dueris.calio.registry.Registrar;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.registry.Registries;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Origin implements Serializable, FactoryInstance {

    @Serial
    private static final long serialVersionUID = 1L;

    NamespacedKey tag;
    DatapackFile originFile;
    ArrayList<Power> powerContainer;
    JSONObject choosingCondition;

    public Origin(boolean toRegistry) {
        if (!toRegistry) {
            throw new RuntimeException("Invalid constructor used.");
        }
    }

    /**
     * An object that stores an origin and all the details about it.
     *
     * @param tag            The origin tag.
     * @param originFile     The origin file, parsed into a HashMap.
     * @param powerContainer An array of powers that the origin has.
     */
    public Origin(NamespacedKey tag, DatapackFile originFile, ArrayList<Power> powerContainer) {
        this.tag = tag;
        this.originFile = originFile;
        this.powerContainer = powerContainer;
    }

    /**
     * @return The customOrigin formatted for debugging, not to be used in other circumstances.
     */
    @Override
    public String toString() {
        return "Tag: " + this.tag + ", OriginFile: " + this.originFile + ", PowerContainer: " + this.powerContainer.toString();
    }

    public boolean getUsesCondition() {
        return this.choosingCondition != null;
    }

    public void setUsesCondition(JSONObject condition) {
        this.choosingCondition = condition;
    }

    @Override
    public NamespacedKey getKey() {
        return this.tag;
    }

    /**
     * @return The origin tag.
     */
    public String getTag() {
        return this.tag.asString();
    }

    /**
     * @return The origin file parsed into a HashMap.
     */
    public DatapackFile getOriginFile() {
        return this.originFile;
    }

    /**
     * @return An array containing all the origin powers.
     */
    public ArrayList<Power> getPowerContainers() {
        return new ArrayList<>(this.powerContainer);
    }


    //origin file

    /**
     * @return The name of the origin.
     */
    public String getName() {
        String name = (String) this.originFile.get("name");
        if (name == null) return "No Name";
        return name;
    }

    /**
     * @return The description for the origin.
     */
    public String getDescription() {
        String description = (String) this.originFile.get("description");
        if (description == null) return "No Description";
        return description;
    }

    /**
     * @return An array of powers from the origin.
     */
    public ArrayList<String> getPowers() {
        if (this.originFile.get("powers") instanceof String) {
            ArrayList<String> powers = new ArrayList<>();
            powers.add(String.valueOf(this.originFile.get("powers")));
            return powers;
        } else if (this.originFile.get("powers") instanceof ArrayList<?>) {
            ArrayList<String> powers = (ArrayList<String>) this.originFile.get("powers");
            if (powers == null) return new ArrayList<>();
            return powers;
        }
        return new ArrayList<>();
    }

    /**
     * @return The icon of the origin.
     */
    public String getIcon() {
        Object value = this.originFile.get("icon");
        try {
            if (((JSONObject) value).get("item") != "minecraft:air") return (String) ((JSONObject) value).get("item");
            else return "minecraft:player_head";
        } catch (Exception e) {
            try {
                if (!value.toString().equals("minecraft:air")) return value.toString();
                else return "minecraft:player_head";
            } catch (Exception ex) {
                return "minecraft:player_head";
            }
        }
    }

    public int getOrder() {
        return this.originFile.get("order") == null ? 5 : this.originFile.get("order") instanceof Long ? Math.toIntExact((long) this.originFile.get("order")) : (int) this.originFile.get("order");
    }

    /**
     * @return The icon as a Material Object.
     */
    public Material getMaterialIcon() {
        return me.dueris.calio.util.MiscUtils.getBukkitMaterial(getIcon());
    }

    /**
     * @return The impact of the origin.
     */
    public Long getImpact() {
        Long impact = Long.valueOf(this.originFile.get("impact").toString());
        if (impact == null) return 1L;
        return impact;
    }

    /**
     * @return If the origin is choose-able from the choose menu.
     */
    public Boolean getUnchooseable() {
        Boolean hidden = (Boolean) this.originFile.get("unchooseable");
        if (hidden == null) return false;
        return hidden;
    }

    /**
     * @return The PowerContainer with the given type if present in the origin.
     */
    public ArrayList<Power> getMultiPowerFileFromType(String powerType) {
        ArrayList<Power> powers = new ArrayList<>();
        for (Power power : getPowerContainers()) {
            if (power == null) continue;
            if (power.getType().equals(powerType)) powers.add(power);
        }
        return powers;
    }

    public Power getSinglePowerFileFromType(String powerType) {
        for (Power power : getPowerContainers()) {
            if (power.getType().equals(powerType)) return power;
        }
        return null;
    }

    @Override
    public List<FactoryObjectInstance> getValidObjectFactory() {
        return List.of(
            new FactoryObjectInstance("name", String.class, "No Name"),
            new FactoryObjectInstance("icon", ItemStack.class, new ItemStack(Material.PLAYER_HEAD, 1)),
            new FactoryObjectInstance("impact", Integer.class, 0),
            new FactoryObjectInstance("unchooseable", Boolean.class, false),
            new FactoryObjectInstance("powers", JSONArray.class, new JSONArray())
        );
    }

    @Override
    public void createInstance(FactoryBuilder obj, File rawFile, Registrar<? extends Registerable> registry, NamespacedKey namespacedTag) {
        Registrar<Origin> registrar = (Registrar<Origin>) registry;
        try {
            ArrayList<Power> containers = new ArrayList<>();
            for (Object object : ((JSONArray) obj.getOrDefault("powers", new JSONArray()))) {
                String string = object.toString();
                if (((Registrar<Power>) GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).rawRegistry.containsKey(NamespacedKey.fromString(string))) {
                    containers.add(((Registrar<Power>) GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).get(NamespacedKey.fromString(string)));
                }
                for (Power power : CraftApoli.getNestedPowers(((Registrar<Power>) GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).get(NamespacedKey.fromString(string)))) {
                    if (power != null) {
                        containers.add(power);
                    }
                }
            }
            registrar.register(new Origin(namespacedTag, new DatapackFile(obj.keySet().stream().toList(), obj.values().stream().toList()), containers));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
