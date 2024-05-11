package me.dueris.genesismc.factory.conditions.types;

import com.mojang.brigadier.StringReader;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrable;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.content.OrbOfOrigins;
import me.dueris.genesismc.content.enchantment.EnchantTableHandler;
import me.dueris.genesismc.factory.data.types.Comparison;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.util.Utils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class ItemConditions {

	public void prep() {
		register(new ConditionFactory(GenesisMC.apoliIdentifier("food"), (condition, itemStack) -> itemStack.getType().isEdible()));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("smeltable"), (condition, itemStack) -> itemStack.getType().isFuel()));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("relative_durability"), (condition, itemStack) -> {
			String comparison = condition.getString("comparison");
			double compareTo = condition.getNumber("compare_to").getDouble();
			double amt = Math.abs(CraftItemStack.asNMSCopy(itemStack).getMaxDamage() - CraftItemStack.asNMSCopy(itemStack).getDamageValue()) / CraftItemStack.asNMSCopy(itemStack).getMaxDamage();
			return Comparison.fromString(comparison).compare(amt, compareTo);
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("is_equippable"), (condition, itemStack) -> EnchantTableHandler.wearable.contains(itemStack.getType())));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("is_damageable"), (condition, itemStack) -> CraftItemStack.asCraftCopy(itemStack).handle.isDamageableItem()));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("fireproof"), (condition, itemStack) -> CraftItemStack.asCraftCopy(itemStack).handle.has(DataComponents.FIRE_RESISTANT)));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("enchantment"), (condition, itemStack) -> {
			Enchantment enchantment = CraftRegistry.ENCHANTMENT.get(NamespacedKey.fromString(condition.getString("enchantment")));
			if (enchantment != null) {
				net.minecraft.world.item.enchantment.Enchantment nmsEnchantment = CraftEnchantment.bukkitToMinecraft(enchantment);
				Comparison comparison = Comparison.fromString(condition.getString("comparison"));
				int compare_to = condition.getNumber("compare_to").getInt();

				int level;
				if (nmsEnchantment != null) {
					level = EnchantmentHelper.getItemEnchantmentLevel(nmsEnchantment, CraftItemStack.asNMSCopy(itemStack));
				} else {
					// Get amount of enchantments on the item
					level = CraftItemStack.asNMSCopy(itemStack).getEnchantments().size();
				}

				return comparison.compare(level, compare_to);
			}
			return false;
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("enchantable"), (condition, itemStack) -> CraftItemStack.asNMSCopy(itemStack).isEnchantable()));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("empty"), (condition, itemStack) -> CraftItemStack.asNMSCopy(itemStack).isEmpty()));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("durability"), (condition, itemStack) -> {
			String comparison = condition.getString("comparison");
			double compareTo = condition.getNumber("compare_to").getDouble();
			double amt = CraftItemStack.asNMSCopy(itemStack).getMaxDamage() - CraftItemStack.asNMSCopy(itemStack).getDamageValue();
			return Comparison.fromString(comparison).compare(amt, compareTo);
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("armor_value"), (condition, itemStack) -> {
			String comparison = condition.getString("comparison");
			double compareTo = condition.getNumber("compare_to").getDouble();
			double amt = Utils.getArmorValue(itemStack);
			return Comparison.fromString(comparison).compare(amt, compareTo);
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("amount"), (condition, itemStack) -> {
			String comparison = condition.getString("comparison");
			double compareTo = condition.getNumber("compare_to").getDouble();
			int amt = itemStack.getAmount();
			return Comparison.fromString(comparison).compare(amt, compareTo);
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("fuel"), (condition, itemStack) -> itemStack.getType().isFuel()));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("meat"), (condition, itemStack) -> CraftItemStack.asNMSCopy(itemStack).is(ItemTags.MEAT)));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("nbt"), (condition, itemStack) -> NbtUtils.compareNbt(Utils.ParserUtils.parseJson(new StringReader(condition.getString("nbt")), CompoundTag.CODEC), CraftItemStack.asCraftCopy(itemStack).handle.saveOptional(GenesisMC.server.registryAccess()), true)));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("ingredient"), (condition, itemStack) -> {
			if (itemStack != null && itemStack.getType() != null) {
				Predicate<FactoryJsonObject> returnPred = new Predicate<>() {
					@Override
					public boolean test(FactoryJsonObject ingredientMap) {
						if (ingredientMap.isPresent("item")) {
							String itemValue = ingredientMap.getString("item");
							String item;
							if (itemValue.contains(":")) {
								item = itemValue.split(":")[1];
							} else {
								item = itemValue;
							}
							if (item.contains("orb_of_origin")) {
								return itemStack.isSimilar(OrbOfOrigins.orb);
							}
							return itemStack.getType().equals(Material.valueOf(item.toUpperCase()));
						} else if (ingredientMap.isPresent("tag")) {
							NamespacedKey tag = NamespacedKey.fromString(ingredientMap.getString("tag"));
							TagKey<Item> key = TagKey.create(net.minecraft.core.registries.Registries.ITEM, CraftNamespacedKey.toMinecraft(tag));
							return CraftItemStack.asNMSCopy(itemStack).is(key);
						}

						return false;
					}
				};

				if (condition.isJsonObject("ingredient")) {
					FactoryJsonObject ingredientMap = condition.getJsonObject("ingredient");
					return returnPred.test(ingredientMap);
				} else if (condition.isJsonArray("ingredient")) {
					for (FactoryJsonObject ingredientMap : condition.getJsonArray("ingredient").asJsonObjectList()) {
						if (returnPred.test(ingredientMap)) {
							return true;
						}
					}
				}
			}
			return false;
		}));
		// Doesn't work since 1.20.5 - no more tier level
        /* register(new ConditionFactory(GenesisMC.apoliIdentifier("harvest_level"), (condition, itemStack) -> {
            String comparison = condition.getString("comparison");
            double compareTo = condition.getNumber("compare_to").getDouble();
            return CraftItemStack.asNMSCopy(itemStack).getItem() instanceof TieredItem toolItem
                && Comparison.getFromString(comparison).compare(toolItem.getTier().getLevel(), compareTo);
        })); */
	}

	private void register(ConditionFactory factory) {
		GenesisMC.getPlugin().registry.retrieve(Registries.ITEM_CONDITION).register(factory);
	}

	public class ConditionFactory implements Registrable {
		NamespacedKey key;
		BiPredicate<FactoryJsonObject, ItemStack> test;

		public ConditionFactory(NamespacedKey key, BiPredicate<FactoryJsonObject, ItemStack> test) {
			this.key = key;
			this.test = test;
		}

		public boolean test(FactoryJsonObject condition, ItemStack tester) {
			return test.test(condition, tester);
		}

		@Override
		public NamespacedKey getKey() {
			return key;
		}
	}

}
