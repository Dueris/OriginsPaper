package me.dueris.genesismc.util;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;

/**
 * Helps with old-version conversion of data during runtime
 */
public class DataConverter {
    public static Attribute resolveAttribute(String string) {
        String att = fixNamespace(fixAttributeNamespace(string));
        if (att.equalsIgnoreCase("reach-entity-attributes:reach")) return Attribute.PLAYER_BLOCK_INTERACTION_RANGE;
        if (att.equalsIgnoreCase("reach-entity-attributes:attack_range"))
            return Attribute.PLAYER_ENTITY_INTERACTION_RANGE;
        return Attribute.valueOf(att.toUpperCase().split(":")[1]);
    }

    public static NamespacedKey resolveTextureLocationNamespace(NamespacedKey key) {
        if (key.asString().equalsIgnoreCase("origins:resource_bar.png"))
            return NamespacedKey.fromString("origins:textures/gui/resource_bar.png");
        return key;
    }

    private static String fixNamespace(String input) {
        return input.contains(":") ? input : NamespacedKey.fromString(input).asString();
    }

    private static String fixAttributeNamespace(String input) {
        return input.contains(".") ? input.replace(".", "_") : input;
    }
}
