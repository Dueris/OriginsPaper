package io.github.dueris.originspaper.action.type.meta;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.AbstractAction;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.AbstractActionType;
import io.github.dueris.originspaper.util.context.TypeActionContext;
import net.minecraft.util.RandomSource;
import org.apache.commons.lang3.function.TriFunction;

import java.util.Optional;

public interface ChanceMetaActionType<T extends TypeActionContext<?>, A extends AbstractAction<T, ? extends AbstractActionType<T, A>>> {

    A successAction();

    Optional<A> failAction();

    float chance();

    default void executeAction(T context) {

        if (RandomSource.create().nextFloat() < chance()) {
            successAction().accept(context);
        }

        else {
            failAction().ifPresent(action -> action.accept(context));
        }

    }

    static <T extends TypeActionContext<?>, A extends AbstractAction<T, AT>, AT extends AbstractActionType<T, A>, M extends AbstractActionType<T, A> & ChanceMetaActionType<T, A>> ActionConfiguration<M> createConfiguration(SerializableDataType<A> actionDataType, TriFunction<A, Optional<A>, Float, M> constructor) {
        return ActionConfiguration.of(
            OriginsPaper.apoliIdentifier("chance"),
            new SerializableData()
                .add("success_action", actionDataType)
                .add("fail_action", actionDataType.optional(), Optional.empty())
                .add("chance", SerializableDataType.boundNumber(SerializableDataTypes.FLOAT, 0F, 1F)),
            data -> constructor.apply(
                data.get("success_action"),
                data.get("fail_action"),
                data.get("chance")
            ),
            (m, serializableData) -> serializableData.instance()
                .set("success_action", m.successAction())
                .set("fail_action", m.failAction())
                .set("chance", m.chance())
        );
    }

}
