package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.dueris.originspaper.power.type.GameEventListenerPowerType;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(VibrationSystem.class)
public interface VibrationSystemMixin {

	@Mixin(VibrationSystem.User.class)
	interface CallbackMixin {

		@WrapOperation(method = "isValidVibration", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Holder;is(Lnet/minecraft/tags/TagKey;)Z", ordinal = 0))
		private boolean apoli$accountForPowerCallbacks(Holder<GameEvent> gameEvent, TagKey<GameEvent> gameEventTag, Operation<Boolean> original) {
			return (VibrationSystem.User) this instanceof GameEventListenerPowerType.Callback powerCallback
				? powerCallback.shouldAccept(gameEvent)
				: original.call(gameEvent, gameEventTag);
		}

	}

	@Mixin(VibrationSystem.Ticker.class)
	interface TickerMixin {

		@WrapWithCondition(method = "trySelectAndScheduleVibration", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"))
		private static boolean apoli$onlyShowParticleWhenSpecified(ServerLevel world, ParticleOptions particle, double x, double y, double z, int count, double deltaX, double deltaY, double deltaZ, double speed, VibrationSystem.Data listenerData) {
			return !(listenerData instanceof GameEventListenerPowerType.ListenerData powerListenerData)
				|| powerListenerData.shouldShowParticle();
		}

	}

}
