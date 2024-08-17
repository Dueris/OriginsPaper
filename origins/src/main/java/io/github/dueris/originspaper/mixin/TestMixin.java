package io.github.dueris.originspaper.mixin;

import com.dragoncommissions.mixbukkit.api.shellcode.impl.api.CallbackInfo;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.monster.EnderMan;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public class TestMixin {

	public static void hurt(EnderMan test, DamageSource damageSource, float damage, CallbackInfo callBackInfo) {
		Bukkit.broadcastMessage(test.getDisplayName().getString() + " gets hurt from " + damageSource.getMsgId() + "  (Damage amount: " + damage + "), but i say neigh");
		callBackInfo.setReturnValue(false);
	}
}
