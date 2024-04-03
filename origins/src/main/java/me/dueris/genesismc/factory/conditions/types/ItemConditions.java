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
    public static final List<Material> ENCHANTABLE_MATERIALS = new ArrayList<>();
    public static HashMap<String, ArrayList<Material>> entityTagMappings = new HashMap<>();

    static {
        // Weapons
        ENCHANTABLE_MATERIALS.add(Material.WOODEN_SWORD);
        ENCHANTABLE_MATERIALS.add(Material.STONE_SWORD);
        ENCHANTABLE_MATERIALS.add(Material.IRON_SWORD);
        ENCHANTABLE_MATERIALS.add(Material.GOLDEN_SWORD);
        ENCHANTABLE_MATERIALS.add(Material.DIAMOND_SWORD);
        ENCHANTABLE_MATERIALS.add(Material.TRIDENT);

        // Tools
        ENCHANTABLE_MATERIALS.add(Material.WOODEN_PICKAXE);
        ENCHANTABLE_MATERIALS.add(Material.STONE_PICKAXE);
        ENCHANTABLE_MATERIALS.add(Material.IRON_PICKAXE);
        ENCHANTABLE_MATERIALS.add(Material.GOLDEN_PICKAXE);
        ENCHANTABLE_MATERIALS.add(Material.DIAMOND_PICKAXE);

        ENCHANTABLE_MATERIALS.add(Material.WOODEN_AXE);
        ENCHANTABLE_MATERIALS.add(Material.STONE_AXE);
        ENCHANTABLE_MATERIALS.add(Material.IRON_AXE);
        ENCHANTABLE_MATERIALS.add(Material.GOLDEN_AXE);
        ENCHANTABLE_MATERIALS.add(Material.DIAMOND_AXE);

        // Armor
        ENCHANTABLE_MATERIALS.add(Material.LEATHER_HELMET);
        ENCHANTABLE_MATERIALS.add(Material.LEATHER_CHESTPLATE);
        ENCHANTABLE_MATERIALS.add(Material.LEATHER_LEGGINGS);
        ENCHANTABLE_MATERIALS.add(Material.LEATHER_BOOTS);

        ENCHANTABLE_MATERIALS.add(Material.CHAINMAIL_HELMET);
        ENCHANTABLE_MATERIALS.add(Material.CHAINMAIL_CHESTPLATE);
        ENCHANTABLE_MATERIALS.add(Material.CHAINMAIL_LEGGINGS);
        ENCHANTABLE_MATERIALS.add(Material.CHAINMAIL_BOOTS);

        ENCHANTABLE_MATERIALS.add(Material.IRON_HELMET);
        ENCHANTABLE_MATERIALS.add(Material.IRON_CHESTPLATE);
        ENCHANTABLE_MATERIALS.add(Material.IRON_LEGGINGS);
        ENCHANTABLE_MATERIALS.add(Material.IRON_BOOTS);

        ENCHANTABLE_MATERIALS.add(Material.DIAMOND_HELMET);
        ENCHANTABLE_MATERIALS.add(Material.DIAMOND_CHESTPLATE);
        ENCHANTABLE_MATERIALS.add(Material.DIAMOND_LEGGINGS);
        ENCHANTABLE_MATERIALS.add(Material.DIAMOND_BOOTS);

        ENCHANTABLE_MATERIALS.add(Material.NETHERITE_HELMET);
        ENCHANTABLE_MATERIALS.add(Material.NETHERITE_CHESTPLATE);
        ENCHANTABLE_MATERIALS.add(Material.NETHERITE_LEGGINGS);
        ENCHANTABLE_MATERIALS.add(Material.NETHERITE_BOOTS);
    }

    public static List<Material> getNonMeatMaterials() {
        List<Material> nonMeatMaterials = new ArrayList<>();

        nonMeatMaterials.add(Material.APPLE);
        nonMeatMaterials.add(Material.CARROT);
        nonMeatMaterials.add(Material.POTATO);
        nonMeatMaterials.add(Material.BEETROOT);
        nonMeatMaterials.add(Material.WHEAT);
        nonMeatMaterials.add(Material.MELON_SLICE);
        nonMeatMaterials.add(Material.PUMPKIN);
        nonMeatMaterials.add(Material.KELP);
        nonMeatMaterials.add(Material.DRIED_KELP);
        nonMeatMaterials.add(Material.SEAGRASS);
        nonMeatMaterials.add(Material.HONEY_BOTTLE);
        nonMeatMaterials.add(Material.GLOW_BERRIES);
        nonMeatMaterials.add(Material.CHORUS_FRUIT);
        nonMeatMaterials.add(Material.BAKED_POTATO);
        nonMeatMaterials.add(Material.SWEET_BERRIES);
        nonMeatMaterials.add(Material.POISONOUS_POTATO);
        nonMeatMaterials.add(Material.PUMPKIN_PIE);
        nonMeatMaterials.add(Material.SUSPICIOUS_STEW);
        nonMeatMaterials.add(Material.MUSHROOM_STEW);
        nonMeatMaterials.add(Material.BREAD);
        nonMeatMaterials.add(Material.CAKE);
        nonMeatMaterials.add(Material.COOKIE);

        return nonMeatMaterials;
    }

    public static List<Material> getMeatMaterials() {
        List<Material> meatMaterials = new ArrayList<>();

        meatMaterials.add(Material.BEEF);
        meatMaterials.add(Material.PORKCHOP);
        meatMaterials.add(Material.MUTTON);
        meatMaterials.add(Material.CHICKEN);
        meatMaterials.add(Material.RABBIT);
        meatMaterials.add(Material.TROPICAL_FISH);
        meatMaterials.add(Material.COD);
        meatMaterials.add(Material.COOKED_COD);
        meatMaterials.add(Material.COOKED_SALMON);
        meatMaterials.add(Material.RABBIT_STEW);
        meatMaterials.add(Material.SPIDER_EYE);
        meatMaterials.add(Material.SALMON);
        meatMaterials.add(Material.PUFFERFISH);

        meatMaterials.add(Material.COOKED_BEEF);
        meatMaterials.add(Material.COOKED_PORKCHOP);
        meatMaterials.add(Material.COOKED_MUTTON);
        meatMaterials.add(Material.COOKED_CHICKEN);
        meatMaterials.add(Material.COOKED_RABBIT);

        meatMaterials.add(Material.ROTTEN_FLESH);

        return meatMaterials;
    }

    public void prep() {
        // Meta conditions, shouldnt execute
        // Meta conditions are added in each file to ensure they dont error and skip them when running
        // a meta condition inside another meta condition
        register(new ConditionFactory(GenesisMC.apoliIdentifier("and"), (condition, obj) -> {
            throw new IllegalStateException("Executor should not be here right now! Report to Dueris!");
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("or"), (condition, obj) -> {
            throw new IllegalStateException("Executor should not be here right now! Report to Dueris!");
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("chance"), (condition, obj) -> {
            throw new IllegalStateException("Executor should not be here right now! Report to Dueris!");
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("constant"), (condition, obj) -> {
            throw new IllegalStateException("Executor should not be here right now! Report to Dueris!");
        }));
        // Meta conditions end
        register(new ConditionFactory(GenesisMC.apoliIdentifier("food"), (condition, itemStack) -> {
            return itemStack.getType().isEdible();
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("smeltable"), (condition, itemStack) -> {
            return itemStack.getType().isFuel();
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("relative_durability"), (condition, itemStack) -> {
            String comparison = condition.get("comparison").toString();
            double compareTo = Double.parseDouble(condition.get("compare_to").toString());
            double amt = itemStack.getDurability() / itemStack.getType().getMaxDurability();
            return Comparison.getFromString(comparison).compare(amt, compareTo);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("is_equippable"), (condition, itemStack) -> {
            return EnchantTableHandler.wearable.contains(itemStack.getType());
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("is_damageable"), (condition, itemStack) -> {
            return CraftItemStack.asCraftCopy(itemStack).handle.isDamageableItem();
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("fireproof"), (condition, itemStack) -> {
            return CraftItemStack.asCraftCopy(itemStack).handle.getItem().isFireResistant();
        }));
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
        register(new ConditionFactory(GenesisMC.apoliIdentifier("enchantable"), (condition, itemStack) -> {
            return ENCHANTABLE_MATERIALS.contains(itemStack.getType());
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("empty"), (condition, itemStack) -> {
            return itemStack.getType().isAir();
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("durability"), (condition, itemStack) -> {
            String comparison = condition.get("comparison").toString();
            double compareTo = Double.parseDouble(condition.get("compare_to").toString());
            double amt = itemStack.getDurability();
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
        register(new ConditionFactory(GenesisMC.apoliIdentifier("fuel"), (condition, itemStack) -> {
            return itemStack.getType().isFuel();
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("meat"), (condition, itemStack) -> {
            return getMeatMaterials().contains(itemStack.getType());
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("nbt"), (condition, itemStack) -> {
            return NbtUtils.compareNbt(MiscUtils.ParserUtils.parseJson(new StringReader(condition.get("nbt").toString()), CompoundTag.CODEC), CraftItemStack.asCraftCopy(itemStack).handle.getTag(), true);
        }));
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
