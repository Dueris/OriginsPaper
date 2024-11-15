package io.github.dueris.originspaper.action.type.meta;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.AbstractActionType;
import io.github.dueris.originspaper.util.context.TypeActionContext;

import java.util.function.Supplier;

public interface NothingMetaActionType {

	static <T extends TypeActionContext<?>, M extends AbstractActionType<T, ?> & NothingMetaActionType> ActionConfiguration<M> createConfiguration(Supplier<M> constructor) {
		return ActionConfiguration.simple(OriginsPaper.apoliIdentifier("nothing"), constructor);
	}

}
