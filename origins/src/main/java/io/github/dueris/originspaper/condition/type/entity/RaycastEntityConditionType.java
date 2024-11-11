package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.originspaper.condition.BiEntityCondition;
import io.github.dueris.originspaper.condition.BlockCondition;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.util.Space;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.function.Predicate;

public class RaycastEntityConditionType extends EntityConditionType {

    public static final TypedDataObjectFactory<RaycastEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("match_bientity_condition", BiEntityCondition.DATA_TYPE.optional(), Optional.empty())
            .add("hit_bientity_condition", BiEntityCondition.DATA_TYPE.optional(), Optional.empty())
            .add("block_condition", BlockCondition.DATA_TYPE.optional(), Optional.empty())
            .add("shape_type", SerializableDataTypes.SHAPE_TYPE, ClipContext.Block.OUTLINE)
            .add("fluid_handling", SerializableDataTypes.FLUID_HANDLING, ClipContext.Fluid.ANY)
            .add("direction", SerializableDataTypes.VECTOR.optional(), Optional.empty())
            .add("space", ApoliDataTypes.SPACE, Space.WORLD)
            .add("entity_distance", SerializableDataTypes.POSITIVE_DOUBLE.optional(), Optional.empty())
            .add("block_distance", SerializableDataTypes.POSITIVE_DOUBLE.optional(), Optional.empty())
            .add("distance", SerializableDataTypes.POSITIVE_DOUBLE.optional(), Optional.empty())
            .add("entity", SerializableDataTypes.BOOLEAN, true)
            .add("block", SerializableDataTypes.BOOLEAN, true),
        data -> new RaycastEntityConditionType(
            data.get("match_bientity_condition"),
            data.get("hit_bientity_condition"),
            data.get("block_condition"),
            data.get("shape_type"),
            data.get("fluid_handling"),
            data.get("direction"),
            data.get("space"),
            data.get("entity_distance"),
            data.get("block_distance"),
            data.get("distance"),
            data.get("entity"),
            data.get("block")
        ),
        (conditionType, serializableData) -> serializableData.instance()
            .set("match_bientity_condition", conditionType.matchBiEntityCondition)
            .set("hit_bientity_condition", conditionType.hitBiEntityCondition)
            .set("block_condition", conditionType.blockCondition)
            .set("shape_type", conditionType.shapeType)
            .set("fluid_handling", conditionType.fluidHandling)
            .set("direction", conditionType.direction)
            .set("space", conditionType.space)
            .set("entity_distance", conditionType.entityDistance)
            .set("block_distance", conditionType.blockDistance)
            .set("distance", conditionType.distance)
            .set("entity", conditionType.entity)
            .set("block", conditionType.block)
    );

    private final Optional<BiEntityCondition> matchBiEntityCondition;
    private final Optional<BiEntityCondition> hitBiEntityCondition;

    private final Optional<BlockCondition> blockCondition;

    private final ClipContext.Block shapeType;
    private final ClipContext.Fluid fluidHandling;

    private final Optional<Vec3> direction;
    private final Space space;

    private final Optional<Double> entityDistance;
    private final Optional<Double> blockDistance;
    private final Optional<Double> distance;

    private final boolean entity;
    private final boolean block;

    public RaycastEntityConditionType(Optional<BiEntityCondition> matchBiEntityCondition, Optional<BiEntityCondition> hitBiEntityCondition, Optional<BlockCondition> blockCondition, ClipContext.Block shapeType, ClipContext.Fluid fluidHandling, Optional<Vec3> direction, Space space, Optional<Double> entityDistance, Optional<Double> blockDistance, Optional<Double> distance, boolean entity, boolean block) {
        this.matchBiEntityCondition = matchBiEntityCondition;
        this.hitBiEntityCondition = hitBiEntityCondition;
        this.blockCondition = blockCondition;
        this.shapeType = shapeType;
        this.fluidHandling = fluidHandling;
        this.direction = direction;
        this.space = space;
        this.entityDistance = entityDistance;
        this.blockDistance = blockDistance;
        this.distance = distance;
        this.entity = entity;
        this.block = block;
    }

    @Override
    public boolean test(Entity entity) {

        Vec3 origin = entity.getEyePosition();
        Vec3 direction = this.direction
            .map(dir -> transformDirection(entity, dir))
            .orElseGet(() -> entity.getViewVector(1.0F));

        Vec3 destination;
        HitResult hitResult = null;

        if (this.entity) {

            double distance = getEntityReach(entity);
            destination = origin.add(direction.scale(distance));

            hitResult = entityRaycast(entity, origin, destination);

        }

        if (this.block) {

            double distance = getBlockReach(entity);
            destination = origin.add(direction.scale(distance));

            BlockHitResult blockResult = blockRaycast(entity, origin, destination);
            if (blockResult.getType() != HitResult.Type.MISS && overrideHitResult(entity, hitResult, blockResult)) {
                hitResult = blockResult;
            }

        }

        return switch (hitResult) {
            case BlockHitResult blockResult when blockCondition.isPresent() ->
                blockResult.getType() != HitResult.Type.MISS
                    && blockCondition.get().test(entity.level(), blockResult.getBlockPos());
            case EntityHitResult entityResult when hitBiEntityCondition.isPresent() ->
                entityResult.getType() != HitResult.Type.MISS
                    && hitBiEntityCondition.get().test(entity, entityResult.getEntity());
            case null, default ->
                hitResult != null
                    && hitResult.getType() != HitResult.Type.MISS;
        };

    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return EntityConditionTypes.RAYCAST;
    }

    private EntityHitResult entityRaycast(Entity caster, Vec3 origin, Vec3 destination) {

        Vec3 ray = destination.subtract(origin);
        AABB box = caster.getBoundingBox().expandTowards(ray).inflate(1.0D);

        Predicate<Entity> intersectPredicate = EntitySelector.NO_SPECTATORS
            .and(intersected -> matchBiEntityCondition
                .map(condition -> condition.test(caster, intersected))
                .orElse(true));

        return ProjectileUtil.getEntityHitResult(
            caster,
            origin,
            destination,
            box,
            intersectPredicate,
            ray.lengthSqr()
        );

    }

    private BlockHitResult blockRaycast(Entity caster, Vec3 origin, Vec3 destination) {
        ClipContext context = new ClipContext(origin, destination, shapeType, fluidHandling, caster);
        return caster.level().clip(context);
    }

    private Vec3 transformDirection(Entity entity, Vec3 direction) {

        Vector3f normalizedDirection = new Vector3f((float) direction.x(), (float) direction.y(), (float) direction.z()).normalize();
        space.toGlobal(normalizedDirection, entity);

        return new Vec3(normalizedDirection);

    }

    private static boolean overrideHitResult(Entity caster, @Nullable HitResult prev, HitResult next) {
        return prev == null
            || prev.getType() == HitResult.Type.MISS
            || prev.distanceTo(caster) > next.distanceTo(caster);
    }

    private double getEntityReach(Entity entity) {
        return entityDistance
            .or(() -> distance)
            .orElseGet(() -> entity instanceof LivingEntity livingEntity
                ? livingEntity.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE)
                : 0.0);
    }

    private double getBlockReach(Entity entity) {
        return blockDistance
            .or(() -> distance)
            .orElseGet(() -> entity instanceof LivingEntity livingEntity
                ? livingEntity.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE)
                : 0.0);
    }

}
