package io.github.dueris.originspaper.condition.types.bientity;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import org.jetbrains.annotations.NotNull;

public class AttackTargetCondition {

	public static boolean condition(SerializableData.Instance data, @NotNull Tuple<Entity, Entity> actorAndTarget) {

		Entity actor = actorAndTarget.getA();
		Entity target = actorAndTarget.getB();

		if (actor == null || target == null) {
			return false;
		}

		return (actor instanceof Mob mobActor && target.equals(mobActor.getTarget()))
			|| (actor instanceof NeutralMob angerableActor && target.equals(angerableActor.getTarget()));

	}

	public static @NotNull ConditionFactory<Tuple<Entity, Entity>> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("attack_target"),
			SerializableData.serializableData(),
			AttackTargetCondition::condition
		);
	}
}
