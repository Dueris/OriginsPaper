package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.EntityAction;
import io.github.dueris.originspaper.action.ItemAction;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.condition.ItemCondition;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerReference;
import io.github.dueris.originspaper.power.type.InventoryPowerType;
import io.github.dueris.originspaper.util.InventoryUtil.InventoryType;
import io.github.dueris.originspaper.util.Util;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.SlotRange;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static io.github.dueris.originspaper.util.InventoryUtil.replaceInventory;

public class ReplaceInventoryEntityActionType extends EntityActionType {

    public static final TypedDataObjectFactory<ReplaceInventoryEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("inventory_type", ApoliDataTypes.INVENTORY_TYPE, InventoryType.INVENTORY)
            .add("power", ApoliDataTypes.POWER_REFERENCE.optional(), Optional.empty())
            .add("entity_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
            .add("item_action", ItemAction.DATA_TYPE.optional(), Optional.empty())
            .add("item_condition", ItemCondition.DATA_TYPE.optional(), Optional.empty())
            .add("stack", SerializableDataTypes.ITEM_STACK)
            .add("slot", ApoliDataTypes.SLOT_RANGE, null)
            .addFunctionedDefault("slots", ApoliDataTypes.SLOT_RANGES, data -> Util.singletonListOrEmpty(data.get("slot")))
            .add("merge_nbt", SerializableDataTypes.BOOLEAN, false),
        data -> new ReplaceInventoryEntityActionType(
            data.get("inventory_type"),
            data.get("power"),
            data.get("entity_action"),
            data.get("item_action"),
            data.get("item_condition"),
            data.get("stack"),
            data.get("slots"),
            data.get("merge_nbt")
        ),
        (actionType, serializableData) -> serializableData.instance()
            .set("inventory_type", actionType.inventoryType)
            .set("power", actionType.power)
            .set("entity_action", actionType.entityAction)
            .set("item_action", actionType.itemAction)
            .set("item_condition", actionType.itemCondition)
            .set("stack", actionType.stack)
            .set("slots", actionType.slotRanges)
            .set("merge_nbt", actionType.mergeNbt)
    );

    private final InventoryType inventoryType;
    private final Optional<PowerReference> power;

    private final Optional<EntityAction> entityAction;
    private final Optional<ItemAction> itemAction;

    private final Optional<ItemCondition> itemCondition;
    private final ItemStack stack;

    private final List<SlotRange> slotRanges;
    private final Set<Integer> slots;

    private final boolean mergeNbt;

    public ReplaceInventoryEntityActionType(InventoryType inventoryType, Optional<PowerReference> power, Optional<EntityAction> entityAction, Optional<ItemAction> itemAction, Optional<ItemCondition> itemCondition, ItemStack stack, List<SlotRange> slotRanges, boolean mergeNbt) {

        this.inventoryType = inventoryType;
        this.power = power;

        this.entityAction = entityAction;
        this.itemAction = itemAction;

        this.itemCondition = itemCondition;
        this.stack = stack;

        this.slotRanges = slotRanges;
        this.slots = Util.toSlotIdSet(slotRanges);

        this.mergeNbt = mergeNbt;

    }

    @Override
    protected void execute(Entity entity) {

        Optional<InventoryPowerType> inventoryPowerType = power
            .filter(p -> inventoryType == InventoryType.POWER)
            .map(p -> p.getPowerTypeFrom(entity))
            .filter(InventoryPowerType.class::isInstance)
            .map(InventoryPowerType.class::cast);

        replaceInventory(entity, slots, inventoryPowerType, entityAction, itemAction, itemCondition, stack, mergeNbt);

    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return EntityActionTypes.REPLACE_INVENTORY;
    }

}
