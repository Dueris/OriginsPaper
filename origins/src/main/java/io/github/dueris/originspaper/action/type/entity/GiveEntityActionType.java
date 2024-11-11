package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.ItemAction;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.util.InventoryUtil;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class GiveEntityActionType extends EntityActionType {

    public static final TypedDataObjectFactory<GiveEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("item_action", ItemAction.DATA_TYPE.optional(), Optional.empty())
            .add("preferred_slot", SerializableDataTypes.ATTRIBUTE_MODIFIER_SLOT.optional(), Optional.empty())
            .add("stack", SerializableDataTypes.ITEM_STACK),
        data -> new GiveEntityActionType(
            data.get("item_action"),
            data.get("preferred_slot"),
            data.get("stack")
        ),
        (actionType, serializableData) -> serializableData.instance()
            .set("item_action", actionType.itemAction)
            .set("preferred_slot", actionType.preferredSlot)
            .set("stack", actionType.stack)
    );

    private final Optional<ItemAction> itemAction;

    private final Optional<EquipmentSlotGroup> preferredSlot;
    private final ItemStack stack;

    public GiveEntityActionType(Optional<ItemAction> itemAction, Optional<EquipmentSlotGroup> preferredSlot, ItemStack stack) {
        this.itemAction = itemAction;
        this.preferredSlot = preferredSlot;
        this.stack = stack;
    }

    @Override
    protected void execute(Entity entity) {

        if (entity.level().isClientSide() || stack.isEmpty()) {
            return;
        }

        SlotAccess stackReference = InventoryUtil.createStackReference(stack);
        itemAction.ifPresent(action -> action.execute(entity.level(), stackReference));

        ItemStack stackToGive = stackReference.get();

        if (preferredSlot.isPresent() && entity instanceof LivingEntity living) {

            EquipmentSlotGroup actualPreferredSlot = preferredSlot.get();
            for (EquipmentSlot slot : EquipmentSlot.values()) {

                if (!actualPreferredSlot.test(slot)) {
                    continue;
                }

                ItemStack stackInSlot = living.getItemBySlot(slot);
                if (stackInSlot.isEmpty()) {
                    living.setItemSlot(slot, stackToGive);
                    return;
                }

                else if (ItemStack.matches(stackInSlot, stackToGive) && stackInSlot.getCount() < stackInSlot.getMaxStackSize()) {

                    int itemsToGive = Math.min(stackInSlot.getMaxStackSize() - stackInSlot.getCount(), stackToGive.getCount());

                    stackInSlot.grow(itemsToGive);
                    stackToGive.shrink(itemsToGive);

                    if (stackToGive.isEmpty()) {
                        return;
                    }

                }

            }

        }

        if (entity instanceof Player player) {
            player.getInventory().placeItemBackInInventory(stackToGive);
        }

        else {
            InventoryUtil.throwItem(entity, stackToGive, false , false);
        }

    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return EntityActionTypes.GIVE;
    }

}
