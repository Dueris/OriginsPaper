package io.github.dueris.originspaper.mixin;

import com.dragoncommissions.mixbukkit.api.shellcode.impl.api.CallbackInfo;
import io.github.dueris.originspaper.power.type.ActionOnWakeUpPower;
import io.github.dueris.originspaper.power.type.PhasingPower;
import io.github.dueris.originspaper.power.type.simple.LikeWaterPower;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

	@Inject(method = "tick", locator = At.Value.RETURN)
	public static void apoli$likeWater(@NotNull Player instance, CallbackInfo info) {
		LikeWaterPower.tick((org.bukkit.entity.Player) instance.getBukkitEntity());
		if (PowerHolderComponent.hasPowerType(instance.getBukkitEntity(), PhasingPower.class)) {
			instance.noPhysics = true;
		}
	}

	@Inject(method = "turtleHelmetTick", locator = At.Value.HEAD)
	public static void apoli$turtleHelmetTick(@NotNull Player instance, CallbackInfo info) {
		ItemStack itemstack = instance.getItemBySlot(EquipmentSlot.HEAD);

		if (itemstack.is(Items.TURTLE_HELMET) && PowerHolderComponent.hasPower(instance.getBukkitEntity(), "origins:water_breathing") == instance.isEyeInFluid(FluidTags.WATER)) {
			instance.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 200, 0, false, false, true), EntityPotionEffectEvent.Cause.TURTLE_HELMET);
		}

		info.setReturned(true);

	}

	@Inject(method = "stopSleepInBed", locator = At.Value.HEAD)
	public static void apoli$actionOnWakeUp(Player instance, boolean bl, boolean updateSleepingPlayers, CallbackInfo info) {
		if(!bl && !updateSleepingPlayers && instance.getSleepingPos().isPresent()) {
			BlockPos sleepingPos = instance.getSleepingPos().get();
			PowerHolderComponent.getPowers(instance.getBukkitEntity(), ActionOnWakeUpPower.class).stream().filter(p -> p.doesApply(sleepingPos, instance)).forEach(p -> p.executeActions(sleepingPos, Direction.DOWN, instance));
		}
	}
}
