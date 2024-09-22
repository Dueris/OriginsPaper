package io.github.dueris.originspaper.access;

import io.github.dueris.originspaper.power.factory.PowerType;
import net.minecraft.world.entity.player.Player;

public interface PowerCraftingInventory {

	void apoli$setPowerType(PowerType powerType);

	PowerType apoli$getPowerType();

	void apoli$setPlayer(Player player);

	Player apoli$getPlayer();

}
