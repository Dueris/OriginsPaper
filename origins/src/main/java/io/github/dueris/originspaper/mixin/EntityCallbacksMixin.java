package io.github.dueris.originspaper.mixin;

import io.github.dueris.originspaper.power.PowerIntegration;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/server/level/ServerLevel$EntityCallbacks")
public class EntityCallbacksMixin {

	@Inject(method = "onTrackingStart(Lnet/minecraft/world/entity/Entity;)V", at = @At("TAIL"))
	public void invokeEntityLoadCallback(Entity entity, CallbackInfo ci) {
		PowerIntegration.callEntityAddCallback(entity);
	}

	@Inject(method = "onTrackingEnd(Lnet/minecraft/world/entity/Entity;)V", at = @At("TAIL"))
	public void invokeEntityUnloadCallback(Entity entity, CallbackInfo ci) {
		PowerIntegration.callEntityRemoveCallback(entity);
	}
}
