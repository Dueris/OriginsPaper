package io.github.dueris.originspaper.condition.type.bientity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.BiEntityConditionType;
import io.github.dueris.originspaper.condition.type.BiEntityConditionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class CanSeeBiEntityConditionType extends BiEntityConditionType {

	public static final TypedDataObjectFactory<CanSeeBiEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("shape_type", SerializableDataTypes.SHAPE_TYPE, ClipContext.Block.VISUAL)
			.add("fluid_handling", SerializableDataTypes.FLUID_HANDLING, ClipContext.Fluid.NONE),
		data -> new CanSeeBiEntityConditionType(
			data.get("shape_type"),
			data.get("fluid_handling")
		),
		(conditionType, serializableData) -> serializableData.instance()
			.set("shape_type", conditionType.shapeType)
			.set("fluid_handling", conditionType.fluidHandling)
	);

	private final ClipContext.Block shapeType;
	private final ClipContext.Fluid fluidHandling;

	public CanSeeBiEntityConditionType(ClipContext.Block shapeType, ClipContext.Fluid fluidHandling) {
		this.shapeType = shapeType;
		this.fluidHandling = fluidHandling;
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return BiEntityConditionTypes.CAN_SEE;
	}

	@Override
	public boolean test(Entity actor, Entity target) {

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

}
