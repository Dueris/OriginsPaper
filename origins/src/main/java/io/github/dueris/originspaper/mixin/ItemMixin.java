package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.power.type.ModifyFoodPowerType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Item.class)
public abstract class ItemMixin {

	@ModifyExpressionValue(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;canEat(Z)Z"))
	private boolean apoli$makeItemAlwaysEdible(boolean original, Level world, Player user, InteractionHand hand, @Local ItemStack stackInHand) {
		return original || PowerHolderComponent.hasPowerType(user, ModifyFoodPowerType.class, mfp -> mfp.doesMakeAlwaysEdible() && mfp.doesApply(stackInHand));
	}

}
