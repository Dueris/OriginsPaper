package io.github.dueris.originspaper.mixin;

import com.dragoncommissions.mixbukkit.api.shellcode.impl.api.CallbackInfo;
import io.github.dueris.originspaper.power.PreventItemPickupPower;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;

@Mixin(Mob.class)
public class MobMixin {

	@Inject(method = "aiStep", locator = At.Value.PICKUP_ITEM)
	public static boolean apoli$onItemPickup(Mob instance, ItemEntity itemEntity, CallbackInfo info) {

		if (PreventItemPickupPower.doesPrevent(itemEntity, instance)) {
			info.setReturnValue(false);
			info.setReturned(true);

			instance.level().getProfiler().pop();
			return false;
		}

		return true;

	}
}
