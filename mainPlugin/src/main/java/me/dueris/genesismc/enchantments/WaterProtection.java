package me.dueris.genesismc.enchantments;

import io.papermc.paper.enchantments.EnchantmentRarity;
import me.dueris.genesismc.GenesisMC;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.EntityCategory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@Deprecated(forRemoval = true)
public class WaterProtection extends Enchantment {
    public WaterProtection() {
        super();
    }

    @Override
    public @NotNull String getName() {
        return "Water Protection";
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public @NotNull EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.WEARABLE;
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public boolean conflictsWith(@NotNull Enchantment enchantment) {
        for(Enchantment enchant : Anvil.conflictenchantments){
            if(enchant == enchantment){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canEnchantItem(@NotNull ItemStack itemStack) {
        return true;
    }

    @Override
    public @NotNull Component displayName(int i) {
        return Component.text("Water Protection");
    }

    @Override
    public boolean isTradeable() {
        return true;
    }

    @Override
    public boolean isDiscoverable() {
        return true;
    }

    @Override
    public @NotNull EnchantmentRarity getRarity() {
        return EnchantmentRarity.COMMON;
    }

    @Override
    public float getDamageIncrease(int i, @NotNull EntityCategory entityCategory) {
        return 0;
    }

    @Override
    public @NotNull Set<EquipmentSlot> getActiveSlots() {
        return null;
    }

    @Override
    public @NotNull String translationKey() {
        return null;
    }

    @Override
    public int getMinModifiedCost(int level) {
        return level * 1;
    }

    @Override
    public int getMaxModifiedCost(int level) {
        return level * 2;
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return NamespacedKey.fromString("origins:water_protection");
    }
}
