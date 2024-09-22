package io.github.dueris.originspaper.mixin;

import io.github.dueris.originspaper.access.PowerCraftingInventory;
import io.github.dueris.originspaper.power.factory.PowerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.TransientCraftingContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(TransientCraftingContainer.class)
public abstract class TransientCraftingContainerMixin implements PowerCraftingInventory {

	@Unique
	private PowerType apoli$CachedPowerType;

	@Unique
	private Player apoli$cachedPlayer;

	@Override
	public void apoli$setPowerType(PowerType powerType) {
		apoli$CachedPowerType = powerType;
	}

	@Override
	public PowerType apoli$getPowerType() {
		return apoli$CachedPowerType;
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
