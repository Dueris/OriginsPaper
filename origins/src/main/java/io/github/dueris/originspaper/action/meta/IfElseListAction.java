package io.github.dueris.originspaper.action.meta;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableDataBuilder;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionTypeFactory;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import net.minecraft.util.Tuple;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

public class IfElseListAction {

	public static <T, U> void action(@NotNull SerializableData.Instance data, T t, @NotNull Function<T, U> actionToConditionTypeFunction) {
		List<Tuple<ConditionTypeFactory<U>, ActionTypeFactory<T>>> actions =
			data.get("actions");
		U u = actionToConditionTypeFunction.apply(t);
		for (Tuple<ConditionTypeFactory<U>, ActionTypeFactory<T>> action : actions) {
			if (action.getA().test(u)) {
				action.getB().accept(t);
				break;
			}
		}
	}

	public static <T, U> @NotNull ActionTypeFactory<T> getFactory(
		SerializableDataBuilder<ActionTypeFactory<T>> actionDataType,
		SerializableDataBuilder<ConditionTypeFactory<U>> conditionDataType,
		Function<T, U> actionToConditionTypeFunction) {
		return new ActionTypeFactory<>(OriginsPaper.apoliIdentifier("if_else_list"), SerializableData.serializableData()
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

	public static <T> @NotNull ActionTypeFactory<T> getFactory(
		SerializableDataBuilder<ActionTypeFactory<T>> actionDataType,
		SerializableDataBuilder<ConditionTypeFactory<T>> conditionDataType) {
		return getFactory(actionDataType, conditionDataType, t -> t);
	}
}
