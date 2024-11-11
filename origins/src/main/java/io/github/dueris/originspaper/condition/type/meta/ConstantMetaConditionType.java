package io.github.dueris.originspaper.condition.type.meta;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.AbstractCondition;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.AbstractConditionType;
import io.github.dueris.originspaper.util.context.TypeConditionContext;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;

import java.util.function.Function;

public interface ConstantMetaConditionType {

    boolean value();

    static <T extends TypeConditionContext, C extends AbstractCondition<T, CT>, CT extends AbstractConditionType<T, C>, M extends AbstractConditionType<T, C> & ConstantMetaConditionType> ConditionConfiguration<M> createConfiguration(Function<Boolean, M> constructor) {
        return ConditionConfiguration.of(
            OriginsPaper.apoliIdentifier("constant"),
            new SerializableData()
                .add("value", SerializableDataTypes.BOOLEAN),
            data -> constructor.apply(
                data.get("value")
            ),
            (m, serializableData) -> serializableData.instance()
                .set("value", m.value())
        );
    }

}
