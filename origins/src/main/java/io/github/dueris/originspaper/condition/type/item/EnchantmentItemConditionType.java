package io.github.dueris.originspaper.condition.type.item;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.ItemConditionType;
import io.github.dueris.originspaper.condition.type.ItemConditionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.type.ModifyEnchantmentLevelPowerType;
import io.github.dueris.originspaper.util.Comparison;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class EnchantmentItemConditionType extends ItemConditionType {

	public static final TypedDataObjectFactory<EnchantmentItemConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("enchantment", SerializableDataTypes.ENCHANTMENT.optional(), Optional.empty())
			.add("use_modifications", SerializableDataTypes.BOOLEAN, true)
			.add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN)
			.add("compare_to", SerializableDataTypes.INT, 0),
		data -> new EnchantmentItemConditionType(
			data.get("enchantment"),
			data.get("use_modifications"),
			data.get("comparison"),
			data.get("compare_to")
		),
		(conditionType, serializableData) -> serializableData.instance()
			.set("enchantment", conditionType.enchantmentKey)
			.set("use_modifications", conditionType.useModifications)
			.set("comparison", conditionType.comparison)
			.set("compare_to", conditionType.compareTo)
	);

	private final Optional<ResourceKey<Enchantment>> enchantmentKey;
	private final boolean useModifications;

	private final Comparison comparison;
	private final int compareTo;

	public EnchantmentItemConditionType(Optional<ResourceKey<Enchantment>> enchantmentKey, boolean useModifications, Comparison comparison, int compareTo) {
		this.enchantmentKey = enchantmentKey;
		this.useModifications = useModifications;
		this.comparison = comparison;
		this.compareTo = compareTo;
	}

	@Override
	public boolean test(Level world, ItemStack stack) {

		ItemEnchantments enchantmentsComponent = ModifyEnchantmentLevelPowerType.getEnchantments(stack, stack.getEnchantments(), useModifications);
		int levelOrEnchantments = enchantmentKey
			.map(key -> world.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(key))
			.map(enchantmentsComponent::getLevel)
			.orElseGet(enchantmentsComponent::size);

		return comparison.compare(levelOrEnchantments, compareTo);

	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return ItemConditionTypes.ENCHANTMENT;
	}

}
