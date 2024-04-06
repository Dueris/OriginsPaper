package me.dueris.genesismc.registry;

import me.dueris.genesismc.GenesisMC;
import org.bukkit.NamespacedKey;

public class Registries {
    public static final NamespacedKey ORIGIN = GenesisMC.apoliIdentifier("origin");
    public static final NamespacedKey LAYER = GenesisMC.apoliIdentifier("layer");
    public static final NamespacedKey POWER = GenesisMC.apoliIdentifier("power");
    public static final NamespacedKey CRAFT_POWER = GenesisMC.apoliIdentifier("craft_power");

    public static final NamespacedKey FLUID_CONDITION = GenesisMC.apoliIdentifier("fluid_condition");
    public static final NamespacedKey ITEM_CONDITION = GenesisMC.apoliIdentifier("item_condition");
    public static final NamespacedKey ENTITY_CONDITION = GenesisMC.apoliIdentifier("entity_condition");
    public static final NamespacedKey DAMAGE_CONDITION = GenesisMC.apoliIdentifier("damage_condition");
    public static final NamespacedKey BIENTITY_CONDITION = GenesisMC.apoliIdentifier("bientity_condition");
    public static final NamespacedKey BLOCK_CONDITION = GenesisMC.apoliIdentifier("block_condition");
    public static final NamespacedKey BIOME_CONDITION = GenesisMC.apoliIdentifier("biome_condition");

    public static final NamespacedKey ITEM_ACTION = GenesisMC.apoliIdentifier("item_action");
    public static final NamespacedKey ENTITY_ACTION = GenesisMC.apoliIdentifier("entity_action");
    public static final NamespacedKey BIENTITY_ACTION = GenesisMC.apoliIdentifier("bientity_action");
    public static final NamespacedKey BLOCK_ACTION = GenesisMC.apoliIdentifier("block_action");

    public static final NamespacedKey TEXTURE_LOCATION = GenesisMC.apoliIdentifier("texture_location");
    public static final NamespacedKey PACK_SOURCE = GenesisMC.apoliIdentifier("pack_source");
}