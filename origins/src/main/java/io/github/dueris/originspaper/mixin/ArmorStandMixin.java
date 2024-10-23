package io.github.dueris.originspaper.mixin;

import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.power.type.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorStand.class)
public abstract class ArmorStandMixin extends LivingEntity {

	private ArmorStandMixin(EntityType<? extends LivingEntity> entityType, Level world) {
		super(entityType, world);
	}

	@Inject(method = "hurt", at = @At("RETURN"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;isCreativePlayer()Z")))
	private void apoli$invokeHitActions(DamageSource source, float amount, @NotNull CallbackInfoReturnable<Boolean> cir) {

		if (!cir.getReturnValue()) {
			return;
		}

		Entity attacker = source.getEntity();

		PowerHolderComponent.withPowerTypes(this, ActionWhenHitPowerType.class, p -> p.doesApply(attacker, source, amount), p -> p.whenHit(attacker));
		PowerHolderComponent.withPowerTypes(attacker, ActionOnHitPowerType.class, p -> p.doesApply(this, source, amount), p -> p.onHit(this));

		PowerHolderComponent.withPowerTypes(this, SelfActionWhenHitPowerType.class, p -> p.doesApply(source, amount), SelfActionWhenHitPowerType::whenHit);
		PowerHolderComponent.withPowerTypes(this, AttackerActionWhenHitPowerType.class, p -> p.doesApply(source, amount), p -> p.whenHit(attacker));

		PowerHolderComponent.withPowerTypes(attacker, SelfActionOnHitPowerType.class, p -> p.doesApply(this, source, amount), SelfActionOnHitPowerType::onHit);
		PowerHolderComponent.withPowerTypes(attacker, TargetActionOnHitPowerType.class, p -> p.doesApply(this, source, amount), p -> p.onHit(this));

	}

}

