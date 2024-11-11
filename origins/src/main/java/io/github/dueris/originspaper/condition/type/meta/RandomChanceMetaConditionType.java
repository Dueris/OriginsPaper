package io.github.dueris.originspaper.condition.type.meta;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.AbstractCondition;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.AbstractConditionType;
import io.github.dueris.originspaper.util.context.TypeConditionContext;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.util.RandomSource;

import java.util.function.Function;

public interface RandomChanceMetaConditionType {

    float chance();

    default boolean testCondition() {
        return RandomSource.create().nextFloat() < chance();
    }

    static <T extends TypeConditionContext, C extends AbstractCondition<T, CT>, CT extends AbstractConditionType<T, C>, M extends AbstractConditionType<T, C> & RandomChanceMetaConditionType> ConditionConfiguration<M> createConfiguration(Function<Float, M> constructor) {
        return ConditionConfiguration.of(
            OriginsPaper.apoliIdentifier("random_chance"),
            new SerializableData()
                .add("chance", SerializableDataType.boundNumber(SerializableDataTypes.FLOAT, 0F, 1F)),
            data -> constructor.apply(
                data.get("chance")
            ),
            (m, serializableData) -> serializableData.instance()
                .set("chance", m.chance())
        );
    }

}
