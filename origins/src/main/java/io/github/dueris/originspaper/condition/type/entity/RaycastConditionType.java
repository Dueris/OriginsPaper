package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.util.Space;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.phys.*;
import org.joml.Vector3f;

import java.util.function.Predicate;

public class RaycastConditionType {

	public static boolean condition(SerializableData.Instance data, Entity entity) {

		Vec3 origin = new Vec3(entity.getX(), entity.getEyeY(), entity.getZ());
		Vec3 direction = entity.getViewVector(1);
		if (data.isPresent("direction")) {
			direction = data.get("direction");
			Space space = data.get("space");
			Vector3f vector3f = new Vector3f((float) direction.x(), (float) direction.y(), (float) direction.z()).normalize();
			space.toGlobal(vector3f, entity);
			direction = new Vec3(vector3f);
		}
		Vec3 target;

		HitResult hitResult = null;
		if (data.getBoolean("entity")) {
			double distance = getEntityReach(data, entity);
			target = origin.add(direction.scale(distance));
			hitResult = performEntityRaycast(entity, origin, target, data.get("match_bientity_condition"));
		}
		if (data.getBoolean("block")) {
			double distance = getBlockReach(data, entity);
			target = origin.add(direction.scale(distance));
			BlockHitResult blockHit = performBlockRaycast(entity, origin, target, data.get("shape_type"), data.get("fluid_handling"));
			if (blockHit.getType() != HitResult.Type.MISS) {
				if (hitResult == null || hitResult.getType() == HitResult.Type.MISS) {
					hitResult = blockHit;
				} else {
					if (hitResult.distanceTo(entity) > blockHit.distanceTo(entity)) {
						hitResult = blockHit;
					}
				}
			}
		}
		if (hitResult != null && hitResult.getType() != HitResult.Type.MISS) {
			if (hitResult instanceof BlockHitResult bhr && data.isPresent("block_condition")) {
				BlockInWorld cbp = new BlockInWorld(entity.level(), bhr.getBlockPos(), true);
				return data.<Predicate<BlockInWorld>>get("block_condition").test(cbp);
			}
			if (hitResult instanceof EntityHitResult ehr && data.isPresent("hit_bientity_condition")) {
				return data.<Predicate<Tuple<Entity, Entity>>>get("hit_bientity_condition")
					.test(new Tuple<>(entity, ehr.getEntity()));
			}
			return true;
		}
		return false;
	}

	private static double getEntityReach(SerializableData.Instance data, Entity entity) {

		if (!data.isPresent("entity_distance") && !data.isPresent("distance")) {
			return entity instanceof LivingEntity livingEntity && livingEntity.getAttributes().hasAttribute(Attributes.ENTITY_INTERACTION_RANGE)
				? livingEntity.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE)
				: 3;
		} else {
			return data.isPresent("entity_distance")
				? data.getDouble("entity_distance")
				: data.getDouble("distance");
		}

	}


	private static double getBlockReach(SerializableData.Instance data, Entity entity) {

		if (!data.isPresent("block_distance") && !data.isPresent("distance")) {
			return entity instanceof LivingEntity livingEntity && livingEntity.getAttributes().hasAttribute(Attributes.ENTITY_INTERACTION_RANGE)
				? livingEntity.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE)
				: 4.5;
		} else {
			return data.isPresent("block_distance")
				? data.getDouble("block_distance")
				: data.getDouble("distance");
		}

	}

	private static BlockHitResult performBlockRaycast(Entity source, Vec3 origin, Vec3 target, ClipContext.Block shapeType, ClipContext.Fluid fluidHandling) {
		ClipContext context = new ClipContext(origin, target, shapeType, fluidHandling, source);
		return source.level().clip(context);
	}

	private static EntityHitResult performEntityRaycast(Entity source, Vec3 origin, Vec3 target, ConditionTypeFactory<Tuple<Entity, Entity>>.Instance biEntityCondition) {
		Vec3 ray = target.subtract(origin);
		AABB box = source.getBoundingBox().expandTowards(ray).inflate(1.0D, 1.0D, 1.0D);
		EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(source, origin, target, box, (entityx) -> {
			return !entityx.isSpectator() && (biEntityCondition == null || biEntityCondition.test(new Tuple<>(source, entityx)));
		}, ray.lengthSqr());
		return entityHitResult;
	}

	public static ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(OriginsPaper.apoliIdentifier("raycast"),
			new SerializableData()
				.add("distance", SerializableDataTypes.DOUBLE, null)
				.add("block_distance", SerializableDataTypes.DOUBLE, null)
				.add("entity_distance", SerializableDataTypes.DOUBLE, null)
				.add("direction", SerializableDataTypes.VECTOR, null)
				.add("space", ApoliDataTypes.SPACE, Space.WORLD)
				.add("block", SerializableDataTypes.BOOLEAN, true)
				.add("entity", SerializableDataTypes.BOOLEAN, true)
				.add("shape_type", SerializableDataType.enumValue(ClipContext.Block.class), ClipContext.Block.OUTLINE)
				.add("fluid_handling", SerializableDataType.enumValue(ClipContext.Fluid.class), ClipContext.Fluid.ANY)
				.add("match_bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
				.add("hit_bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
				.add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null),
			RaycastConditionType::condition
		);
	}
}
