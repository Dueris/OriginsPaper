package io.github.dueris.originspaper.mixin;

import io.github.dueris.originspaper.power.type.ModifyExperiencePower;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrb.class)
public class ExperienceOrbMixin {

	@Shadow
	public int count;

	@Inject(method = "playerTouch", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Player;takeXpDelay:I", ordinal = 1))
	private void modifyXpAmount(@NotNull Player player, CallbackInfo ci) {
		this.count = (int) PowerHolderComponent.modify(player.getBukkitEntity(), ModifyExperiencePower.class, this.count);
	}
}
