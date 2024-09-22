package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.dueris.originspaper.power.type.ActionOnWakeUpPower;
import io.github.dueris.originspaper.power.type.ModifyExhaustionPower;
import io.github.dueris.originspaper.power.type.PhasingPower;
import io.github.dueris.originspaper.power.type.simple.LikeWaterPower;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

	@Shadow
	@Final
	private Inventory inventory;

	protected PlayerMixin(EntityType<? extends LivingEntity> type, Level world) {
		super(type, world);
	}

	@Shadow
	public abstract @NotNull CraftEntity getBukkitEntity();

	@Inject(method = "tick", at = @At("RETURN"))
	private void apoli$likeWater(CallbackInfo ci) {
		LikeWaterPower.tick((org.bukkit.entity.Player) getBukkitEntity());
		if (PowerHolderComponent.hasPowerType(getBukkitEntity(), PhasingPower.class)) {
			this.noPhysics = true;
		}
	}

	@ModifyExpressionValue(method = "turtleHelmetTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z"))
	private boolean origins$submergedProxy(boolean original) {
		return PowerHolderComponent.hasPower(getBukkitEntity(), "origins:water_breathing") != original;
	}

	@Inject(method = "stopSleepInBed", at = @At("HEAD"))
	private void apoli$actionOnWakeUp(boolean bl, boolean updateSleepingPlayers, CallbackInfo ci) {
		Player instance = (Player) (Object) this;
		if (!bl && !updateSleepingPlayers && instance.getSleepingPos().isPresent()) {
			BlockPos sleepingPos = instance.getSleepingPos().get();
			PowerHolderComponent.getPowers(instance.getBukkitEntity(), ActionOnWakeUpPower.class).stream().filter(p -> p.doesApply(sleepingPos, instance)).forEach(p -> p.executeActions(sleepingPos, Direction.DOWN, instance));
		}
	}

	@ModifyVariable(method = "causeFoodExhaustion(FLorg/bukkit/event/entity/EntityExhaustionEvent$ExhaustionReason;)V", at = @At("HEAD"), argsOnly = true)
	private float modifyExhaustion(float exhaustionIn) {
		return PowerHolderComponent.modify(this.getBukkitEntity(), ModifyExhaustionPower.class, exhaustionIn);
	}

}
