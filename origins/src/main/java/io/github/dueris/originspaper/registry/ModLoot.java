package io.github.dueris.originspaper.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;

public class ModLoot {

	private static final Set<ResourceKey<LootTable>> AFFECTED_TABLES = new HashSet<>();

	private static final ResourceKey<LootTable> SIMPLE_DUNGEON = getAndAddTable("chests/simple_dungeon");
	private static final ResourceKey<LootTable> STRONGHOLD_LIBRARY = getAndAddTable("chests/stronghold_library");
	private static final ResourceKey<LootTable> MINESHAFT = getAndAddTable("chests/abandoned_mineshaft");
	private static final ResourceKey<LootTable> SMALL_UNDERWATER_RUIN = getAndAddTable("chests/underwater_ruin_small");

	private static final BiFunction<Holder<Enchantment>, Integer, SetEnchantmentsFunction.Builder> SIMPLE_ENCHANTMENT_SETTER = (enchantmentEntry, levels) ->
		new SetEnchantmentsFunction.Builder()
			.withEnchantment(enchantmentEntry, ConstantValue.exactly(levels));

	private static @NotNull ResourceKey<LootTable> getAndAddTable(String path) {

		ResourceKey<LootTable> tableKey = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.parse(path));
		AFFECTED_TABLES.add(tableKey);

		return tableKey;

	}

	public static void modify(ResourceKey<LootTable> key, LootTable.Builder tableBuilder, HolderLookup.Provider registries) {
		if (!AFFECTED_TABLES.contains(key)) {
			return;
		}

		Holder<Enchantment> waterProtection = registries
			.lookupOrThrow(Registries.ENCHANTMENT)
			.getOrThrow(ModEnchantments.WATER_PROTECTION);

		if (key.equals(SIMPLE_DUNGEON)) {
			tableBuilder.withPool(new LootPool.Builder()
				.setRolls(ConstantValue.exactly(1.0F))
				.add(LootItem.lootTableItem(Items.BOOK)
					.setWeight(20)
					.apply(SIMPLE_ENCHANTMENT_SETTER.apply(waterProtection, 1)))
				.add(LootItem.lootTableItem(Items.BOOK)
					.setWeight(10)
					.apply(SIMPLE_ENCHANTMENT_SETTER.apply(waterProtection, 2)))
				.add(EmptyLootItem.emptyItem()
					.setWeight(80)));
		} else if (key.equals(STRONGHOLD_LIBRARY)) {
			tableBuilder.withPool(new LootPool.Builder()
				.setRolls(ConstantValue.exactly(1.0F))
				.add(LootItem.lootTableItem(Items.BOOK)
					.setWeight(20)
					.apply(SIMPLE_ENCHANTMENT_SETTER.apply(waterProtection, 2)))
				.add(LootItem.lootTableItem(Items.BOOK)
					.setWeight(10)
					.apply(SIMPLE_ENCHANTMENT_SETTER.apply(waterProtection, 3)))
				.add(EmptyLootItem.emptyItem()
					.setWeight(80)));
		} else if (key.equals(MINESHAFT)) {
			tableBuilder.withPool(new LootPool.Builder()
				.setRolls(ConstantValue.exactly(1.0F))
				.add(LootItem.lootTableItem(Items.BOOK)
					.setWeight(20)
					.apply(SIMPLE_ENCHANTMENT_SETTER.apply(waterProtection, 1)))
				.add(LootItem.lootTableItem(Items.BOOK)
					.setWeight(5)
					.apply(SIMPLE_ENCHANTMENT_SETTER.apply(waterProtection, 2)))
				.add(EmptyLootItem.emptyItem()
					.setWeight(90)));
		} else if (key.equals(SMALL_UNDERWATER_RUIN)) {
			tableBuilder.withPool(new LootPool.Builder()
				.setRolls(ConstantValue.exactly(1.0F))
				.add(LootItem.lootTableItem(Items.BOOK)
					.setWeight(10)
					.apply(SIMPLE_ENCHANTMENT_SETTER.apply(waterProtection, 1)))
				.add(LootItem.lootTableItem(Items.BOOK)
					.setWeight(20)
					.apply(SIMPLE_ENCHANTMENT_SETTER.apply(waterProtection, 2)))
				.add(EmptyLootItem.emptyItem()
					.setWeight(110)));
		}
	}

}
