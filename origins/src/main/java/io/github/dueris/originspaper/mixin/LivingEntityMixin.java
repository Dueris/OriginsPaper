package io.github.dueris.originspaper.mixin;

import com.dragoncommissions.mixbukkit.api.shellcode.impl.api.CallbackInfo;
import io.github.dueris.originspaper.data.types.modifier.ModifierUtil;
import io.github.dueris.originspaper.power.ActionOnDeathPower;
import io.github.dueris.originspaper.power.EffectImmunityPower;
import io.github.dueris.originspaper.power.ModifyFallingPowerType;
import io.github.dueris.originspaper.power.PreventDeathPower;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.bukkit.attribute.Attribute;
import org.jetbrains.annotations.NotNull;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

	@Inject(method = "hurt", locator = At.Value.DEATH_CHECK)
	public static void apoli$preventDeath(LivingEntity entity, DamageSource source, float amount, CallbackInfo info) {
		boolean cancel = false;

		if (entity instanceof Player player) {
			for (PreventDeathPower power : PowerHolderComponent.getPowers(player.getBukkitEntity(), PreventDeathPower.class)) {
				if (power.doesApply(source, amount)) {
					power.executeAction(player);
					entity.setHealth(1.0F);
					cancel = true;
				}
			}
		}

		if (cancel) {
			info.setReturnValue(false);
			info.setReturned(true);
		}
	}

	@Inject(method = "hurt", locator = At.Value.ON_DIE)
	public static void apoli$actionOnDeath(LivingEntity entity, DamageSource source, float amount, CallbackInfo info) {
		if (entity instanceof Player player) {
			for (ActionOnDeathPower power : PowerHolderComponent.getPowers(player.getBukkitEntity(), ActionOnDeathPower.class)) {
				if (power.doesApply(source.getEntity(), source, amount, entity)) {
					power.onDeath(source.getEntity(), entity);
				}
			}
		}
	}

	@Inject(method = "canBeAffected", locator = At.Value.RETURN)
	public static void apoli$canHaveStatusEffect(@NotNull LivingEntity entity, MobEffectInstance effect, CallbackInfo info) {
		boolean original = entity.getType().is(EntityTypeTags.IMMUNE_TO_INFESTED) ?
			!effect.is(MobEffects.INFESTED) : (entity.getType().is(EntityTypeTags.IMMUNE_TO_OOZING) ? !effect.is(MobEffects.OOZING) :
			(!entity.getType().is(EntityTypeTags.IGNORES_POISON_AND_REGEN) || !effect.is(MobEffects.REGENERATION) && !effect.is(MobEffects.POISON)));
		boolean applies = true;
		for (EffectImmunityPower power : PowerHolderComponent.getPowers(entity.getBukkitEntity(), EffectImmunityPower.class)) {
			if (power.doesApply(effect)) {
				applies = false;
				break;
			}
		}
		info.setReturned(true);
		info.setReturnValue(
			original && applies
		);
	}

	@Inject(method = "travel", locator = At.Value.HEAD)
	public static void apoli$modifyFalling(LivingEntity instance, Vec3 movementInput, CallbackInfo info) {
		if (instance instanceof Player player) {

			if (player.getDeltaMovement().y >= -0.06D || player.fallDistance <= 0) {
				player.getBukkitEntity().getAttribute(Attribute.GENERIC_GRAVITY).setBaseValue(player.getBukkitEntity().getAttribute(Attribute.GENERIC_GRAVITY).getDefaultValue());
				return;
			}

			for (ModifyFallingPowerType power : PowerHolderComponent.getPowers(player.getBukkitEntity(), ModifyFallingPowerType.class)) {

				if (!power.shouldTakeFallDamage()) {
					player.fallDistance = 0;
				}

				double original = player.getBukkitEntity().getAttribute(Attribute.GENERIC_GRAVITY).getDefaultValue();
				double modified = ModifierUtil.applyModifiers(player, power.getModifiers(), original);

				player.getBukkitEntity().getAttribute(Attribute.GENERIC_GRAVITY).setBaseValue(modified);
			}
		}
	}

}
