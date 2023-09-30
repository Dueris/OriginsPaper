package me.dueris.genesismc.factory.conditions.item;

import me.dueris.genesismc.enchantments.EnchantProtEvent;
import me.dueris.genesismc.enchantments.WaterProtection;
import me.dueris.genesismc.factory.TagRegistry;
import me.dueris.genesismc.factory.conditions.Condition;
import me.dueris.genesismc.factory.powers.player.RestrictArmor;
import me.dueris.genesismc.utils.ArmorUtils;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.Fluid;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static me.dueris.genesismc.factory.conditions.ConditionExecutor.getResult;

public class ItemCondition implements Condition {
    @Override
    public String condition_type() {
        return "ITEM_CONDITION";
    }

    @Override
    public Optional<Boolean> check(HashMap<String, Object> condition, Player p, PowerContainer power, String powerfile, Entity actor, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent entityDamageEvent) {
        if (condition.isEmpty()) return Optional.empty();
        boolean inverted = (boolean) condition.getOrDefault("inverted", false);
        String type = condition.get("type").toString().toLowerCase();
        if (type.equalsIgnoreCase("origins:ingredient")) {
            if (condition.containsKey("ingredient")) {
                Map<String, Object> ingredientMap = (Map<String, Object>) condition.get("ingredient");
                if (ingredientMap.containsKey("item")) {
                    String itemValue = ingredientMap.get("item").toString();
                    String item = null;
                    if(itemValue.contains(":")){
                        item = itemValue.split(":")[1];
                    }else{
                        item = itemValue;
                    }
                    if (itemStack.getType().equals(Material.valueOf(item.toUpperCase()))) {
                        return getResult(inverted, true);
                    } else {
                        return getResult(inverted, false);
                    }
                } else if (ingredientMap.containsKey("tag")) {
                    if(TagRegistry.getRegisteredTagFromFileKey(ingredientMap.get("tag").toString()) != null){
                        for(String mat : TagRegistry.getRegisteredTagFromFileKey(ingredientMap.get("tag").toString())){
                            if(itemStack.getType().equals(Material.valueOf(mat.split(":")[1].toUpperCase()))){
                                return Optional.of(true);
                            }
                        }
                    }
                }
            }
        }
        if (type.equalsIgnoreCase("origins:meat")) {
            if (itemStack.getType().isEdible()) {
                if (inverted) {
                    if (getNonMeatMaterials().contains(itemStack)) {
                        return getResult(false, true);
                    } else {
                        return getResult(false, false);
                    }
                } else {
                    if (getMeatMaterials().contains(itemStack)) {
                        return getResult(false, true);
                    } else {
                        return getResult(false, false);
                    }
                }
            } else {
                return getResult(false, false);
            }
        }
        if (type.equalsIgnoreCase("origins:amount")){
            String comparison = condition.get("comparison").toString();
            double compareTo = Double.parseDouble(condition.get("compare_to").toString());
            int amt = itemStack.getAmount();
            return getResult(inverted, RestrictArmor.compareValues(amt, comparison, compareTo));
        }
        if (type.equalsIgnoreCase("origins:armor_value")){
            String comparison = condition.get("comparison").toString();
            double compareTo = Double.parseDouble(condition.get("compare_to").toString());
            double amt = ArmorUtils.getArmorValue(itemStack);
            return getResult(inverted, RestrictArmor.compareValues(amt, comparison, compareTo));
        }
        if (type.equalsIgnoreCase("origins:durability")){
            String comparison = condition.get("comparison").toString();
            double compareTo = Double.parseDouble(condition.get("compare_to").toString());
            double amt = itemStack.getDurability();
            return getResult(inverted, RestrictArmor.compareValues(amt, comparison, compareTo));
        }
        if (type.equalsIgnoreCase("origins:empty")){
            return getResult(inverted, itemStack.getType().isAir());
        }
        if (type.equalsIgnoreCase("origins:enchantable")){
            return getResult(inverted, ENCHANTABLE_MATERIALS.contains(itemStack.getType()));
        }
        if (type.equalsIgnoreCase("origins:enchantment")){
            String comparison = condition.get("comparison").toString();
            double compareTo = Double.parseDouble(condition.get("compare_to").toString());
            for(Enchantment enchantment : itemStack.getEnchantments().keySet()){
                if(enchantment.getName().equalsIgnoreCase(String.valueOf(condition.getOrDefault("enchantment", enchantment.getName())))){
                    int amt = itemStack.getEnchantments().get(enchantment);
                    return getResult(inverted, RestrictArmor.compareValues(amt, comparison, compareTo));
                }
            }
        }
        if (type.equalsIgnoreCase("origins:fireproof")){
            return getResult(inverted, itemStack.getType().toString().toLowerCase().contains("NETHERITE"));
        }
        if (type.equalsIgnoreCase("origins:is_damageable")){
            return getResult(inverted, !itemStack.getType().isBlock());
        }
        if (type.equalsIgnoreCase("origins:is_equippable")){
            return getResult(inverted, EnchantProtEvent.wearable.contains(itemStack.getType()));
        }
        if (type.equalsIgnoreCase("origins:relative_durability")){
            String comparison = condition.get("comparison").toString();
            double compareTo = Double.parseDouble(condition.get("compare_to").toString());
            double amt = itemStack.getDurability() / itemStack.getType().getMaxDurability();
            return getResult(inverted, RestrictArmor.compareValues(amt, comparison, compareTo));
        }
        if (type.equalsIgnoreCase("origins:smeltable")){
            return getResult(inverted, itemStack.getType().isFuel());
        }
        if (type.equalsIgnoreCase("origins:food")){
            return getResult(inverted, itemStack.getType().isEdible());
        }
        return getResult(inverted, false);
    }

    public static final List<Material> ENCHANTABLE_MATERIALS = new ArrayList<>();

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
        nonMeatMaterials.add(Material.BAMBOO);

        nonMeatMaterials.add(Material.KELP);
        nonMeatMaterials.add(Material.SEAGRASS);

        nonMeatMaterials.add(Material.MILK_BUCKET);
        nonMeatMaterials.add(Material.EGG);

        nonMeatMaterials.add(Material.SUGAR);
        nonMeatMaterials.add(Material.HONEY_BOTTLE);

        nonMeatMaterials.add(Material.WATER_BUCKET);
        nonMeatMaterials.add(Material.LAVA_BUCKET);

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

}
