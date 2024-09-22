package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.dueris.originspaper.access.PowerCraftingInventory;
import io.github.dueris.originspaper.access.ScreenHandlerUsabilityOverride;
import io.github.dueris.originspaper.power.type.ModifyCraftingPower;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingMenu.class)
public abstract class CraftingMenuMixin extends RecipeBookMenu<CraftingInput, CraftingRecipe> implements ScreenHandlerUsabilityOverride {

	@Shadow
	@Final
	public TransientCraftingContainer craftSlots;
	@Unique
	private boolean apoli$canUse = false;

	public CraftingMenuMixin(MenuType<?> type, int syncId) {
		super(type, syncId);
	}

	@Inject(method = "slotChangedCraftingGrid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/crafting/RecipeHolder;)Ljava/util/Optional;"))
	private static void apoli$clearPowerCraftingInventory(AbstractContainerMenu handler, Level world, Player player, CraftingContainer craftingInventory, ResultContainer resultInventory, @Nullable RecipeHolder<CraftingRecipe> recipe, CallbackInfo ci) {

		if (craftingInventory instanceof PowerCraftingInventory pci) {
			pci.apoli$setPowerType(null);
		}

	}

	@Override
	public boolean apoli$canUse() {
		return this.apoli$canUse;
	}

	@Override
	public void apoli$canUse(boolean canUse) {
		this.apoli$canUse = canUse;
	}

	@ModifyExpressionValue(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At(value = "NEW", target = "(Lnet/minecraft/world/inventory/AbstractContainerMenu;IILnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/inventory/TransientCraftingContainer;"))
	private TransientCraftingContainer apoli$cachePlayerToCraftingInventory(TransientCraftingContainer original, int syncId, Inventory playerInventory) {

		if (original instanceof PowerCraftingInventory pci) {
			pci.apoli$setPlayer(playerInventory.player);
		}

		return original;

	}

	@ModifyReturnValue(method = "stillValid", at = @At("RETURN"))
	private boolean apoli$allowUsingViaPower(boolean original, Player playerEntity) {
		return original || this.apoli$canUse();
	}

	@ModifyVariable(method = "quickMoveStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;copy()Lnet/minecraft/world/item/ItemStack;", shift = At.Shift.AFTER), ordinal = 1)
	private ItemStack apoli$modifyResultStackOnQuickMove(ItemStack itemStack2, Player player, int slotIndex, @Local Slot slot) {

		if (!(craftSlots instanceof PowerCraftingInventory pci && pci.apoli$getPowerType() instanceof ModifyCraftingPower mcp)) {
			return itemStack2;
		}

		int availableSlotIndex = player.getInventory().getSlotWithRemainingSpace(itemStack2);

		if (availableSlotIndex == -1) {
			availableSlotIndex = player.getInventory().getFreeSlot();
		}

		SlotAccess reference = Util.createStackReference(itemStack2);

		if (availableSlotIndex != -1 && slot instanceof ResultSlot) {

			// TODO - MODIFY CRAFTING POWER TYPE

//			((SlotState) slot).apoli$setState(ModifyCraftingPowerType.MODIFIED_RESULT_STACK);
//			mcp.applyAfterCraftingItemAction(reference);

		}

		return reference.get();

	}
}
