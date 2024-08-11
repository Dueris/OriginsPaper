package me.dueris.originspaper.action.meta;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableDataBuilder;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.action.ActionFactory;
import me.dueris.originspaper.util.ApoliScheduler;
import org.jetbrains.annotations.NotNull;

public class DelayAction {
	private static final ApoliScheduler SCHEDULER = new ApoliScheduler();

	public static <T> void action(@NotNull DeserializedFactoryJson data, T t) {
		ActionFactory<T> action = data.get("action");
		SCHEDULER.queue(s -> action.accept(t), data.getInt("ticks"));
	}

	public static <T> @NotNull ActionFactory<T> getFactory(SerializableDataBuilder<ActionFactory<T>> dataType) {
		return new ActionFactory<T>(OriginsPaper.apoliIdentifier("delay"),
			InstanceDefiner.instanceDefiner()
				.add("ticks", SerializableDataTypes.INT)
				.add("action", dataType),
			DelayAction::action
		);
	}
}
