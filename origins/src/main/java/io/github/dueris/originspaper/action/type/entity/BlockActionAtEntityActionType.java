package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.BlockAction;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.calio.data.SerializableData;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class BlockActionAtEntityActionType extends EntityActionType {

    public static final TypedDataObjectFactory<BlockActionAtEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("block_action", BlockAction.DATA_TYPE),
        data -> new BlockActionAtEntityActionType(
            data.get("block_action")
        ),
        (actionType, serializableData) -> serializableData.instance()
            .set("block_action", actionType.blockAction)
    );

    private final BlockAction blockAction;

    public BlockActionAtEntityActionType(BlockAction blockAction) {
        this.blockAction = blockAction;
    }

    @Override
    protected void execute(Entity entity) {
        blockAction.execute(entity.level(), entity.blockPosition(), Optional.empty());
    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return EntityActionTypes.BLOCK_ACTION_AT;
    }

}
