package me.dueris.genesismc.enchantments;

import com.google.common.annotations.Beta;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

@Beta
public class WaterProtectionNMSImpl extends Enchantment {
    public WaterProtectionNMSImpl(Rarity weight, EnchantmentCategory target, EquipmentSlot[] slotTypes) {
        super(weight, target, slotTypes);
    }

    @Override
    public int getMinCost(int level) {
        return level * 25;
    }

    @Override
    public int getMaxCost(int level) {
        return this.getMinCost(level) + 50;
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public boolean isTradeable() {
        return super.isTradeable();
    }

    @Override
    public int getMaxLevel() {
        return super.getMaxLevel();
    }

    @Override
    public int getMinLevel() {
        return super.getMinLevel();
    }
}
