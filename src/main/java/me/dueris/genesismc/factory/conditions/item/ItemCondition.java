package me.dueris.genesismc.factory.conditions.item;

import com.mojang.brigadier.StringReader;
import me.dueris.genesismc.enchantments.EnchantTable;
import me.dueris.genesismc.factory.TagRegistry;
import me.dueris.genesismc.factory.conditions.Condition;
import me.dueris.genesismc.factory.powers.player.RestrictArmor;
import me.dueris.genesismc.items.OrbOfOrigins;
import me.dueris.genesismc.utils.ArmorUtils;
import me.dueris.genesismc.utils.Utils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import org.bukkit.Fluid;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.util.*;

import static me.dueris.genesismc.factory.conditions.ConditionExecutor.getResult;

public class ItemCondition implements Condition {
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
//        nonMeatMaterials.add(Material.BAMBOO);

        nonMeatMaterials.add(Material.KELP);
        nonMeatMaterials.add(Material.DRIED_KELP);
        nonMeatMaterials.add(Material.SEAGRASS);

        // nonMeatMaterials.add(Material.MILK_BUCKET); // allow milk buckets
//        nonMeatMaterials.add(Material.EGG);

//        nonMeatMaterials.add(Material.SUGAR);
        nonMeatMaterials.add(Material.HONEY_BOTTLE);
        nonMeatMaterials.add(Material.GLOW_BERRIES);
        nonMeatMaterials.add(Material.CHORUS_FRUIT);
        nonMeatMaterials.add(Material.BAKED_POTATO);
        nonMeatMaterials.add(Material.SWEET_BERRIES);
        nonMeatMaterials.add(Material.POISONOUS_POTATO);
        nonMeatMaterials.add(Material.PUMPKIN_PIE);
        nonMeatMaterials.add(Material.SUSPICIOUS_STEW);
        nonMeatMaterials.add(Material.MUSHROOM_STEW);

        // nonMeatMaterials.add(Material.WATER_BUCKET); // why were these even here??
        // nonMeatMaterials.add(Material.LAVA_BUCKET); // why were these even here??

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

    @Override
    public String condition_type() {
        return "ITEM_CONDITION";
    }

    @Override
    public Optional<Boolean> check(JSONObject condition, Entity actor, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent entityDamageEvent) {
        if (condition.isEmpty()) return Optional.empty();
        boolean inverted = (boolean) condition.getOrDefault("inverted", false);
        String type = condition.get("type").toString().toLowerCase();
        if (itemStack == null) return Optional.empty();
        switch (type) {
            case "apoli:ingredient" -> {
                if (condition.containsKey("ingredient")) {
                    Map<String, Object> ingredientMap = (Map<String, Object>) condition.get("ingredient");
                    if (ingredientMap.containsKey("item")) {
                        String itemValue = ingredientMap.get("item").toString();
                        String item = null;
                        if (itemValue.contains(":")) {
                            item = itemValue.split(":")[1];
                        } else {
                            item = itemValue;
                        }
                        if (item.contains("orb_of_origin")) {
                            return getResult(inverted, Optional.of(itemStack.isSimilar(OrbOfOrigins.orb)));
                        }
                        return getResult(inverted, Optional.of(itemStack.getType().equals(Material.valueOf(item.toUpperCase()))));
                    } else if (ingredientMap.containsKey("tag")) {
                        try {
                            if (TagRegistry.getRegisteredTagFromFileKey(ingredientMap.get("tag").toString()) != null) {
                                if (!entityTagMappings.containsKey(ingredientMap.get("tag"))) {
                                    entityTagMappings.put(ingredientMap.get("tag").toString(), new ArrayList<>());
                                    for (String mat : TagRegistry.getRegisteredTagFromFileKey(ingredientMap.get("tag").toString())) {
                                        entityTagMappings.get(ingredientMap.get("tag")).add(Material.valueOf(mat.split(":")[1].toUpperCase()));
                                    }
                                } else {
                                    // mappings exist, now we can start stuff
                                    return getResult(inverted, Optional.of(entityTagMappings.get(ingredientMap.get("tag")).contains(itemStack.getType())));
                                }
                            }
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return getResult(inverted, Optional.of(false));
            }
            case "apoli:nbt" -> {
                return getResult(inverted, Optional.of(NbtUtils.compareNbt(Utils.ParserUtils.parseJson(new StringReader(condition.get("nbt").toString()), CompoundTag.CODEC), ((CraftItemStack)itemStack).handle.getTag(), true)));
            }
            case "apoli:meat" -> {
                if (itemStack.getType().isEdible()) {
                    if (inverted) {
                        return getResult(inverted, Optional.of(getNonMeatMaterials().contains(itemStack)));
                    } else {
                        return getResult(inverted, Optional.of(getMeatMaterials().contains(itemStack)));
                    }
                } else {
                    return getResult(inverted, Optional.of(false));
                }
            }
            case "apoli:fuel" -> {
                return getResult(inverted, Optional.of(itemStack.getType().isFuel()));
            }
            case "apoli:amount" -> {
                String comparison = condition.get("comparison").toString();
                double compareTo = Double.parseDouble(condition.get("compare_to").toString());
                int amt = itemStack.getAmount();
                return getResult(inverted, Optional.of(RestrictArmor.compareValues(amt, comparison, compareTo)));
            }
            case "apoli:armor_value" -> {
                String comparison = condition.get("comparison").toString();
                double compareTo = Double.parseDouble(condition.get("compare_to").toString());
                double amt = ArmorUtils.getArmorValue(itemStack);
                return getResult(inverted, Optional.of(RestrictArmor.compareValues(amt, comparison, compareTo)));
            }
            case "apoli:durability" -> {
                String comparison = condition.get("comparison").toString();
                double compareTo = Double.parseDouble(condition.get("compare_to").toString());
                double amt = itemStack.getDurability();
                return getResult(inverted, Optional.of(RestrictArmor.compareValues(amt, comparison, compareTo)));
            }
            case "apoli:empty" -> {
                return getResult(inverted, Optional.of(itemStack.getType().isAir()));
            }
            case "apoli:enchantable" -> {
                return getResult(inverted, Optional.of(ENCHANTABLE_MATERIALS.contains(itemStack.getType())));
            }
            case "apoli:enchantment" -> {
                String comparison = condition.get("comparison").toString();
                double compareTo = Double.parseDouble(condition.get("compare_to").toString());
                for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
                    if (enchantment.getName().equalsIgnoreCase(String.valueOf(condition.getOrDefault("enchantment", enchantment.getName())))) {
                        int amt = itemStack.getEnchantments().get(enchantment);
                        return getResult(inverted, Optional.of(RestrictArmor.compareValues(amt, comparison, compareTo)));
                    }
                }
                return getResult(inverted, Optional.of(false));
            }
            case "apoli:fireproof" -> {
                return getResult(inverted, Optional.of(itemStack.getType().toString().toLowerCase().contains("NETHERITE")));
            }
            case "apoli:is_damageable" -> {
                return getResult(inverted, Optional.of(!itemStack.getType().isBlock()));
            }
            case "apoli:is_equippable" -> {
                return getResult(inverted, Optional.of(EnchantTable.wearable.contains(itemStack.getType())));
            }
            case "apoli:relative_durability" -> {
                String comparison = condition.get("comparison").toString();
                double compareTo = Double.parseDouble(condition.get("compare_to").toString());
                double amt = itemStack.getDurability() / itemStack.getType().getMaxDurability();
                return getResult(inverted, Optional.of(RestrictArmor.compareValues(amt, comparison, compareTo)));
            }
            case "apoli:smeltable" -> {
                return getResult(inverted, Optional.of(itemStack.getType().isFuel()));
            }
            case "apoli:food" -> {
                return getResult(inverted, Optional.of(itemStack.getType().isEdible()));
            }
            default -> {
                return getResult(inverted, Optional.empty());
            }
        }
    }

}
