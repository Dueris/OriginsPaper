package me.dueris.originspaper.factory.condition.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.data.ApoliDataTypes;
import me.dueris.originspaper.data.types.Comparison;
import me.dueris.originspaper.factory.condition.ConditionFactory;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

public class EnchantmentCondition {

	public static boolean condition(@NotNull Entity entity, ResourceKey<Enchantment> enchantmentKey, Calculation calculation, Comparison comparison, int compareTo, boolean useModifications) {

		Registry<Enchantment> enchantmentRegistry = entity.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
		int enchantmentLevel = 0;

		if (entity instanceof LivingEntity livingEntity) {
			enchantmentLevel = calculation.queryTotalLevel(livingEntity, enchantmentRegistry.getHolder(enchantmentKey).orElseThrow(), useModifications);
		}

		return comparison.compare(enchantmentLevel, compareTo);

	}

	public static @NotNull ConditionFactory<Entity> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("enchantment"),
			InstanceDefiner.instanceDefiner()
				.add("enchantment", SerializableDataTypes.ENCHANTMENT)
				.add("calculation", SerializableDataTypes.enumValue(Calculation.class), Calculation.SUM)
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
				return stack.getEnchantments().getLevel(enchantmentEntry);
			}

		},

		MAX {
			@Override
			public int queryLevel(ItemStack stack, Holder<Enchantment> enchantmentEntry, boolean useModifications, int totalLevel) {

				int potentialLevel = stack.getEnchantments().getLevel(enchantmentEntry);

				if (potentialLevel >= totalLevel) {
					return potentialLevel;
				} else {
					return 0;
				}

			}

		};

		public int queryTotalLevel(LivingEntity entity, @NotNull Holder<Enchantment> enchantmentEntry, boolean useModifications) {

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
