package me.dueris.genesismc.enchantments;

import me.dueris.genesismc.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;

public class WaterProtectionEnchantment extends Enchantment {
    String descriptionID = "water_protection";

    public WaterProtectionEnchantment(Rarity weight, EnchantmentCategory target, EquipmentSlot[] slotTypes) {
        super(weight, target, slotTypes);
    }

    @Override
    public int getMinCost(int level) {
        return level * 2;
    }

    @Override
    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    @Override
    protected String getOrCreateDescriptionId() {
        if (this.descriptionID == null) {
            this.descriptionID = Util.makeDescriptionId("enchantment", BuiltInRegistries.ENCHANTMENT.getKey(this));
        }

        return this.descriptionID;
    }

    @Override
    public int getMaxCost(int level) {
        return (level * 2) + 3;
    }

    @Override
    public Component getFullname(int level) {
        MutableComponent mutableComponent = Component.literal("Water Protection");
        mutableComponent.withStyle(ChatFormatting.GRAY);
        if (level != 1 || this.getMaxLevel() != 1) {
            mutableComponent.append(CommonComponents.SPACE).append(Component.translatable("enchantment.level." + level));
        }
        return mutableComponent;
    }

    @Override
    public boolean isTradeable() {
        return super.isTradeable();
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public int getMinLevel() {
        return 1;
    }

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        if (other == this || ((other instanceof ProtectionEnchantment && !(((ProtectionEnchantment) other).type == ProtectionEnchantment.Type.FALL)))) {
            return false;
        }
        return super.checkCompatibility(other);
    }

    @Override
    public int getDamageProtection(int level, DamageSource source) {
        DamageType dmgType = Utils.DAMAGE_REGISTRY.get(new ResourceLocation("origins", "water_protection"));
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return 0;
        } else if (source == Utils.getDamageSource(dmgType)) {
            return level;
        }
        return super.getDamageProtection(level, source);
    }
}
