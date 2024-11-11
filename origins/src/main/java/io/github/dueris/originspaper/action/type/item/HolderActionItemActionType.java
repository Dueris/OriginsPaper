package io.github.dueris.originspaper.action.type.item;

import io.github.dueris.originspaper.access.EntityLinkedItemStack;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.EntityAction;
import io.github.dueris.originspaper.action.type.ItemActionType;
import io.github.dueris.originspaper.action.type.ItemActionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.calio.data.SerializableData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class HolderActionItemActionType extends ItemActionType {

    public static final TypedDataObjectFactory<HolderActionItemActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("action", EntityAction.DATA_TYPE),
        data -> new HolderActionItemActionType(
            data.get("action")
        ),
        (actionType, serializableData) -> serializableData.instance()
            .set("action", actionType.entityAction)
    );

    private final EntityAction entityAction;

    public HolderActionItemActionType(EntityAction entityAction) {
        this.entityAction = entityAction;
    }

    @Override
	protected void execute(Level world, SlotAccess stackReference) {

        Entity holder = ((EntityLinkedItemStack) stackReference.get()).apoli$getEntity(true);

        if (holder != null) {
            entityAction.execute(holder);
        }

    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return ItemActionTypes.HOLDER;
    }

}
