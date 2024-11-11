package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.originspaper.condition.BlockCondition;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.calio.data.SerializableData;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class InBlockEntityConditionType extends EntityConditionType {

    public static final TypedDataObjectFactory<InBlockEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("block_condition", BlockCondition.DATA_TYPE),
        data -> new InBlockEntityConditionType(
            data.get("block_condition")
        ),
        (conditionType, serializableData) -> serializableData.instance()
            .set("block_condition", conditionType.blockCondition)
    );

    private final BlockCondition blockCondition;

    public InBlockEntityConditionType(BlockCondition blockCondition) {
        this.blockCondition = blockCondition;
    }

    @Override
    public boolean test(Entity entity) {
        return blockCondition.test(entity.level(), entity.blockPosition());
    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return EntityConditionTypes.IN_BLOCK;
    }

}
