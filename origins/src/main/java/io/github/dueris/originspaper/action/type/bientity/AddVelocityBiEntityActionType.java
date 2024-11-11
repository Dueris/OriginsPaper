package io.github.dueris.originspaper.action.type.bientity;

import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.BiEntityActionType;
import io.github.dueris.originspaper.action.type.BiEntityActionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
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
            .add("reference", SerializableDataType.enumValue(Reference.class), Reference.POSITION)
            .add("x", SerializableDataTypes.FLOAT, 0F)
            .add("y", SerializableDataTypes.FLOAT, 0F)
            .add("z", SerializableDataTypes.FLOAT, 0F)
            .add("set", SerializableDataTypes.BOOLEAN, false),
        data -> new AddVelocityBiEntityActionType(
            data.get("reference"),
            new Vector3f(
                data.get("x"),
                data.get("y"),
                data.get("z")
            ),
            data.get("set")
        ),
        (actionType, serializableData) -> serializableData.instance()
            .set("reference", actionType.reference)
            .set("x", actionType.velocity.x())
            .set("y", actionType.velocity.y())
            .set("z", actionType.velocity.z())
            .set("set", actionType.set)
    );

    private final Reference reference;
    private final Vector3f velocity;

    private final boolean set;

    public AddVelocityBiEntityActionType(Reference reference, Vector3f velocity, boolean set) {
        this.reference = reference;
        this.velocity = velocity;
        this.set = set;
    }

    @Override
	protected void execute(Entity actor, Entity target) {

        if (actor == null || target == null) {
            return;
        }

        TriConsumer<Float, Float, Float> method = set
            ? target::setDeltaMovement
            : target::push;

        Vec3 referenceVec = reference.apply(actor, target);
        Space.transformVectorToBase(referenceVec, velocity, actor.getYRot(), true);  //  Vector normalized by method

        method.accept(velocity.x(), velocity.y(), velocity.z());
        target.hurtMarked = true;

    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return BiEntityActionTypes.ADD_VELOCITY;
    }

    public enum Reference {

        POSITION((actor, target) -> target.position().subtract(actor.position())),
        ROTATION((actor, target) -> {

            float pitch = actor.getXRot();
            float yaw = actor.getYRot();

            float i = 0.017453292F;

            float j = -Mth.sin(yaw * i) * Mth.cos(pitch * i);
            float k = -Mth.sin(pitch * i);
            float l =  Mth.cos(yaw * i) * Mth.cos(pitch * i);

            return new Vec3(j, k, l);

        });

        final BiFunction<Entity, Entity, Vec3> refFunction;
        Reference(BiFunction<Entity, Entity, Vec3> refFunction) {
            this.refFunction = refFunction;
        }

        public Vec3 apply(Entity actor, Entity target) {
            return refFunction.apply(actor, target);
        }

    }

}
