package me.dueris.originspaper.factory.conditions.types.bientity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import net.minecraft.util.Tuple;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class CanSeeCondition {

	public static boolean condition(DeserializedFactoryJson data, Tuple<Entity, Entity> actorAndTarget) {

		Entity actor = actorAndTarget.getA();
		Entity target = actorAndTarget.getB();

		if ((actor == null || target == null) || actor.level() != target.level()) {
			return false;
		}

		ClipContext.Block shapeType = data.get("shape_type");
		ClipContext.Fluid fluidHandling = data.get("fluid_handling");

		Vec3 actorEyePos = actor.getEyePosition();
		Vec3 targetEyePos = target.getEyePosition();

		if (actorEyePos.distanceTo(targetEyePos) > 128.0d) {
			return false;
		}

		ClipContext context = new ClipContext(actorEyePos, targetEyePos, shapeType, fluidHandling, actor);
		return actor.level().clip(context).getType() == HitResult.Type.MISS;

	}

	public static ConditionFactory<Tuple<Entity, Entity>> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("can_see"),
			InstanceDefiner.instanceDefiner()
				.add("shape_type", SerializableDataTypes.SHAPE_TYPE, ClipContext.Block.VISUAL)
				.add("fluid_handling", SerializableDataTypes.FLUID_HANDLING, ClipContext.Fluid.NONE),
			CanSeeCondition::condition
		);
	}
}
