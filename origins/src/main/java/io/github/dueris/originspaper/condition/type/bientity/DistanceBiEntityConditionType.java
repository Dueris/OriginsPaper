package io.github.dueris.originspaper.condition.type.bientity;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.BiEntityConditionType;
import io.github.dueris.originspaper.condition.type.BiEntityConditionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.util.Comparison;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class DistanceBiEntityConditionType extends BiEntityConditionType {

    public static final TypedDataObjectFactory<DistanceBiEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("comparison", ApoliDataTypes.COMPARISON)
            .add("compare_to", SerializableDataTypes.DOUBLE),
        data -> new DistanceBiEntityConditionType(
            data.get("comparison"),
            data.get("compare_to")
        ),
        (conditionType, serializableData) -> serializableData.instance()
            .set("comparison", conditionType.comparison)
            .set("compare_to", conditionType.compareTo)
    );

    private final Comparison comparison;
    private final double compareTo;

    public DistanceBiEntityConditionType(Comparison comparison, double compareTo) {
        this.comparison = comparison;
        this.compareTo = compareTo;
    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return BiEntityConditionTypes.DISTANCE;
    }

    @Override
    public boolean test(Entity actor, Entity target) {
        return actor != null
            && target != null
            && comparison.compare(actor.position().distanceToSqr(target.position()), compareTo * compareTo);
    }

}
