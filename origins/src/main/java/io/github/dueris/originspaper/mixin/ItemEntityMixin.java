package io.github.dueris.originspaper.mixin;

import com.dragoncommissions.mixbukkit.api.shellcode.impl.api.CallbackInfo;
import io.github.dueris.originspaper.power.PreventItemPickupPower;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {

	@Inject(method = "playerTouch", locator = At.Value.ADD_INVENTORY_STACK)
	public static boolean apoli$preventItemPickup(ItemEntity instance, Player player, CallbackInfo info) {
		if (PreventItemPickupPower.doesPrevent(instance, player)) {
			info.setReturned(true);
			info.setReturnValue(false);
			return false;
		}
		return true;
	}
}
