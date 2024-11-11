package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.originspaper.condition.BlockCondition;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.calio.data.SerializableData;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

public class OnBlockEntityConditionType extends EntityConditionType {

    public static final TypedDataObjectFactory<OnBlockEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("block_condition", BlockCondition.DATA_TYPE.optional(), Optional.empty()),
        data -> new OnBlockEntityConditionType(
            data.get("block_condition")
        ),
        (conditionType, serializableData) -> serializableData.instance()
            .set("block_condition", conditionType.blockCondition)
    );

    private final Optional<BlockCondition> blockCondition;

    public OnBlockEntityConditionType(Optional<BlockCondition> blockCondition) {
        this.blockCondition = blockCondition;
    }

    @Override
    public boolean test(Entity entity) {
        BlockPos pos = BlockPos.containing(entity.getX(), entity.getBoundingBox().minY - 0.5000001D, entity.getZ());
        return entity.onGround()
            && blockCondition.map(condition -> condition.test(entity.level(), pos)).orElse(true);
    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return EntityConditionTypes.ON_BLOCK;
    }

}
