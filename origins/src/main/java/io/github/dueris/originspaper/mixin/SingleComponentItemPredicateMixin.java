package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.dueris.originspaper.power.type.ModifyEnchantmentLevelPowerType;
import net.minecraft.advancements.critereon.SingleComponentItemPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SingleComponentItemPredicate.class)
public interface SingleComponentItemPredicateMixin {

	@WrapOperation(method = "matches(Lnet/minecraft/world/item/ItemStack;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"))
	private Object apoli$accountForModifiedEnchantments(ItemStack stack, DataComponentType<?> componentType, Operation<Object> original) {

		Object objComponent = original.call(stack, componentType);
		if (componentType == DataComponents.ENCHANTMENTS) {
			//  The resulting object had to be cast to ItemEnchantmentsComponent, but that should be fine since we're checking if the component
			//  type is enchantments anyway, right...? We can't even check if the object is an instance of it since that would just not work if
			//  it's null... -eggohito
			return ModifyEnchantmentLevelPowerType.getAndUpdateModifiedEnchantments(stack, (ItemEnchantments) objComponent);
		} else {
			return objComponent;
		}

	}

}
