package me.dueris.genesismc.mixin.mixins;

import com.google.common.annotations.Beta;
import me.dueris.genesismc.enchantments.WaterProtectionEnchantment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Method;

//@Mixin(Enchantments.class)
@Deprecated
public class EnchantmentsMixin {
//    @Inject(method = "<clinit>", at = @At("RETURN"))
//    private static void addCustomEnchantment(CallbackInfo ci) {
//        try {
//            Enchantment customEnchantment = new WaterProtectionEnchantment(Enchantment.Rarity.UNCOMMON, EnchantmentCategory.ARMOR_HEAD, EquipmentSlot.values());
//
//            Method registerMethod = Enchantments.class.getDeclaredMethod("register", String.class, Enchantment.class);
//            registerMethod.setAccessible(true);
//
//            registerMethod.invoke(null, "water_protection", customEnchantment);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
