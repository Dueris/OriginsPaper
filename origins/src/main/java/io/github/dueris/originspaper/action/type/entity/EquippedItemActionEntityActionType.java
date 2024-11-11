package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.ItemAction;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import org.jetbrains.annotations.NotNull;

public class EquippedItemActionEntityActionType extends EntityActionType {

    public static final TypedDataObjectFactory<EquippedItemActionEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("equipment_slot", SerializableDataTypes.ATTRIBUTE_MODIFIER_SLOT)
            .add("item_action", ItemAction.DATA_TYPE),
        data -> new EquippedItemActionEntityActionType(
            data.get("equipment_slot"),
            data.get("item_action")
        ),
        (actionType, serializableData) -> serializableData.instance()
            .set("equipment_slot", actionType.equipmentSlot)
            .set("item_action", actionType.itemAction)
    );

    private final EquipmentSlotGroup equipmentSlot;
    private final ItemAction itemAction;

    public EquippedItemActionEntityActionType(EquipmentSlotGroup equipmentSlot, ItemAction itemAction) {
        this.equipmentSlot = equipmentSlot;
        this.itemAction = itemAction;
    }

    @Override
    protected void execute(Entity entity) {

        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }

        for (EquipmentSlot slot : EquipmentSlot.values()) {

            if (equipmentSlot.test(slot)) {
                itemAction.execute(entity.level(), SlotAccess.forEquipmentSlot(livingEntity, slot));
            }

        }

    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return EntityActionTypes.EQUIPPED_ITEM_ACTION;
    }

}
