package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.type.ModifyEnchantmentLevelPowerType;
import io.github.dueris.originspaper.util.Comparison;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentConditionType {

	public static boolean condition(Entity entity, ResourceKey<Enchantment> enchantmentKey, Calculation calculation, Comparison comparison, int compareTo, boolean useModifications) {

		Registry<Enchantment> enchantmentRegistry = entity.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
		int enchantmentLevel = 0;

		if (entity instanceof LivingEntity livingEntity) {
			enchantmentLevel = calculation.queryTotalLevel(livingEntity, enchantmentRegistry.getHolder(enchantmentKey).orElseThrow(), useModifications);
		}

		return comparison.compare(enchantmentLevel, compareTo);

	}

	public static ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("enchantment"),
			new SerializableData()
				.add("enchantment", SerializableDataTypes.ENCHANTMENT)
				.add("calculation", SerializableDataType.enumValue(Calculation.class), Calculation.SUM)
				.add("comparison", ApoliDataTypes.COMPARISON)
				.add("compare_to", SerializableDataTypes.INT)
				.add("use_modifications", SerializableDataTypes.BOOLEAN, true),
			(data, entity) -> condition(
				entity,
				data.get("enchantment"),
				data.get("calculation"),
				data.get("comparison"),
				data.get("compare_to"),
				data.get("use_modifications")
			)
		);
	}

	public enum Calculation {

		SUM {
			@Override
			public int queryLevel(ItemStack stack, Holder<Enchantment> enchantmentEntry, boolean useModifications, int totalLevel) {
				return ModifyEnchantmentLevelPowerType.getAndUpdateModifiedEnchantments(stack, stack.getEnchantments(), useModifications).getLevel(enchantmentEntry);
			}

		},

		MAX {
			@Override
			public int queryLevel(ItemStack stack, Holder<Enchantment> enchantmentEntry, boolean useModifications, int totalLevel) {

				int potentialLevel = ModifyEnchantmentLevelPowerType.getAndUpdateModifiedEnchantments(stack, stack.getEnchantments(), useModifications).getLevel(enchantmentEntry);

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
