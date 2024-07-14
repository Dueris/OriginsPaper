package me.dueris.originspaper.util;

import me.dueris.originspaper.factory.data.types.Modifier;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;

/**
 * Helps with old-version conversion of data during runtime
 */
public class DataConverter {
	private static final double DEFAULT_ENTITY_ATTRIBUTE_VALUE = 3.0;
	private static final double DEFAULT_BLOCK_ATTRIBUTE_VALUE = 3.5;
	private static final double DEFAULT_REACH_DISTANCE = 5.0;

	public static Attribute resolveAttribute(String string) {
		String att = fixNamespace(fixAttributeNamespace(string));
		if (att.equalsIgnoreCase("reach-entity-attributes:reach")) return Attribute.PLAYER_BLOCK_INTERACTION_RANGE;
		if (att.equalsIgnoreCase("reach-entity-attributes:attack_range"))
			return Attribute.PLAYER_ENTITY_INTERACTION_RANGE;
		return Attribute.valueOf(att.toUpperCase().split(":")[1]);
	}

	public static ResourceLocation resolveTextureLocationNamespace(ResourceLocation key) {
		if (key.toString().equalsIgnoreCase("origins:resource_bar.png"))
			return ResourceLocation.parse("origins:textures/gui/resource_bar.png");
		return key;
	}

	private static String fixNamespace(String input) {
		return input.contains(":") ? input : ResourceLocation.parse(input).toString();
	}

	private static String fixAttributeNamespace(String input) {
		return input.contains(".") ? input.replace(".", "_") : input;
	}

	public static AttributeModifier.Operation convertToOperation(Modifier modifier) {
		return switch (modifier.operation()) {
			case "addition", "add_base_early", "add_base_late" -> AttributeModifier.Operation.ADD_NUMBER;
			case "multiply_base" -> AttributeModifier.Operation.ADD_SCALAR;
			case "multiply_total" -> AttributeModifier.Operation.MULTIPLY_SCALAR_1;
			default -> throw new RuntimeException("Not a valid attribute operation!");
		};
	}

	public static AttributeModifier convertToAttributeModifier(Modifier modifier) {
		return new AttributeModifier(modifier.handle.getStringOrDefault("name", "modifier"), modifier.value(), convertToOperation(modifier));
	}

	public static double attributeToEntityReach(double attributeValue) {
		if (attributeValue < 0) {
			throw new IllegalArgumentException("Attribute value must be non-negative.");
		}
		return (attributeValue / DEFAULT_ENTITY_ATTRIBUTE_VALUE) * DEFAULT_REACH_DISTANCE;
	}

	public static double entityReachToAttribute(double reachDistance) {
		if (reachDistance < 0) {
			throw new IllegalArgumentException("Reach distance must be non-negative.");
		}
		return (reachDistance / DEFAULT_REACH_DISTANCE) * DEFAULT_ENTITY_ATTRIBUTE_VALUE;
	}

	public static double attributeToBlockReach(double attributeValue) {
		if (attributeValue < 0) {
			throw new IllegalArgumentException("Attribute value must be non-negative.");
		}
		return (attributeValue / DEFAULT_BLOCK_ATTRIBUTE_VALUE) * DEFAULT_REACH_DISTANCE;
	}

	public static double blockReachToAttribute(double reachDistance) {
		if (reachDistance < 0) {
			throw new IllegalArgumentException("Reach distance must be non-negative.");
		}
		return (reachDistance / DEFAULT_REACH_DISTANCE) * DEFAULT_BLOCK_ATTRIBUTE_VALUE;
	}
}
