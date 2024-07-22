package me.dueris.originspaper.util;

import me.dueris.originspaper.factory.data.types.Modifier;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class DataConverter {
	private static final double DEFAULT_ENTITY_ATTRIBUTE_VALUE = 3.0;
	private static final double DEFAULT_BLOCK_ATTRIBUTE_VALUE = 3.5;
	private static final double DEFAULT_REACH_DISTANCE = 5.0;

	public static Attribute resolveAttribute(String string) {
		String att = fixNamespace(fixAttributeNamespace(string));
		if (att.equalsIgnoreCase("reach-entity-attributes:reach")) {
			return Attribute.PLAYER_BLOCK_INTERACTION_RANGE;
		} else {
			return att.equalsIgnoreCase("reach-entity-attributes:attack_range")
				? Attribute.PLAYER_ENTITY_INTERACTION_RANGE
				: Attribute.valueOf(att.toUpperCase().split(":")[1]);
		}
	}

	public static ResourceLocation resolveTextureLocationNamespace(@NotNull ResourceLocation key) {
		return key.toString().equalsIgnoreCase("origins:resource_bar.png") ? ResourceLocation.parse("origins:textures/gui/resource_bar.png") : key;
	}

	private static String fixNamespace(@NotNull String input) {
		return input.contains(":") ? input : ResourceLocation.parse(input).toString();
	}

	private static String fixAttributeNamespace(@NotNull String input) {
		return input.contains(".") ? input.replace(".", "_") : input;
	}

	public static Operation convertToOperation(@NotNull Modifier modifier) {
		String var1 = modifier.operation();

		return switch (var1) {
			case "addition", "add_base_early", "add_base_late", "add_value" -> Operation.ADD_NUMBER;
			case "multiply_base" -> Operation.ADD_SCALAR;
			case "multiply_total" -> Operation.MULTIPLY_SCALAR_1;
			default -> throw new RuntimeException("Not a valid attribute operation!");
		};
	}

	@Contract("_ -> new")
	public static @NotNull AttributeModifier convertToAttributeModifier(@NotNull Modifier modifier) {
		return new AttributeModifier(modifier.handle.getStringOrDefault("name", "modifier"), modifier.value().floatValue(), convertToOperation(modifier));
	}

	public static double attributeToEntityReach(double attributeValue) {
		if (attributeValue < 0.0) {
			throw new IllegalArgumentException("Attribute value must be non-negative.");
		} else {
			return attributeValue / 3.0 * 5.0;
		}
	}

	public static double entityReachToAttribute(double reachDistance) {
		if (reachDistance < 0.0) {
			throw new IllegalArgumentException("Reach distance must be non-negative.");
		} else {
			return reachDistance / 5.0 * 3.0;
		}
	}

	public static double attributeToBlockReach(double attributeValue) {
		if (attributeValue < 0.0) {
			throw new IllegalArgumentException("Attribute value must be non-negative.");
		} else {
			return attributeValue / 3.5 * 5.0;
		}
	}

	public static double blockReachToAttribute(double reachDistance) {
		if (reachDistance < 0.0) {
			throw new IllegalArgumentException("Reach distance must be non-negative.");
		} else {
			return reachDistance / 5.0 * 3.5;
		}
	}
}
