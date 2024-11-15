package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.type.ModifyEnchantmentLevelPowerType;
import io.github.dueris.originspaper.util.Comparison;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

public class EnchantmentEntityConditionType extends EntityConditionType {

	public static final TypedDataObjectFactory<EnchantmentEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("enchantment", SerializableDataTypes.ENCHANTMENT)
			.add("use_modifications", SerializableDataTypes.BOOLEAN, true)
			.add("calculation", SerializableDataType.enumValue(Calculation.class), Calculation.SUM)
			.add("comparison", ApoliDataTypes.COMPARISON)
			.add("compare_to", SerializableDataTypes.INT),
		data -> new EnchantmentEntityConditionType(
			data.get("enchantment"),
			data.get("use_modifications"),
			data.get("calculation"),
			data.get("comparison"),
			data.get("compare_to")
		),
		(conditionType, serializableData) -> serializableData.instance()
			.set("enchantment", conditionType.enchantmentKey)
			.set("use_modifications", conditionType.useModifications)
			.set("calculation", conditionType.calculation)
			.set("comparison", conditionType.comparison)
			.set("compare_to", conditionType.compareTo)
	);

	private final ResourceKey<Enchantment> enchantmentKey;
	private final boolean useModifications;

	private final Calculation calculation;

	private final Comparison comparison;
	private final int compareTo;

	public EnchantmentEntityConditionType(ResourceKey<Enchantment> enchantmentKey, boolean useModifications, Calculation calculation, Comparison comparison, int compareTo) {
		this.enchantmentKey = enchantmentKey;
		this.useModifications = useModifications;
		this.calculation = calculation;
		this.comparison = comparison;
		this.compareTo = compareTo;
	}

	@Override
	public boolean test(Entity entity) {

		if (entity instanceof LivingEntity livingEntity) {

			Holder<Enchantment> enchantment = entity.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(enchantmentKey);
			int level = calculation.queryTotalLevel(livingEntity, enchantment, useModifications);

			return comparison.compare(level, compareTo);

		} else {
			return false;
		}

	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return EntityConditionTypes.ENCHANTMENT;
	}

	public enum Calculation {

		SUM {
			@Override
			public int queryLevel(ItemStack stack, Holder<Enchantment> enchantmentEntry, boolean useModifications, int totalLevel) {
				return ModifyEnchantmentLevelPowerType.getEnchantments(stack, stack.getEnchantments(), useModifications).getLevel(enchantmentEntry);
			}

		},

		MAX {
			@Override
			public int queryLevel(ItemStack stack, Holder<Enchantment> enchantmentEntry, boolean useModifications, int totalLevel) {

				int potentialLevel = ModifyEnchantmentLevelPowerType.getEnchantments(stack, stack.getEnchantments(), useModifications).getLevel(enchantmentEntry);

				if (potentialLevel >= totalLevel) {
					return potentialLevel;
				} else {
					return 0;
				}

			}

		};

		public int queryTotalLevel(LivingEntity entity, Holder<Enchantment> enchantmentEntry, boolean useModifications) {

			Enchantment enchantment = enchantmentEntry.value();
			int totalLevel = 0;

			for (ItemStack stack : enchantment.getSlotItems(entity).values()) {
				totalLevel += this.queryLevel(stack, enchantmentEntry, useModifications, totalLevel);
			}

			return totalLevel;

		}

		public abstract int queryLevel(ItemStack stack, Holder<Enchantment> enchantmentEntry, boolean useModifications, int totalLevel);

	}

}
