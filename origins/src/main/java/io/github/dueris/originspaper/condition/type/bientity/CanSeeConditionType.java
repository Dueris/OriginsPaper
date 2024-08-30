package io.github.dueris.originspaper.condition.type.bientity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class CanSeeConditionType {

	public static boolean condition(Entity actor, Entity target, ClipContext.Block shapeType, ClipContext.Fluid fluidHandling) {

		if ((actor == null || target == null) || actor.level() != target.level()) {
			return false;
		}

		Vec3 actorEyePos = actor.getEyePosition();
		Vec3 targetEyePos = target.getEyePosition();

		if (actorEyePos.distanceTo(targetEyePos) > 128.0d) {
			return false;
		}

		ClipContext context = new ClipContext(actorEyePos, targetEyePos, shapeType, fluidHandling, actor);
		return actor.level().clip(context).getType() == HitResult.Type.MISS;

	}

	public static @NotNull ConditionTypeFactory<Tuple<Entity, Entity>> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("can_see"),
			new SerializableData()
				.add("shape_type", SerializableDataTypes.SHAPE_TYPE, ClipContext.Block.VISUAL)
				.add("fluid_handling", SerializableDataTypes.FLUID_HANDLING, ClipContext.Fluid.NONE),
			(data, actorAndTarget) -> condition(actorAndTarget.getA(), actorAndTarget.getB(),
				data.get("shape_type"),
				data.get("fluid_handling")
			)
		);
	}

}
