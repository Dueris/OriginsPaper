package io.github.dueris.originspaper.mixin;

import com.dragoncommissions.mixbukkit.api.shellcode.impl.api.CallbackInfo;
import io.github.dueris.originspaper.power.type.simple.ScareCreepersPower;
import net.minecraft.world.entity.monster.Creeper;

@Mixin(Creeper.class)
public class CreeperMixin {

	@Inject(method = "registerGoals", locator = At.Value.RETURN)
	public static void origins$scareCreepers(Creeper creeper, CallbackInfo info) {
		ScareCreepersPower.modifyGoals(creeper);
	}
}
