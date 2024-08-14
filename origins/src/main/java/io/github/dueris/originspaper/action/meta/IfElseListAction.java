package io.github.dueris.originspaper.action.meta;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableDataBuilder;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import io.github.dueris.originspaper.condition.ConditionFactory;
import net.minecraft.util.Tuple;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

public class IfElseListAction {

	public static <T, U> void action(@NotNull DeserializedFactoryJson data, T t, @NotNull Function<T, U> actionToConditionTypeFunction) {
		List<Tuple<ConditionFactory<U>, ActionFactory<T>>> actions =
			data.get("actions");
		U u = actionToConditionTypeFunction.apply(t);
		for (Tuple<ConditionFactory<U>, ActionFactory<T>> action : actions) {
			if (action.getA().test(u)) {
				action.getB().accept(t);
				break;
			}
		}
	}

	public static <T, U> @NotNull ActionFactory<T> getFactory(
		SerializableDataBuilder<ActionFactory<T>> actionDataType,
		SerializableDataBuilder<ConditionFactory<U>> conditionDataType,
		Function<T, U> actionToConditionTypeFunction) {
		return new ActionFactory<>(OriginsPaper.apoliIdentifier("if_else_list"), InstanceDefiner.instanceDefiner()
			.add("actions", SerializableDataTypes.list(SerializableDataBuilder.of(
				(jsonElement) -> {
					if (jsonElement.isJsonObject()) {
						JsonObject jo = jsonElement.getAsJsonObject();
						return new Tuple<>(
							conditionDataType.deserialize(jo.get("condition")), actionDataType.deserialize(jo.get("action"))
						);
					}
					throw new JsonSyntaxException("Unable to parse actions for the if_else_list action!");
				}, Tuple.class
			))),
			(inst, t) -> action(inst, t, actionToConditionTypeFunction));
	}

	public static <T> @NotNull ActionFactory<T> getFactory(
		SerializableDataBuilder<ActionFactory<T>> actionDataType,
		SerializableDataBuilder<ConditionFactory<T>> conditionDataType) {
		return getFactory(actionDataType, conditionDataType, t -> t);
	}
}
