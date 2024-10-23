package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import io.github.dueris.originspaper.access.ScreenHandlerUsabilityOverride;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.power.PowerManager;
import io.github.dueris.originspaper.power.type.ModifyCraftingPowerType;
import io.github.dueris.originspaper.power.type.RecipePowerType;
import io.github.dueris.originspaper.util.InventoryUtil;
import net.minecraft.util.Tuple;
import net.minecraft.world.Container;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

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

	@Inject(method = "slotChangedCraftingGrid", at = @At("HEAD"))
	private static void originspaper$storeSharedVars(AbstractContainerMenu handler, Level world, Player player, CraftingContainer craftingInventory, ResultContainer resultInventory, RecipeHolder<CraftingRecipe> recipe, CallbackInfo ci,
													 @Share("resulContainerRef") @NotNull LocalRef<ResultContainer> resultContainerLocalRef, @Share("playerRef") @NotNull LocalRef<Player> playerLocalRef) {
		resultContainerLocalRef.set(resultInventory);
		playerLocalRef.set(player);
	}

	@SuppressWarnings("unchecked")
	@WrapOperation(method = "slotChangedCraftingGrid", at = @At(value = "INVOKE", target = "Ljava/util/Optional;isPresent()Z"))
	private static boolean originspaper$storeRecipe(Optional instance, @NotNull Operation<Boolean> original, @Share("recipeHolderRef") @NotNull LocalRef<Optional<RecipeHolder<CraftingRecipe>>> recipeHolderRef) {
		recipeHolderRef.set(instance);
		return original.call(instance);
	}

	@WrapOperation(method = "slotChangedCraftingGrid", at = @At(value = "INVOKE", target = "Lorg/bukkit/craftbukkit/event/CraftEventFactory;callPreCraftEvent(Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/world/Container;Lnet/minecraft/world/item/ItemStack;Lorg/bukkit/inventory/InventoryView;Z)Lnet/minecraft/world/item/ItemStack;"))
	private static ItemStack apoli$modifyCrafting(CraftingContainer matrix, Container resultInventory, ItemStack result, InventoryView lastCraftView, boolean isRepair, Operation<ItemStack> original,
												  @Share("resulContainerRef") @NotNull LocalRef<ResultContainer> resultContainerLocalRef, @Share("playerRef") @NotNull LocalRef<Player> playerLocalRef, @Share("recipeHolderRef") @NotNull LocalRef<Optional<RecipeHolder<CraftingRecipe>>> recipeHolderRef, @Share("notValidRecipeViaPower") LocalRef<Boolean> validRecipeRef) {
		Player player = playerLocalRef.get();
		PowerHolderComponent component = PowerHolderComponent.KEY.getNullable(player);
		RecipeHolder<CraftingRecipe> recipeHolder = recipeHolderRef.get().orElse(null);
		AtomicReference<ItemStack> newResult = new AtomicReference<>(result);
		if (component == null) {
			return original.call(matrix, resultInventory, newResult.get(), lastCraftView, isRepair);
		}

		component.getPowerTypes(ModifyCraftingPowerType.class).stream().filter(p -> p.doesApply(recipeHolder == null ? null : recipeHolder.id(), result)).forEach(p -> {
			if (p.getNewStack() != null) {
				SlotAccess access = InventoryUtil.createStackReference(p.getNewStack().copy());
				if (p.getItemAction() != null) {
					p.getItemAction().accept(new Tuple<>(player.level(), access));
				}
				newResult.set(access.get());
			}
		});
		if (!validRecipeRef.get()) {
			newResult.set(ItemStack.EMPTY);
		}
		return original.call(matrix, resultInventory, newResult.get(), lastCraftView, isRepair);
	}

	@Inject(method = "slotChangedCraftingGrid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/CraftingContainer;setCurrentRecipe(Lnet/minecraft/world/item/crafting/RecipeHolder;)V", shift = At.Shift.AFTER))
	private static void apoli$recipe(AbstractContainerMenu handler, Level world, Player player, @NotNull CraftingContainer craftingInventory, ResultContainer resultInventory, RecipeHolder<CraftingRecipe> recipe, CallbackInfo ci,
									 @Share("notValidRecipeViaPower") @NotNull LocalRef<Boolean> validRecipeRef) {
		validRecipeRef.set(true);
		if (craftingInventory.getCurrentRecipe() != null) {
			RecipeHolder<CraftingRecipe> holder = craftingInventory.getCurrentRecipe();
			if (RecipePowerType.registeredRecipes.contains(holder.id())) {
				PowerHolderComponent component = PowerHolderComponent.KEY.getNullable(player);
				if (component == null) {
					return;
				}

				// Completely discard the recipe, it does nothing, result is nothing too.
				if (!component.hasPower(PowerManager.get(holder.id()))) {
					validRecipeRef.set(false);
				}
			}
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

	@ModifyReturnValue(method = "stillValid", at = @At("RETURN"))
	private boolean apoli$allowUsingViaPower(boolean original, Player playerEntity) {
		return original || this.apoli$canUse();
	}

}
