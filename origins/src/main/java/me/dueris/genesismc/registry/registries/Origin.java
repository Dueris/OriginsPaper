package me.dueris.genesismc.registry.registries;

import com.google.gson.JsonArray;
import me.dueris.calio.CraftCalio;
import me.dueris.calio.builder.inst.FactoryInstance;
import me.dueris.calio.builder.inst.FactoryObjectInstance;
import me.dueris.calio.builder.inst.factory.FactoryBuilder;
import me.dueris.calio.builder.inst.factory.FactoryElement;
import me.dueris.calio.builder.inst.factory.FactoryJsonArray;
import me.dueris.calio.builder.inst.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrable;
import me.dueris.calio.registry.Registrar;
import me.dueris.calio.util.holders.TriPair;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.storage.GenesisConfigs;
import me.dueris.genesismc.util.AsyncUpgradeTracker;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Origin extends FactoryJsonObject implements Serializable, FactoryInstance {

    @Serial
    private static final long serialVersionUID = 1L;

    NamespacedKey tag;
    ArrayList<Power> powerContainer;
    FactoryJsonObject choosingCondition;
    FactoryJsonObject factory;
    boolean isDisabled = false;

    public Origin(boolean toRegistry) {
	super(null);
	if (!toRegistry) {
	    throw new RuntimeException("Invalid constructor used.");
	}
    }

    /**
     * An object that stores an origin and all the details about it.
     *
     * @param tag            The origin tag.
     * @param powerContainer An array of powers that the origin has.
     */
    public Origin(NamespacedKey tag, ArrayList<Power> powerContainer, FactoryJsonObject factoryJsonObject) {
	super(factoryJsonObject.handle);
	this.tag = tag;
	this.powerContainer = powerContainer;
	this.factory = factoryJsonObject;
    }

    /**
     * @return The customOrigin formatted for debugging, not to be used in other circumstances.
     */
    @Override
    public String toString() {
	return "Tag: " + this.tag + ", PowerContainer: " + this.powerContainer.toString();
    }

    public boolean getUsesCondition() {
	return this.choosingCondition != null && !this.choosingCondition.isEmpty();
    }

    public void setUsesCondition(FactoryJsonObject condition) {
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
     * @return An array containing all the origin powers.
     */
    public ArrayList<Power> getPowerContainers() {
	return new ArrayList<>(this.powerContainer);
    }

    /**
     * @return The name of the origin.
     */
    public String getName() {
	return getString("name");
    }

    /**
     * @return The description for the origin.
     */
    public String getDescription() {
	return getString("description");
    }

    /**
     * @return An array of powers from the origin.
     */
    public List<String> getPowers() {
	return isPresent("powers") ? getJsonArray("powers").asList().stream().map(FactoryElement::getString).toList() : new ArrayList<>();
    }

    /**
     * @return The icon of the origin.
     */
    public String getIcon() {
	return getItemStack("icon").getType().getKey().asString();
    }

    public int getOrder() {
	return !isPresent("order") ? 5 : getNumber("order").getInt();
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
    public int getImpact() {
	return getNumberOrDefault("impact", 0).getInt();
    }

    /**
     * @return If the origin is choose-able from the choose menu.
     */
    public boolean getUnchooseable() {
	return getBooleanOrDefault("unchoosable", false) || isDisabled;
    }

    private void setDisabled() {
	isDisabled = true;
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

    @Override
    public List<FactoryObjectInstance> getValidObjectFactory() {
	return List.of(
	    new FactoryObjectInstance("name", String.class, "No Name"),
	    new FactoryObjectInstance("description", String.class, "No Description"),
	    new FactoryObjectInstance("icon", ItemStack.class, new ItemStack(Material.PLAYER_HEAD, 1)),
	    new FactoryObjectInstance("impact", Integer.class, 0),
	    new FactoryObjectInstance("unchooseable", Boolean.class, false),
	    new FactoryObjectInstance("powers", FactoryJsonArray.class, new JsonArray())
	);
    }

    @Override
    public void createInstance(FactoryBuilder obj, File rawFile, Registrar<? extends Registrable> registry, NamespacedKey namespacedTag) {
	Registrar<Origin> registrar = (Registrar<Origin>) registry;
	ArrayList<Power> containers = new ArrayList<>();
	for (String element : obj.getRoot().getJsonArray("powers").asList().stream().map(FactoryElement::getString).toList()) {
	    if (((Registrar<Power>) GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).rawRegistry.containsKey(NamespacedKey.fromString(element))) {
		containers.add(((Registrar<Power>) GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).get(NamespacedKey.fromString(element)));
	    }
	    for (Power power : CraftApoli.getNestedPowers(((Registrar<Power>) GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).get(NamespacedKey.fromString(element)))) {
		if (power != null) {
		    containers.add(power);
		}
	    }
	}
	Origin origin = new Origin(namespacedTag, containers, obj.getRoot());
	((ArrayList<String>) GenesisConfigs.getMainConfig().get("disabled-origins")).forEach(d -> {
	    if (origin.getTag().equalsIgnoreCase(d)) {
		CraftCalio.INSTANCE.getLogger().info("Origin(%e%) was disabled by the config!".replace("%e%", d));
		origin.setDisabled();
	    }
	});
	registrar.register(origin);

	if (obj.getRoot().isPresent("upgrades")) {
	    obj.getRoot().getJsonArray("upgrades").asJsonObjectList().forEach(upgrade -> AsyncUpgradeTracker.upgrades.put(
		origin, new TriPair(
		    upgrade.getString("condition"),
		    upgrade.getNamespacedKey("origin"),
		    upgrade.getStringOrDefault("announcement", AsyncUpgradeTracker.NO_ANNOUNCEMENT)
		)));
	}
    }
}
