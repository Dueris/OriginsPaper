package me.dueris.originspaper.factory.conditions.types.bientity;

import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.util.holder.Pair;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import me.dueris.originspaper.factory.data.ApoliDataTypes;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class InvertCondition {

	@Contract(" -> new")
	public static @NotNull ConditionFactory<Pair<Entity, Entity>> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("invert"),
			InstanceDefiner.instanceDefiner()
				.add("condition", ApoliDataTypes.BIENTITY_CONDITION),
			(data, actorAndTarget) -> {
				return data.<Predicate<Tuple<Entity, Entity>>>get("condition").test(new Tuple<>(actorAndTarget.getB(), actorAndTarget.getA()));
			}
		);
	}
}
