package io.github.dueris.originspaper.mixin;

import com.dragoncommissions.mixbukkit.api.shellcode.impl.api.CallbackInfo;
import io.github.dueris.originspaper.power.PreventGameEventPower;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {

	@Inject(method = "gameEvent", locator = At.Value.HEAD)
	public static void apoli$preventGameEvent(ServerLevel level, Holder<GameEvent> event, Vec3 emitterPos, GameEvent.Context emitter, CallbackInfo info) {
		if (emitter.sourceEntity() != null &&
			PowerHolderComponent.doesHaveConditionedPower(emitter.sourceEntity().getBukkitEntity(), PreventGameEventPower.class, p -> {
				boolean cancel = false;
				if (p.doesPrevent(event)) {
					p.executeAction(emitter.sourceEntity());
					cancel = true;
				}
				return cancel;
			})) {
			info.setReturned(true);
			info.setReturnValue(false);
		}
	}
}
