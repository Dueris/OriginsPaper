package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.power.type.*;
import net.minecraft.advancements.critereon.PlayerHurtEntityTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Interaction.class)
public abstract class InteractionMixin extends Entity {

	private InteractionMixin(EntityType<?> type, Level world) {
		super(type, world);
	}

	@WrapWithCondition(method = "skipAttackInteraction", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/critereon/PlayerHurtEntityTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;FFZ)V"))
	private boolean apoli$cacheDamageData(PlayerHurtEntityTrigger criterion, ServerPlayer player, Entity entity, DamageSource source, float dealt, float taken, boolean blocked, @Share("damageSource") @NotNull LocalRef<DamageSource> damageSourceRef, @Share("damageDealt") @NotNull LocalFloatRef damageDealtRef, @Share("damageTaken") @NotNull LocalFloatRef damageTakenRef) {

		damageSourceRef.set(source);

		damageDealtRef.set(dealt);
		damageTakenRef.set(taken);

		return true;

	}

	@Inject(method = "skipAttackInteraction", at = @At(value = "RETURN", ordinal = 0))
	private void apoli$invokeHitActions(Entity attacker, CallbackInfoReturnable<Boolean> cir, @Share("damageSource") @NotNull LocalRef<DamageSource> damageSourceRef, @Share("damageDealt") @NotNull LocalFloatRef damageDealtRef, @Share("damageTaken") @NotNull LocalFloatRef damageTakenRef) {

		DamageSource damageSource = damageSourceRef.get();

		float damageDealt = damageDealtRef.get();
		float damageTaken = damageTakenRef.get();

		PowerHolderComponent.withPowerTypes(this, ActionWhenHitPowerType.class, p -> p.doesApply(attacker, damageSource, damageTaken), p -> p.whenHit(attacker));
		PowerHolderComponent.withPowerTypes(attacker, ActionOnHitPowerType.class, p -> p.doesApply(this, damageSource, damageDealt), p -> p.onHit(this));

		PowerHolderComponent.withPowerTypes(this, SelfActionWhenHitPowerType.class, p -> p.doesApply(damageSource, damageTaken), SelfActionWhenHitPowerType::whenHit);
		PowerHolderComponent.withPowerTypes(this, AttackerActionWhenHitPowerType.class, p -> p.doesApply(damageSource, damageTaken), p -> p.whenHit(attacker));

		PowerHolderComponent.withPowerTypes(attacker, SelfActionOnHitPowerType.class, p -> p.doesApply(this, damageSource, damageDealt), SelfActionOnHitPowerType::onHit);
		PowerHolderComponent.withPowerTypes(attacker, TargetActionOnHitPowerType.class, p -> p.doesApply(this, damageSource, damageDealt), p -> p.onHit(this));

	}

}

