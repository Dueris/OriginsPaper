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
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static io.github.dueris.originspaper.util.InventoryUtil.dropInventory;

public class DropInventoryEntityActionType extends EntityActionType {

    public static final TypedDataObjectFactory<DropInventoryEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("inventory_type", ApoliDataTypes.INVENTORY_TYPE, InventoryType.INVENTORY)
            .add("power", ApoliDataTypes.POWER_REFERENCE.optional(), Optional.empty())
            .add("entity_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
            .add("item_action", ItemAction.DATA_TYPE.optional(), Optional.empty())
            .add("item_condition", ItemCondition.DATA_TYPE.optional(), Optional.empty())
            .add("slot", ApoliDataTypes.SLOT_RANGE, null)
            .addFunctionedDefault("slots", ApoliDataTypes.SLOT_RANGES, data -> Util.singletonListOrEmpty(data.get("slot")))
            .add("throw_randomly", SerializableDataTypes.BOOLEAN, false)
            .add("retain_ownership", SerializableDataTypes.BOOLEAN, false)
            .add("amount", SerializableDataTypes.POSITIVE_INT.optional(), Optional.empty()),
        data -> new DropInventoryEntityActionType(
            data.get("inventory_type"),
            data.get("power"),
            data.get("entity_action"),
            data.get("item_action"),
            data.get("item_condition"),
			data.get("slots"),
            data.get("throw_randomly"),
            data.get("retain_ownership"),
            data.get("amount")
        ),
        (actionType, serializableData) -> serializableData.instance()
            .set("inventory_type", actionType.inventoryType)
            .set("power", actionType.power)
            .set("entity_action", actionType.entityAction)
            .set("item_action", actionType.itemAction)
            .set("item_condition", actionType.itemCondition)
            .set("slots", actionType.slotRanges)
            .set("throw_randomly", actionType.throwRandomly)
            .set("retain_ownership", actionType.retainOwnership)
            .set("amount", actionType.amount)
    );

    private final InventoryType inventoryType;
    private final Optional<PowerReference> power;

    private final Optional<EntityAction> entityAction;

    private final Optional<ItemAction> itemAction;
    private final Optional<ItemCondition> itemCondition;

    private final List<SlotRange> slotRanges;
    private final Set<Integer> slots;

    private final boolean throwRandomly;
    private final boolean retainOwnership;

    private final Optional<Integer> amount;

    public DropInventoryEntityActionType(InventoryType inventoryType, Optional<PowerReference> power, Optional<EntityAction> entityAction, Optional<ItemAction> itemAction, Optional<ItemCondition> itemCondition, List<SlotRange> slotRanges, boolean throwRandomly, boolean retainOwnership, Optional<Integer> amount) {

        this.inventoryType = inventoryType;
        this.power = power;

        this.entityAction = entityAction;
        this.itemAction = itemAction;
        this.itemCondition = itemCondition;

        this.slotRanges = slotRanges;
        this.slots = Util.toSlotIdSet(slotRanges);

        this.throwRandomly = throwRandomly;
        this.retainOwnership = retainOwnership;
        this.amount = amount;

    }

    @Override
    protected void execute(Entity entity) {

        Optional<InventoryPowerType> inventoryPowerType = power
            .filter(ivp -> inventoryType == InventoryType.POWER)
            .map(p -> p.getPowerTypeFrom(entity))
            .filter(InventoryPowerType.class::isInstance)
            .map(InventoryPowerType.class::cast);

        dropInventory(entity, slots, inventoryPowerType, entityAction, itemAction, itemCondition, throwRandomly, retainOwnership, amount);

    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return EntityActionTypes.DROP_INVENTORY;
    }

}
