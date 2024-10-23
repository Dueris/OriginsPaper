package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.power.type.PreventGameEventPowerType;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventDispatcher;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {

	@WrapWithCondition(method = "gameEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/gameevent/GameEventDispatcher;post(Lnet/minecraft/core/Holder;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/level/gameevent/GameEvent$Context;)V"))
	private boolean apoli$preventGameEvent(GameEventDispatcher manager, Holder<GameEvent> event, Vec3 emitterPos, GameEvent.@NotNull Context emitter) {
		return emitter.sourceEntity() == null
			|| !PowerHolderComponent.withPowerTypes(emitter.sourceEntity(), PreventGameEventPowerType.class, p -> p.doesPrevent(event), PreventGameEventPowerType::executeAction);
	}
}
