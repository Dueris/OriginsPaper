package me.dueris.genesismc.factory.conditions.types;

import com.mojang.brigadier.StringReader;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrable;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.content.OrbOfOrigins;
import me.dueris.genesismc.factory.data.types.Comparison;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.util.EntityLinkedItemStack;
import me.dueris.genesismc.util.Reflector;
import me.dueris.genesismc.util.Util;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class ItemConditions {

	public void registerConditions() {
		register(new ConditionFactory(GenesisMC.apoliIdentifier("food"), (condition, itemStack) -> itemStack.getType().isEdible()));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("smeltable"), (condition, itemStack) -> itemStack.getType().isFuel()));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("relative_durability"), (condition, itemStack) -> {
			String comparison = condition.getString("comparison");
			double compareTo = condition.getNumber("compare_to").getDouble();
			double amt = Math.abs(CraftItemStack.asNMSCopy(itemStack).getMaxDamage() - CraftItemStack.asNMSCopy(itemStack).getDamageValue()) / CraftItemStack.asNMSCopy(itemStack).getMaxDamage();
			return Comparison.fromString(comparison).compare(amt, compareTo);
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("is_equippable"), (condition, itemStack) -> {
			net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(itemStack);
			EquipmentSlot slot = condition.getEnumValueOrDefault("equipment_slot", EquipmentSlot.class, null);

			if (slot == null) {
				return Equipable.get(nms) != null;
			}
			return LivingEntity.getEquipmentSlotForItem(nms) == slot;
		}));
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
			double amt = Util.getArmorValue(itemStack);
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
		register(new ConditionFactory(GenesisMC.apoliIdentifier("nbt"), (condition, itemStack) -> NbtUtils.compareNbt(Util.ParserUtils.parseJson(new StringReader(condition.getString("nbt")), CompoundTag.CODEC), CraftItemStack.asCraftCopy(itemStack).handle.saveOptional(GenesisMC.server.registryAccess()), true)));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("rarity"), (condition, itemStack) -> CraftItemStack.asNMSCopy(itemStack).getRarity().equals(condition.getEnumValue("rarity", Rarity.class))));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("stackable"), (condition, itemStack) -> CraftItemStack.asNMSCopy(itemStack).isStackable()));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("used_on_release"), (condition, itemStack) -> CraftItemStack.asNMSCopy(itemStack).useOnRelease()));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("ingredient"), (condition, itemStack) -> {
			if (itemStack != null && itemStack.getType() != null) {
				Predicate<FactoryJsonObject> returnPred = ingredientMap -> {
					if (ingredientMap.isPresent("item")) {
						String itemValue = ingredientMap.getString("item");
						if (itemValue.contains("orb_of_origin")) {
							return itemStack.isSimilar(OrbOfOrigins.orb);
						}
						return itemStack.getType().equals(CraftRegistry.MATERIAL.get(NamespacedKey.fromString(itemValue)));
					} else if (ingredientMap.isPresent("tag")) {
						NamespacedKey tag = NamespacedKey.fromString(ingredientMap.getString("tag"));
						TagKey<Item> key = TagKey.create(net.minecraft.core.registries.Registries.ITEM, CraftNamespacedKey.toMinecraft(tag));
						return CraftItemStack.asNMSCopy(itemStack).is(key);
					}

					return false;
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
		register(new ConditionFactory(GenesisMC.apoliIdentifier("item_cooldown"), (condition, item) -> {
			net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(item);
			Entity entity = ((CraftEntity) EntityLinkedItemStack.getInstance().getHolder(item)).getHandle();

			if (nms.isEmpty() || !(entity instanceof Player player)) return false;
			Item i = nms.getItem();
			ItemCooldowns cooldowns = player.getCooldowns();
			Map<Item, ItemCooldowns.CooldownInstance> instanceMap = cooldowns.cooldowns;
			ItemCooldowns.CooldownInstance cooldownEntry = instanceMap.get(i);
			return cooldownEntry != null &&
				Comparison.fromString(condition.getString("comparison")).compare(cooldownEntry.endTime - Reflector.accessField("startTime", ItemCooldowns.CooldownInstance.class, cooldownEntry, int.class), condition.getNumber("compare_to").getInt());
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("relative_item_cooldown"), (condition, item) -> {
			net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(item);
			Entity entity = ((CraftEntity) EntityLinkedItemStack.getInstance().getHolder(item)).getHandle();

			if (nms.isEmpty() || !(entity instanceof Player player)) return false;
			Item i = nms.getItem();
			ItemCooldowns cooldowns = player.getCooldowns();
			return Comparison.fromString(condition.getString("comparison")).compare(cooldowns.getCooldownPercent(i, 0.0F), condition.getNumber("compare_to").getInt());
		}));
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
		public NamespacedKey key() {
			return key;
		}
	}

}
