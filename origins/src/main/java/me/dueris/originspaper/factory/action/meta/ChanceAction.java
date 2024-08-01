package me.dueris.originspaper.factory.action.meta;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableDataBuilder;
import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.action.ActionFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class ChanceAction {

	public static <T> @NotNull ActionFactory<T> getFactory(SerializableDataBuilder<ActionFactory<T>> dataType) {
		return new ActionFactory<T>(OriginsPaper.apoliIdentifier("chance"),
			InstanceDefiner.instanceDefiner()
				.add("action", dataType)
				.add("chance", SerializableDataTypes.FLOAT)
				.add("fail_action", dataType, null),
			(data, t) -> {
				ActionFactory<T> action = data.get("action");
				if (new Random().nextFloat() < data.getFloat("chance")) {
					action.accept(t);
				} else if (data.isPresent("fail_action")) {
					ActionFactory<T> fail = data.get("fail_action");
					fail.accept(t);
				}
			}
		);
	}
}
