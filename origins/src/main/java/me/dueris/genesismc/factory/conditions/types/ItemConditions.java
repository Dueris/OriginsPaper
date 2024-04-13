package me.dueris.genesismc.factory.conditions.types;

import com.mojang.brigadier.StringReader;
import me.dueris.calio.builder.inst.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registerable;
import me.dueris.calio.util.MiscUtils;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.content.OrbOfOrigins;
import me.dueris.genesismc.content.enchantment.EnchantTableHandler;
import me.dueris.genesismc.factory.data.types.Comparison;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.util.Utils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.CraftRegistry;
import org.bukkit.craftbukkit.v1_20_R3.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftNamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.BiPredicate;

public class ItemConditions {

    public void prep() {
        register(new ConditionFactory(GenesisMC.apoliIdentifier("food"), (condition, itemStack) -> itemStack.getType().isEdible()));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("smeltable"), (condition, itemStack) -> itemStack.getType().isFuel()));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("relative_durability"), (condition, itemStack) -> {
            String comparison = condition.getString("comparison");
            double compareTo = condition.getNumber("compare_to").getDouble();
            double amt = Math.abs(CraftItemStack.asNMSCopy(itemStack).getMaxDamage() - CraftItemStack.asNMSCopy(itemStack).getDamageValue()) / CraftItemStack.asNMSCopy(itemStack).getMaxDamage();
            return Comparison.getFromString(comparison).compare(amt, compareTo);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("is_equippable"), (condition, itemStack) -> EnchantTableHandler.wearable.contains(itemStack.getType())));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("is_damageable"), (condition, itemStack) -> CraftItemStack.asCraftCopy(itemStack).handle.isDamageableItem()));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("fireproof"), (condition, itemStack) -> CraftItemStack.asCraftCopy(itemStack).handle.getItem().isFireResistant()));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("enchantment"), (condition, itemStack) -> {
            Enchantment enchantment = CraftRegistry.ENCHANTMENT.get(NamespacedKey.fromString(condition.getString("enchantment")));
            if (enchantment != null) {
                net.minecraft.world.item.enchantment.Enchantment nmsEnchantment = CraftEnchantment.bukkitToMinecraft(enchantment);
                Comparison comparison = Comparison.getFromString(condition.getString("comparison"));
                int compare_to = Utils.getToInt(condition.getString("compare_to"));

                int level;
                if (nmsEnchantment != null) {
                    level = EnchantmentHelper.getItemEnchantmentLevel(nmsEnchantment, CraftItemStack.asNMSCopy(itemStack));
                } else {
                    Map<net.minecraft.world.item.enchantment.Enchantment, Integer> enchantmentIntegerMap = EnchantmentHelper.getEnchantments(CraftItemStack.asNMSCopy(itemStack));
                    level = enchantmentIntegerMap.size();
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
            return Comparison.getFromString(comparison).compare(amt, compareTo);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("armor_value"), (condition, itemStack) -> {
            String comparison = condition.getString("comparison");
            double compareTo = condition.getNumber("compare_to").getDouble();
            double amt = Utils.getArmorValue(itemStack);
            return Comparison.getFromString(comparison).compare(amt, compareTo);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("amount"), (condition, itemStack) -> {
            String comparison = condition.getString("comparison");
            double compareTo = condition.getNumber("compare_to").getDouble();
            int amt = itemStack.getAmount();
            return Comparison.getFromString(comparison).compare(amt, compareTo);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("fuel"), (condition, itemStack) -> itemStack.getType().isFuel()));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("meat"), (condition, itemStack) -> CraftItemStack.asNMSCopy(itemStack).getItem().getFoodProperties() != null ? CraftItemStack.asNMSCopy(itemStack).getItem().getFoodProperties().isMeat() : false));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("nbt"), (condition, itemStack) -> NbtUtils.compareNbt(MiscUtils.ParserUtils.parseJson(new StringReader(condition.getString("nbt")), CompoundTag.CODEC), CraftItemStack.asCraftCopy(itemStack).handle.getTag(), true)));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("ingredient"), (condition, itemStack) -> {
            if (itemStack != null && itemStack.getType() != null && CraftItemStack.asCraftCopy(itemStack).handle != null) {
                if (condition.isPresent("ingredient")) {
                    FactoryJsonObject ingredientMap = condition.getJsonObject("ingredient");
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
                        return CraftItemStack.asCraftCopy(itemStack).handle.is(key);
                    }
                }
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("harvest_level"), (condition, itemStack) -> {
            String comparison = condition.getString("comparison");
            double compareTo = condition.getNumber("compare_to").getDouble();
            return CraftItemStack.asNMSCopy(itemStack).getItem() instanceof TieredItem toolItem
                && Comparison.getFromString(comparison).compare(toolItem.getTier().getLevel(), compareTo);
        }));
    }

    private void register(ConditionFactory factory) {
        GenesisMC.getPlugin().registry.retrieve(Registries.ITEM_CONDITION).register(factory);
    }

    public class ConditionFactory implements Registerable {
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
