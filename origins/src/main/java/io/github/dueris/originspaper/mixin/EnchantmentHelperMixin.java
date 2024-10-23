package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.dueris.originspaper.power.type.ModifyEnchantmentLevelPowerType;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

	@WrapOperation(method = "getItemEnchantmentLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/ItemEnchantments;getLevel(Lnet/minecraft/core/Holder;)I"))
	private static int apoli$modifyEnchantmentsOnLevelQuery(ItemEnchantments enchantmentsComponent, Holder<Enchantment> enchantment, @NotNull Operation<Integer> original, Holder<Enchantment> mEnchantment, ItemStack stack) {
		return original.call(ModifyEnchantmentLevelPowerType.getAndUpdateModifiedEnchantments(stack, enchantmentsComponent), enchantment);
	}

	@ModifyVariable(method = "runIterationOnItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/enchantment/EnchantmentHelper$EnchantmentVisitor;)V", at = @At("STORE"))
	private static ItemEnchantments apoli$modifyEnchantmentsOnForEach(ItemEnchantments original, ItemStack stack) {
		return ModifyEnchantmentLevelPowerType.getAndUpdateModifiedEnchantments(stack, original);
	}

	@ModifyExpressionValue(method = "runIterationOnItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/enchantment/EnchantmentHelper$EnchantmentInSlotVisitor;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"))
	private static boolean apoli$allowWorkableEmptiesInForEach(boolean original, ItemStack stack) {
		return original && !ModifyEnchantmentLevelPowerType.isWorkableEmptyStack(stack);
	}

	@ModifyVariable(method = "runIterationOnItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/enchantment/EnchantmentHelper$EnchantmentInSlotVisitor;)V", at = @At("STORE"))
	private static ItemEnchantments apoli$modifyEnchantmentsOnForEachWithContext(ItemEnchantments original, ItemStack stack) {
		return ModifyEnchantmentLevelPowerType.getAndUpdateModifiedEnchantments(stack, original);
	}

	@ModifyVariable(method = "hasTag", at = @At("STORE"))
	private static ItemEnchantments apoli$modifyEnchantmentsOnInTagQuery(ItemEnchantments original, ItemStack stack) {
		return ModifyEnchantmentLevelPowerType.getAndUpdateModifiedEnchantments(stack, original);
	}

}
