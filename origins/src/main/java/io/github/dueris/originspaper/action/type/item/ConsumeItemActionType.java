package io.github.dueris.originspaper.action.type.item;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.ItemActionType;
import io.github.dueris.originspaper.action.type.ItemActionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ConsumeItemActionType extends ItemActionType {

    public static final TypedDataObjectFactory<ConsumeItemActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("amount", SerializableDataTypes.INT, 1),
        data -> new ConsumeItemActionType(
            data.get("amount")
        ),
        (actionType, serializableData) -> serializableData.instance()
            .set("amount", actionType.amount)
    );

    private final int amount;

    public ConsumeItemActionType(int amount) {
        this.amount = amount;
    }

    @Override
	protected void execute(Level world, SlotAccess stackReference) {
        stackReference.get().shrink(amount);
    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return ItemActionTypes.CONSUME;
    }

}
