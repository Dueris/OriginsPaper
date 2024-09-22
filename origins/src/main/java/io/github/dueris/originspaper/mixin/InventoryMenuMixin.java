package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.dueris.originspaper.access.PowerCraftingInventory;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.TransientCraftingContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InventoryMenu.class)
public class InventoryMenuMixin {

	@ModifyExpressionValue(method = "<init>", at = @At(value = "NEW", target = "(Lnet/minecraft/world/inventory/AbstractContainerMenu;IILnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/inventory/TransientCraftingContainer;"))
	private TransientCraftingContainer apoli$cachePlayerToCraftingInventory(TransientCraftingContainer original, Inventory playerInventory) {

		if (original instanceof PowerCraftingInventory pci) {
			pci.apoli$setPlayer(playerInventory.player);
		}

		return original;

	}

}
