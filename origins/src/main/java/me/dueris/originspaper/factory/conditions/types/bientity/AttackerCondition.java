package me.dueris.originspaper.factory.conditions.types.bientity;

import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import net.minecraft.util.Tuple;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class AttackerCondition {

	public static boolean condition(DeserializedFactoryJson data, Tuple<Entity, Entity> actorAndTarget) {

		Entity actor = actorAndTarget.getA();
		Entity target = actorAndTarget.getB();

		if (actor == null || target == null) {
			return false;
		}

		return target instanceof LivingEntity livingTarget
			&& actor.equals(livingTarget.getLastHurtByMob());

	}

	public static ConditionFactory<Tuple<Entity, Entity>> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("attacker"),
			InstanceDefiner.instanceDefiner(),
			AttackerCondition::condition
		);
	}
}
