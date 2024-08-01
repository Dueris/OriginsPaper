package me.dueris.originspaper.factory.conditions.types.bientity;

import io.github.dueris.calio.parser.InstanceDefiner;
import net.minecraft.util.Tuple;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import me.dueris.originspaper.factory.data.ApoliDataTypes;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class UndirectedCondition {

	@Contract(" -> new")
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
