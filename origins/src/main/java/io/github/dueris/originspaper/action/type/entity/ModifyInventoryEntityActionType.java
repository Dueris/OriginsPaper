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
import io.github.dueris.originspaper.util.InventoryUtil.ProcessMode;
import io.github.dueris.originspaper.util.Util;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.SlotRange;

import static io.github.dueris.originspaper.util.InventoryUtil.modifyInventory;

public class ModifyInventoryEntityActionType extends EntityActionType {

    public static final TypedDataObjectFactory<ModifyInventoryEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("inventory_type", ApoliDataTypes.INVENTORY_TYPE, InventoryType.INVENTORY)
            .add("process_mode", ApoliDataTypes.PROCESS_MODE, ProcessMode.STACKS)
            .add("entity_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
            .add("item_action", ItemAction.DATA_TYPE)
            .add("power", ApoliDataTypes.POWER_REFERENCE.optional(), Optional.empty())
            .add("item_condition", ItemCondition.DATA_TYPE.optional(), Optional.empty())
            .add("slot", ApoliDataTypes.SLOT_RANGE, null)
            .addFunctionedDefault("slots", ApoliDataTypes.SLOT_RANGES, data -> Util.singletonListOrEmpty(data.get("slot")))
            .add("limit", SerializableDataTypes.POSITIVE_INT.optional(), Optional.empty()),
        data -> new ModifyInventoryEntityActionType(
            data.get("inventory_type"),
            data.get("process_mode"),
            data.get("entity_action"),
            data.get("item_action"),
            data.get("power"),
            data.get("item_condition"),
            data.get("slots"),
            data.get("limit")
        ),
        (actionType, serializableData) -> serializableData.instance()
            .set("inventory_type", actionType.inventoryType)
            .set("process_mode", actionType.processMode)
            .set("entity_action", actionType.entityAction)
            .set("item_action", actionType.itemAction)
            .set("power", actionType.power)
            .set("item_condition", actionType.itemCondition)
            .set("slots", actionType.slotRanges)
            .set("limit", actionType.limit)
    );

    private final InventoryType inventoryType;
    private final ProcessMode processMode;

    private final Optional<EntityAction> entityAction;
    private final ItemAction itemAction;

    private final Optional<PowerReference> power;
    private final Optional<ItemCondition> itemCondition;

    private final List<SlotRange> slotRanges;
    private final Set<Integer> slots;

    private final Optional<Integer> limit;

    public ModifyInventoryEntityActionType(InventoryType inventoryType, ProcessMode processMode, Optional<EntityAction> entityAction, ItemAction itemAction, Optional<PowerReference> power, Optional<ItemCondition> itemCondition, List<SlotRange> slotRanges, Optional<Integer> limit) {

        this.inventoryType = inventoryType;
        this.processMode = processMode;

        this.entityAction = entityAction;
        this.itemAction = itemAction;

        this.power = power;
        this.itemCondition = itemCondition;

        this.slotRanges = slotRanges;
        this.slots = Util.toSlotIdSet(slotRanges);

        this.limit = limit;

    }

    @Override
    protected void execute(Entity entity) {

        Optional<InventoryPowerType> inventoryPowerType = power
            .filter(ipt -> inventoryType == InventoryType.POWER)
            .map(p -> p.getPowerTypeFrom(entity))
            .filter(InventoryPowerType.class::isInstance)
            .map(InventoryPowerType.class::cast);

        modifyInventory(entity, slots, inventoryPowerType, entityAction, itemAction, itemCondition, limit, processMode);

    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return EntityActionTypes.MODIFY_INVENTORY;
    }

}
