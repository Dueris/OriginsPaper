package io.github.dueris.originspaper.mixin;

import io.github.dueris.originspaper.access.PowerCraftingInventory;
import io.github.dueris.originspaper.power.factory.PowerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.CraftingInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CraftingInput.class)
public abstract class CraftingInputMixin implements PowerCraftingInventory {

	@Unique
	private PowerType apoli$cachedPowerType;

	@Unique
	private Player apoli$cachedPlayer;

	@Override
	public PowerType apoli$getPowerType() {
		return apoli$cachedPowerType;
	}

	@Override
	public void apoli$setPowerType(PowerType powerType) {
		this.apoli$cachedPowerType = powerType;
	}

	@Override
	public Player apoli$getPlayer() {
		return apoli$cachedPlayer;
	}

	@Override
	public void apoli$setPlayer(Player player) {
		this.apoli$cachedPlayer = player;
	}

}
