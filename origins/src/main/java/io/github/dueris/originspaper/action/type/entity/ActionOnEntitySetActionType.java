package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.factory.PowerReference;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Predicate;

// TODO - EntitySets - Dueris
public class ActionOnEntitySetActionType {

	public static void action(Entity entity, PowerReference power, Consumer<Tuple<Entity, Entity>> biEntityAction, Predicate<Tuple<Entity, Entity>> biEntityCondition, boolean reverse, int limit) {
	}

	public static @NotNull ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("action_on_entity_set"),
			new SerializableData()
				.add("set", ApoliDataTypes.POWER_REFERENCE)
				.add("bientity_action", ApoliDataTypes.BIENTITY_ACTION)
				.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
				.add("reverse", SerializableDataTypes.BOOLEAN, false)
				.add("limit", SerializableDataTypes.INT, 0),
			(data, entity) -> action(entity,
				data.get("set"),
				data.get("bientity_action"),
				data.getOrElse("bientity_condition", actorAndTarget -> true),
				data.get("reverse"),
				data.get("limit")
			)
		);
	}

}
