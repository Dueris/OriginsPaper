package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public class EntityTypeEntityConditionType extends EntityConditionType {

    public static final TypedDataObjectFactory<EntityTypeEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("entity_type", SerializableDataTypes.ENTITY_TYPE),
        data -> new EntityTypeEntityConditionType(
            data.get("entity_type")
        ),
        (conditionType, serializableData) -> serializableData.instance()
            .set("entity_type", conditionType.entityType)
    );

    private final EntityType<?> entityType;

    public EntityTypeEntityConditionType(EntityType<?> entityType) {
        this.entityType = entityType;
    }

    @Override
    public boolean test(Entity entity) {
        return entity.getType().equals(entityType);
    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return EntityConditionTypes.ENTITY_TYPE;
    }

}
