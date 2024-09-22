package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static io.github.dueris.originspaper.power.type.FireProjectilePower.ENDERIAN_PEARLS;

@Mixin(ThrownEnderpearl.class)
public class ThrownEnderpearlMixin {

	@WrapOperation(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
	public boolean origins$enderianPearl(Entity entity, DamageSource source, float amount, Operation<Boolean> original) {
		ThrownEnderpearl pearl = (ThrownEnderpearl) (Object) this;
		if (ENDERIAN_PEARLS.contains(pearl)) {
			ENDERIAN_PEARLS.remove(pearl);
			return false;
		}

		return original.call(entity, source, amount);
	}
}
