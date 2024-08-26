package io.github.dueris.originspaper.condition.types.item;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.Comparison;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class EnchantmentCondition {

	public static boolean condition(@NotNull SerializableData.Instance data, @NotNull Tuple<Level, ItemStack> worldAndStack) {

		ResourceKey<Enchantment> enchantmentKey = data.get("enchantment");
		Holder<Enchantment> enchantment = enchantmentKey == null ? null : worldAndStack.getA().registryAccess().registryOrThrow(Registries.ENCHANTMENT)
			.getHolder(enchantmentKey)
			.orElseThrow();

		Comparison comparison = data.get("comparison");
		int compareTo = data.get("compare_to");

		ItemEnchantments component = worldAndStack.getB().getEnchantments();
		int level = enchantment != null ? component.getLevel(enchantment)
			: component.keySet().size();
		return comparison.compare(level, compareTo);

	}

	public static @NotNull ConditionTypeFactory<Tuple<Level, ItemStack>> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("enchantment"),
			SerializableData.serializableData()
				.add("enchantment", SerializableDataTypes.ENCHANTMENT, null)
				.add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN)
				.add("compare_to", SerializableDataTypes.INT, 0)
				.add("use_modifications", SerializableDataTypes.BOOLEAN, true),
			EnchantmentCondition::condition
		);
	}

}
