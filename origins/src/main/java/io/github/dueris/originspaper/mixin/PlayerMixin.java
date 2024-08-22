package io.github.dueris.originspaper.mixin;

import com.dragoncommissions.mixbukkit.api.shellcode.impl.api.CallbackInfo;
import io.github.dueris.originspaper.power.origins.LikeWaterPower;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.jetbrains.annotations.NotNull;

@Mixin(Player.class)
public class PlayerMixin {

	@Inject(method = "tick", locator = At.Value.HEAD)
	public static void apoli$likeWater(Player instance, CallbackInfo info) {
		LikeWaterPower.tick((org.bukkit.entity.Player) instance.getBukkitEntity());
	}

	@Inject(method = "turtleHelmetTick", locator = At.Value.HEAD)
	public static void apoli$turtleHelmetTick(@NotNull Player instance, CallbackInfo info) {
		ItemStack itemstack = instance.getItemBySlot(EquipmentSlot.HEAD);

		if (itemstack.is(Items.TURTLE_HELMET) && PowerHolderComponent.hasPower(instance.getBukkitEntity(), "origins:water_breathing") == instance.isEyeInFluid(FluidTags.WATER)) {
			instance.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 200, 0, false, false, true), EntityPotionEffectEvent.Cause.TURTLE_HELMET);
		}

		info.setReturned(true);

	}
}
