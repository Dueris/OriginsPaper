package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import io.github.dueris.originspaper.access.EntityLinkedItemStack;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.power.type.*;
import io.github.dueris.originspaper.util.InventoryUtil;
import io.github.dueris.originspaper.util.PriorityPhase;
import io.github.dueris.originspaper.util.StackClickPhase;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nullable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements EntityLinkedItemStack {

	@Unique
	private Entity apoli$holdingEntity;

	@Unique
	private boolean apoli$wasModified;

	@Unique
	private FoodProperties apoli$originalProperties;

	@Shadow
	@Nullable
	public abstract Entity getEntityRepresentation();

	@Override
	public Entity apoli$getEntity() {
		return apoli$getEntity(true);
	}

	@Override
	public Entity apoli$getEntity(boolean prioritiseVanillaHolder) {
		Entity vanillaHolder = getEntityRepresentation();
		if (!prioritiseVanillaHolder || vanillaHolder == null) {
			return apoli$holdingEntity;
		}
		return vanillaHolder;
	}

	@Override
	public void apoli$setEntity(Entity entity) {
		this.apoli$holdingEntity = entity;
	}

	@WrapOperation(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;"))
	private @NotNull InteractionResultHolder<ItemStack> apoli$onItemUse(Item item, Level world, Player user, InteractionHand hand, @NotNull Operation<InteractionResultHolder<ItemStack>> original) {

		ItemStack thisAsStack = (ItemStack) (Object) this;
		//  region  Prevent item use
		if (PowerHolderComponent.hasPowerType(user, PreventItemUsePowerType.class, piup -> piup.doesPrevent(thisAsStack))) {
			return InteractionResultHolder.fail(thisAsStack);
		}
		//  endregion

		//  region  Action on item before use
		SlotAccess useStackReference = InventoryUtil.getStackReferenceFromStack(user, thisAsStack);
		ItemStack useStack = useStackReference.get();

		ActionOnItemUsePowerType.TriggerType triggerType = useStack.getUseDuration(user) == 0
			? ActionOnItemUsePowerType.TriggerType.INSTANT
			: ActionOnItemUsePowerType.TriggerType.START;
		ActionOnItemUsePowerType.executeActions(user, useStackReference, useStack, triggerType, PriorityPhase.BEFORE);
		//  endregion

		ItemStack oldUseStack = useStack.copy();

		InteractionResultHolder<ItemStack> action = original.call(useStack.getItem(), world, user, hand);

		if (!action.getResult().consumesAction()) {
			return action;
		}

		//  region  Action on item after use
		useStackReference = SlotAccess.forEquipmentSlot(user, user.getEquipmentSlotForItem(oldUseStack));
		triggerType = useStack.getUseDuration(user) == 0
			? ActionOnItemUsePowerType.TriggerType.INSTANT
			: ActionOnItemUsePowerType.TriggerType.START;

		ActionOnItemUsePowerType.executeActions(user, useStackReference, useStack, triggerType, PriorityPhase.AFTER);
		return action;
		//  endregion

	}

	@WrapOperation(method = "onUseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;onUseTick(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;I)V"))
	private void apoli$actionOnItemDuringUse(Item item, Level world, LivingEntity user, ItemStack stack, int remainingUseTicks, Operation<Void> original, @Share("usingStackReference") LocalRef<SlotAccess> sharedUsingStackReference) {

		ActionOnItemUsePowerType.TriggerType triggerType = ActionOnItemUsePowerType.TriggerType.DURING;

		SlotAccess usingStackReference = InventoryUtil.getStackReferenceFromStack(user, (ItemStack) (Object) this);
		ItemStack usingStack = usingStackReference.get();

		ActionOnItemUsePowerType.executeActions(user, usingStackReference, usingStack, triggerType, PriorityPhase.BEFORE);

		original.call(usingStack.getItem(), world, user, usingStack, remainingUseTicks);
		ActionOnItemUsePowerType.executeActions(user, usingStackReference, usingStack, triggerType, PriorityPhase.AFTER);

	}

	@WrapOperation(method = "releaseUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;releaseUsing(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;I)V"))
	private void apoli$actionOnItemStoppedUsing(Item item, ItemStack stack, Level world, LivingEntity user, int remainingUseTicks, Operation<Void> original, @Share("stoppedUsingStackReference") LocalRef<SlotAccess> sharedStoppedUsingStackReference) {

		ActionOnItemUsePowerType.TriggerType triggerType = ActionOnItemUsePowerType.TriggerType.STOP;

		SlotAccess stoppedUsingStackReference = InventoryUtil.getStackReferenceFromStack(user, (ItemStack) (Object) this);
		ItemStack stoppedUsingStack = stoppedUsingStackReference.get();

		ActionOnItemUsePowerType.executeActions(user, stoppedUsingStackReference, stoppedUsingStack, triggerType, PriorityPhase.BEFORE);

		original.call(stoppedUsingStack.getItem(), stoppedUsingStack, world, user, remainingUseTicks);
		ActionOnItemUsePowerType.executeActions(user, stoppedUsingStackReference, stoppedUsingStack, triggerType, PriorityPhase.AFTER);

	}

	@WrapOperation(method = "finishUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;finishUsingItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;"))
	private ItemStack apoli$onFinishItemUse(Item item, ItemStack stack, Level world, LivingEntity user, Operation<ItemStack> original) {

		//  region  Action on item before finish using
		SlotAccess finishUsingStackRef = InventoryUtil.getStackReferenceFromStack(user, stack);
		ItemStack finishUsingStack = finishUsingStackRef.get();

		ActionOnItemUsePowerType.executeActions(user, finishUsingStackRef, finishUsingStack, ActionOnItemUsePowerType.TriggerType.FINISH, PriorityPhase.BEFORE);
		//  endregion

		finishUsingStackRef.set(original.call(finishUsingStack.getItem(), finishUsingStack, world, user));

		ActionOnItemUsePowerType.executeActions(user, finishUsingStackRef, finishUsingStack, ActionOnItemUsePowerType.TriggerType.FINISH, PriorityPhase.AFTER);
		return finishUsingStack;
		//  endregion

	}

	@ModifyReturnValue(method = "getUseAnimation", at = @At("RETURN"))
	private UseAnim apoli$replaceUseAction(UseAnim original) {
		return EdibleItemPowerType.get((ItemStack) (Object) this)
			.map(p -> p.getConsumeAnimation().getAction())
			.orElse(original);
	}

	@ModifyReturnValue(method = "getEatingSound", at = @At("RETURN"))
	private SoundEvent apoli$replaceEatingSound(SoundEvent original) {
		return EdibleItemPowerType.get((ItemStack) (Object) this)
			.map(EdibleItemPowerType::getConsumeSoundEvent)
			.orElse(original);
	}

	@ModifyReturnValue(method = "getDrinkingSound", at = @At("RETURN"))
	private SoundEvent apoli$replaceDrinkingSound(SoundEvent original) {
		return EdibleItemPowerType.get((ItemStack) (Object) this)
			.map(EdibleItemPowerType::getConsumeSoundEvent)
			.orElse(original);
	}

	@WrapOperation(method = "overrideStackedOnOther", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;overrideStackedOnOther(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/inventory/Slot;Lnet/minecraft/world/inventory/ClickAction;Lnet/minecraft/world/entity/player/Player;)Z"))
	private boolean apoli$itemOnItem_cursorStack(Item cursorItem, ItemStack cursorStack, @NotNull Slot slot, ClickAction clickType, @NotNull Player player, Operation<Boolean> original) {

		StackClickPhase clickPhase = StackClickPhase.CURSOR;

		SlotAccess cursorStackReference = ((AbstractContainerMenuAccessor) player.containerMenu).callCreateCarriedSlotAccess();
		SlotAccess slotStackReference = SlotAccess.forContainer(slot.container, slot.getContainerSlot());

		return ItemOnItemPowerType.executeActions(player, PriorityPhase.BEFORE, clickPhase, clickType, slot, slotStackReference, cursorStackReference)
			|| original.call(cursorStackReference.get().getItem(), cursorStackReference.get(), slot, clickType, player)
			|| ItemOnItemPowerType.executeActions(player, PriorityPhase.AFTER, clickPhase, clickType, slot, slotStackReference, cursorStackReference);

	}

	@WrapOperation(method = "overrideOtherStackedOnMe", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;overrideOtherStackedOnMe(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/inventory/Slot;Lnet/minecraft/world/inventory/ClickAction;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/SlotAccess;)Z"))
	private boolean apoli$itemOnItem_slotStack(Item slotItem, ItemStack slotStack, ItemStack cursorStack, @NotNull Slot slot, ClickAction clickType, Player player, SlotAccess cursorStackReference, Operation<Boolean> original) {

		StackClickPhase clickPhase = StackClickPhase.SLOT;
		SlotAccess slotStackReference = SlotAccess.forContainer(slot.container, slot.getContainerSlot());

		return ItemOnItemPowerType.executeActions(player, PriorityPhase.BEFORE, clickPhase, clickType, slot, slotStackReference, cursorStackReference)
			|| original.call(slotStackReference.get().getItem(), slotStackReference.get(), cursorStackReference.get(), slot, clickType, player, cursorStackReference)
			|| ItemOnItemPowerType.executeActions(player, PriorityPhase.AFTER, clickPhase, clickType, slot, slotStackReference, cursorStackReference);

	}

	@ModifyReturnValue(method = "copy(Z)Lnet/minecraft/world/item/ItemStack;", at = @At("RETURN"))
	// OriginsPaper - Paper redirects this to `copy(boolean originalStack)` for optimizing hoppers, use that method.
	private ItemStack apoli$passHolderOnCopy(ItemStack original) {

		Entity holder = this.apoli$getEntity();
		if (holder != null) {
			if (original.isEmpty()) {
				original = ModifyEnchantmentLevelPowerType.getOrCreateWorkableEmptyStack(holder);
			} else {
				((EntityLinkedItemStack) original).apoli$setEntity(holder);
			}
		}

		return original;

	}

	@ModifyReturnValue(method = "getUseDuration", at = @At("RETURN"))
	private int apoli$modifyMaxUseTicks(int original) {
		return ModifyFoodPowerType
			.modifyEatTicks(this.apoli$getEntity(), (ItemStack) (Object) this)
			.orElse(original);
	}
}
