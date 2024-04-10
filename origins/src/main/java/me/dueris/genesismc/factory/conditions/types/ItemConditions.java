package me.dueris.genesismc.factory.conditions.types;

import com.mojang.brigadier.StringReader;
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
import net.minecraft.world.item.TieredItem;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftNamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiPredicate;

public class ItemConditions {

    public void prep() {
        register(new ConditionFactory(GenesisMC.apoliIdentifier("food"), (condition, itemStack) -> itemStack.getType().isEdible()));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("smeltable"), (condition, itemStack) -> itemStack.getType().isFuel()));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("relative_durability"), (condition, itemStack) -> {
            String comparison = condition.get("comparison").toString();
            double compareTo = Double.parseDouble(condition.get("compare_to").toString());
            double amt = Math.abs(CraftItemStack.asNMSCopy(itemStack).getMaxDamage() - CraftItemStack.asNMSCopy(itemStack).getDamageValue()) / CraftItemStack.asNMSCopy(itemStack).getMaxDamage();
            return Comparison.getFromString(comparison).compare(amt, compareTo);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("is_equippable"), (condition, itemStack) -> EnchantTableHandler.wearable.contains(itemStack.getType())));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("is_damageable"), (condition, itemStack) -> CraftItemStack.asCraftCopy(itemStack).handle.isDamageableItem()));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("fireproof"), (condition, itemStack) -> CraftItemStack.asCraftCopy(itemStack).handle.getItem().isFireResistant()));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("enchantment"), (condition, itemStack) -> {
            String comparison = condition.get("comparison").toString();
            double compareTo = Double.parseDouble(condition.get("compare_to").toString());
            for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
                if (enchantment.getName().equalsIgnoreCase(String.valueOf(condition.getOrDefault("enchantment", enchantment.getName())))) {
                    int amt = itemStack.getEnchantments().get(enchantment);
                    return Comparison.getFromString(comparison).compare(amt, compareTo);
                }
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("enchantable"), (condition, itemStack) -> CraftItemStack.asNMSCopy(itemStack).isEnchantable()));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("empty"), (condition, itemStack) -> CraftItemStack.asNMSCopy(itemStack).isEmpty()));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("durability"), (condition, itemStack) -> {
            String comparison = condition.get("comparison").toString();
            double compareTo = Double.parseDouble(condition.get("compare_to").toString());
            double amt = CraftItemStack.asNMSCopy(itemStack).getMaxDamage() - CraftItemStack.asNMSCopy(itemStack).getDamageValue();
            return Comparison.getFromString(comparison).compare(amt, compareTo);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("armor_value"), (condition, itemStack) -> {
            String comparison = condition.get("comparison").toString();
            double compareTo = Double.parseDouble(condition.get("compare_to").toString());
            double amt = Utils.getArmorValue(itemStack);
            return Comparison.getFromString(comparison).compare(amt, compareTo);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("amount"), (condition, itemStack) -> {
            String comparison = condition.get("comparison").toString();
            double compareTo = Double.parseDouble(condition.get("compare_to").toString());
            int amt = itemStack.getAmount();
            return Comparison.getFromString(comparison).compare(amt, compareTo);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("fuel"), (condition, itemStack) -> itemStack.getType().isFuel()));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("meat"), (condition, itemStack) -> CraftItemStack.asNMSCopy(itemStack).getItem().getFoodProperties() != null ? CraftItemStack.asNMSCopy(itemStack).getItem().getFoodProperties().isMeat() : false));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("nbt"), (condition, itemStack) -> NbtUtils.compareNbt(MiscUtils.ParserUtils.parseJson(new StringReader(condition.get("nbt").toString()), CompoundTag.CODEC), CraftItemStack.asCraftCopy(itemStack).handle.getTag(), true)));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("ingredient"), (condition, itemStack) -> {
            if (itemStack != null && itemStack.getType() != null) {
                if (condition.containsKey("ingredient")) {
                    JSONObject ingredientMap = (JSONObject) condition.get("ingredient");
                    if (ingredientMap.containsKey("item")) {
                        String itemValue = ingredientMap.get("item").toString();
                        String item = null;
                        if (itemValue.contains(":")) {
                            item = itemValue.split(":")[1];
                        } else {
                            item = itemValue;
                        }
                        if (item.contains("orb_of_origin")) {
                            return itemStack.isSimilar(OrbOfOrigins.orb);
                        }
                        return itemStack.getType().equals(Material.valueOf(item.toUpperCase()));
                    } else if (ingredientMap.containsKey("tag")) {
                        NamespacedKey tag = NamespacedKey.fromString(ingredientMap.get("tag").toString());
                        TagKey key = TagKey.create(net.minecraft.core.registries.Registries.ITEM, CraftNamespacedKey.toMinecraft(tag));
                        return CraftItemStack.asCraftCopy(itemStack).handle.is(key);
                    }
                }
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("harvest_level"), (condition, itemStack) -> {
            String comparison = condition.get("comparison").toString();
            double compareTo = Double.parseDouble(condition.get("compare_to").toString());
            return CraftItemStack.asNMSCopy(itemStack).getItem() instanceof TieredItem toolItem
                && Comparison.getFromString(comparison).compare(toolItem.getTier().getLevel(), compareTo);
        }));
    }

    private void register(ConditionFactory factory) {
        GenesisMC.getPlugin().registry.retrieve(Registries.ITEM_CONDITION).register(factory);
    }

    public class ConditionFactory implements Registerable {
        NamespacedKey key;
        BiPredicate<JSONObject, ItemStack> test;

        public ConditionFactory(NamespacedKey key, BiPredicate<JSONObject, ItemStack> test) {
            this.key = key;
            this.test = test;
        }

        public boolean test(JSONObject condition, ItemStack tester) {
            return test.test(condition, tester);
        }

        @Override
        public NamespacedKey getKey() {
            return key;
        }
    }

}
