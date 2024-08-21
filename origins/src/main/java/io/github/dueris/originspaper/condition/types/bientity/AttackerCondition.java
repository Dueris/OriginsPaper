package io.github.dueris.originspaper.condition.types.bientity;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class AttackerCondition {

	public static boolean condition(SerializableData.Instance data, @NotNull Tuple<Entity, Entity> actorAndTarget) {

		Entity actor = actorAndTarget.getA();
		Entity target = actorAndTarget.getB();

		if (actor == null || target == null) {
			return false;
		}

		return target instanceof LivingEntity livingTarget
			&& actor.equals(livingTarget.getLastHurtByMob());

	}

	public static @NotNull ConditionFactory<Tuple<Entity, Entity>> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("attacker"),
			SerializableData.serializableData(),
			AttackerCondition::condition
		);
	}
}
