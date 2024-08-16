package io.github.dueris.originspaper.condition.types.bientity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class CanSeeCondition {

	public static boolean condition(SerializableData.Instance data, @NotNull Tuple<Entity, Entity> actorAndTarget) {

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

	public static @NotNull ConditionFactory<Tuple<Entity, Entity>> getFactory() {
		return new ConditionFactory<>(
				OriginsPaper.apoliIdentifier("can_see"),
				SerializableData.serializableData()
						.add("shape_type", SerializableDataTypes.SHAPE_TYPE, ClipContext.Block.VISUAL)
						.add("fluid_handling", SerializableDataTypes.FLUID_HANDLING, ClipContext.Fluid.NONE),
				CanSeeCondition::condition
		);
	}
}
