package io.github.dueris.originspaper.action.type.block;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.EntityAction;
import io.github.dueris.originspaper.action.type.BlockActionType;
import io.github.dueris.originspaper.action.type.BlockActionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.util.Util;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SpawnEntityBlockActionType extends BlockActionType {

    public static final TypedDataObjectFactory<SpawnEntityBlockActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("entity_type", SerializableDataTypes.ENTITY_TYPE)
            .add("entity_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
            .add("tag", SerializableDataTypes.NBT_COMPOUND, new CompoundTag()),
        data -> new SpawnEntityBlockActionType(
            data.get("entity_type"),
            data.get("entity_action"),
            data.get("tag")
        ),
        (actionType, serializableData) -> serializableData.instance()
            .set("entity_type", actionType.entityType)
            .set("entity_action", actionType.entityAction)
            .set("tag", actionType.tag)
    );

    private final EntityType<?> entityType;

    private final Optional<EntityAction> entityAction;
    private final CompoundTag tag;

    public SpawnEntityBlockActionType(EntityType<?> entityType, Optional<EntityAction> entityAction, CompoundTag tag) {
        this.entityType = entityType;
        this.entityAction = entityAction;
        this.tag = tag;
    }

    @Override
	protected void execute(Level world, BlockPos pos, Optional<Direction> direction) {
        Util.getEntityWithPassengersSafe(world, entityType, tag, pos.getCenter(), Optional.empty(), Optional.empty()).ifPresent(entity -> {

            if (world instanceof ServerLevel serverWorld) {
                serverWorld.tryAddFreshEntityWithPassengers(entity);
            }

            entityAction.ifPresent(action -> action.execute(entity));

        });
    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return BlockActionTypes.SPAWN_ENTITY;
    }

}
