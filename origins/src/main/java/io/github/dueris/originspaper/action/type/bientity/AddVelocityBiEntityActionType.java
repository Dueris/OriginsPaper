package io.github.dueris.originspaper.action.type.bientity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.context.BiEntityActionContext;
import io.github.dueris.originspaper.action.type.BiEntityActionType;
import io.github.dueris.originspaper.action.type.BiEntityActionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.util.Space;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.function.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.function.BiFunction;

public class AddVelocityBiEntityActionType extends BiEntityActionType {

	public static final TypedDataObjectFactory<AddVelocityBiEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("x", SerializableDataTypes.FLOAT, 0F)
			.add("y", SerializableDataTypes.FLOAT, 0F)
			.add("z", SerializableDataTypes.FLOAT, 0F)
			.addFunctionedDefault("velocity", ApoliDataTypes.VECTOR_3_FLOAT, data -> new Vector3f(data.getFloat("x"), data.getFloat("y"), data.getFloat("z")))
			.add("reference", SerializableDataType.enumValue(Reference.class), Reference.POSITION)
			.add("set", SerializableDataTypes.BOOLEAN, false),
		data -> new AddVelocityBiEntityActionType(
			data.get("velocity"),
			data.get("reference"),
			data.get("set")
		),
		(actionType, serializableData) -> serializableData.instance()
			.set("velocity", actionType.velocity)
			.set("reference", actionType.reference)
			.set("set", actionType.set)
	);

	private final Vector3f velocity;
	private final Reference reference;

	private final boolean set;

	public AddVelocityBiEntityActionType(Vector3f velocity, Reference reference, boolean set) {
		this.velocity = velocity;
		this.reference = reference;
		this.set = set;
	}

	@Override
	public void accept(BiEntityActionContext context) {

		Entity actor = context.actor();
		Entity target = context.target();

		if (actor != null && target != null) {
			execute(actor, target);
		}

	}

	@Override
	protected void execute(Entity actor, Entity target) {

		Vector3f velocityCopy = new Vector3f(velocity);
		TriConsumer<Float, Float, Float> method = set
			? target::setDeltaMovement
			: target::push;

		Vec3 referenceVec = reference.apply(actor, target);
		Space.transformVectorToBase(referenceVec, velocityCopy, actor.getYRot(), true);  //  Vector normalized by method

		method.accept(velocityCopy.x(), velocityCopy.y(), velocityCopy.z());
		target.hurtMarked = true;

	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return BiEntityActionTypes.ADD_VELOCITY;
	}

	public enum Reference implements BiFunction<Entity, Entity, Vec3> {

		POSITION {
			@Override
			public Vec3 apply(Entity actor, Entity target) {
				return target.position().subtract(actor.position());
			}

		},

		ROTATION {
			@Override
			public Vec3 apply(Entity actor, Entity target) {

				float pitch = actor.getXRot();
				float yaw = actor.getYRot();

				float i = 0.017453292F;

				float j = -Mth.sin(yaw * i) * Mth.cos(pitch * i);
				float k = -Mth.sin(pitch * i);
				float l = Mth.cos(yaw * i) * Mth.cos(pitch * i);

				return new Vec3(j, k, l);

			}

		}

	}

}
