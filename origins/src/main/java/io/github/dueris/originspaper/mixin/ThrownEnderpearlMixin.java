package io.github.dueris.originspaper.mixin;

import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ThrownEnderpearl.class)
public class ThrownEnderpearlMixin {

	// TODO
	/* @WrapOperation(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
	public boolean origins$enderianPearl(Entity entity, DamageSource source, float amount, Operation<Boolean> original) {
		ThrownEnderpearl pearl = (ThrownEnderpearl) (Object) this;
		if (ENDERIAN_PEARLS.contains(pearl)) {
			ENDERIAN_PEARLS.remove(pearl);
			return false;
		}

		return original.call(entity, source, amount);
	} */
}
