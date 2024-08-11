package me.dueris.originspaper.condition.types.bientity;

import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.condition.ConditionFactory;
import me.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class UndirectedCondition {

	public static @NotNull ConditionFactory<Tuple<Entity, Entity>> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("undirected"),
			InstanceDefiner.instanceDefiner()
				.add("condition", ApoliDataTypes.BIENTITY_CONDITION),
			(data, actorAndTarget) -> {
				Predicate<Tuple<Entity, Entity>> biEntityCondition = data.get("condition");
				return biEntityCondition.test(actorAndTarget)
					|| biEntityCondition.test(new Tuple<>(actorAndTarget.getB(), actorAndTarget.getA()));
			}
		);
	}
}
