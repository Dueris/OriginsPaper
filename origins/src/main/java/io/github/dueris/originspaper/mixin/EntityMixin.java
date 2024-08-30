package io.github.dueris.originspaper.mixin;

import com.dragoncommissions.mixbukkit.api.shellcode.impl.api.CallbackInfo;
import io.github.dueris.originspaper.power.type.FireImmunityPower;
import io.github.dueris.originspaper.power.type.InvulnerablePower;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

@Mixin(Entity.class)
public class EntityMixin {

	@Inject(method = "fireImmune", locator = At.Value.RETURN)
	public static void apoli$fireImmunity(@NotNull Entity instance, @NotNull CallbackInfo info) {
		info.setReturned(true);
		info.setReturnValue(instance.getType().fireImmune() ||
			PowerHolderComponent.doesHaveConditionedPower(instance.getBukkitEntity(), FireImmunityPower.class, p -> p.isActive(instance)));
	}

	@Inject(method = "isInvulnerableTo", locator = At.Value.RETURN)
	public static void apoli$invulnerability(@NotNull Entity instance, DamageSource damageSource, @NotNull CallbackInfo info) {
		boolean original = instance.isRemoved() || instance.isInvulnerable() &&
			!damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && !damageSource.isCreativePlayer() ||
			damageSource.is(DamageTypeTags.IS_FIRE) && instance.fireImmune() || damageSource.is(DamageTypeTags.IS_FALL) &&
			instance.getType().is(EntityTypeTags.FALL_DAMAGE_IMMUNE);

		info.setReturnValue(original ||
			PowerHolderComponent.doesHaveConditionedPower(instance.getBukkitEntity(), InvulnerablePower.class, p -> p.doesApply(damageSource)));
		info.setReturned(true);
	}

}
