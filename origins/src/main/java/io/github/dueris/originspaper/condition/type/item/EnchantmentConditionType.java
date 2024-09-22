package io.github.dueris.originspaper.condition.type.item;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.Comparison;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class EnchantmentConditionType {

	public static boolean condition(RegistryAccess registryManager, ItemStack stack, @Nullable ResourceKey<Enchantment> enchantmentKey, Comparison comparison, int compareTo, boolean useModifications) {

		Holder<Enchantment> enchantment = enchantmentKey != null
			? registryManager.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(enchantmentKey)
			: null;


		ItemEnchantments enchantmentsComponent = stack.getEnchantments();
		int level = enchantment != null
			? enchantmentsComponent.getLevel(enchantment)
			: enchantmentsComponent.size();

		return comparison.compare(level, compareTo);

	}

	public static ConditionTypeFactory<Tuple<Level, ItemStack>> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("enchantment"),
			new SerializableData()
				.add("enchantment", SerializableDataTypes.ENCHANTMENT, null)
				.add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN)
				.add("compare_to", SerializableDataTypes.INT, 0)
				.add("use_modifications", SerializableDataTypes.BOOLEAN, true),
			(data, worldAndStack) -> condition(worldAndStack.getA().registryAccess(), worldAndStack.getB(),
				data.get("enchantment"),
				data.get("comparison"),
				data.get("compare_to"),
				data.get("use_modifications")
			)
		);
	}

}
