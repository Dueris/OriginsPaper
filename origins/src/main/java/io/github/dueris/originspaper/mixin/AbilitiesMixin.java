package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.dueris.originspaper.access.PlayerTiedAbilities;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.power.type.ModifyAirSpeedPowerType;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/**
 * OriginsPaper goes about modifying ability floats differently to sync them with the client more efficiently, and also to link with the player holder
 */
@Mixin(Abilities.class)
public class AbilitiesMixin implements PlayerTiedAbilities {

	@Unique
	private Player apoli$player;

	@Unique
	private float apoli$previousFlightValue;

	@Override
	public Player apoli$getPlayer() {
		return apoli$player;
	}

	@Override
	public void apoli$setPlayer(Player player) {
		apoli$player = player;
	}

	@ModifyReturnValue(method = "getFlyingSpeed", at = @At("RETURN"))
	private float apoli$modifyAirSpeed(float original) {
		return originspaper$computeUpdatedFlightVar(PowerHolderComponent.modify(apoli$player, ModifyAirSpeedPowerType.class, original));
	}

	@Unique
	private float originspaper$computeUpdatedFlightVar(float modified) {
		if (modified != apoli$previousFlightValue) {
			apoli$previousFlightValue = modified;
			this.apoli$player.onUpdateAbilities();
		}
		return modified;
	}
}
