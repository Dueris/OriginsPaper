package io.github.dueris.originspaper.mixin;

import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.power.type.ModifyExperiencePowerType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrb.class)
public class ExperienceOrbMixin {

	@Shadow
	public int count;

	@Inject(method = "playerTouch", at = @At(value = "INVOKE", target = "Lorg/bukkit/craftbukkit/event/CraftEventFactory;callPlayerExpChangeEvent(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/ExperienceOrb;)Lorg/bukkit/event/player/PlayerExpChangeEvent;", shift = At.Shift.BEFORE))
	public void apoli$modifyExperienceGain(Player player, CallbackInfo ci) {
		this.count = Math.round(PowerHolderComponent.modify(player, ModifyExperiencePowerType.class, count));
	}
}
