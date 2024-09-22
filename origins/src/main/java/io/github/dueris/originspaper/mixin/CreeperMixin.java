package io.github.dueris.originspaper.mixin;

import io.github.dueris.originspaper.power.type.simple.ScareCreepersPower;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Creeper.class)
public class CreeperMixin {

	@Inject(method = "registerGoals", at = @At("RETURN"))
	public void origins$scareCreeper(CallbackInfo ci) {
		ScareCreepersPower.modifyGoals((Creeper) (Object) this);
	}
}
