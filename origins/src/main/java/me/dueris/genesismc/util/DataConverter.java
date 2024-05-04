package me.dueris.genesismc.util;

import me.dueris.genesismc.factory.data.types.Modifier;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;

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

    public static AttributeModifier.Operation convertToOperation(Modifier modifier) {
	return switch (modifier.operation()) {
	    case "addition" -> AttributeModifier.Operation.ADD_NUMBER;
	    case "multiply_base" -> AttributeModifier.Operation.ADD_SCALAR;
	    case "multiply_total" -> AttributeModifier.Operation.MULTIPLY_SCALAR_1;
	    default -> throw new RuntimeException("Not a valid attribute operation!");
	};
    }

    public static AttributeModifier convertToAttributeModifier(Modifier modifier) {
	return new AttributeModifier(modifier.handle.getStringOrDefault("name", "modifier"), modifier.value(), convertToOperation(modifier));
    }
}
