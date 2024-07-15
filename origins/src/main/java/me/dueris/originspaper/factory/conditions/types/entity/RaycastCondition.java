package me.dueris.originspaper.factory.conditions.types.entity;

import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.originspaper.factory.conditions.ConditionExecutor;
import me.dueris.originspaper.factory.data.types.Space;
import me.dueris.originspaper.factory.data.types.VectorGetter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.phys.*;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.joml.Vector3f;

public class RaycastCondition {

	public static boolean condition(FactoryJsonObject data, Entity entity) {

		Vec3 origin = new Vec3(entity.getX(), entity.getEyeY(), entity.getZ());
		Vec3 direction = entity.getViewVector(1);
		if (data.isPresent("direction")) {
			direction = VectorGetter.getNMSVector(data.getJsonObject("direction"));
			Space space = data.getEnumValueOrDefault("space", Space.class, Space.WORLD);
			Vector3f vector3f = new Vector3f((float) direction.x(), (float) direction.y(), (float) direction.z()).normalize();
			space.toGlobal(vector3f, entity);
			direction = new Vec3(vector3f);
		}
		Vec3 target;

		HitResult hitResult = null;
		if (data.getBoolean("entity")) {
			double distance = getEntityReach(data, entity);
			target = origin.add(direction.scale(distance));
			hitResult = performEntityRaycast(entity, origin, target, data.getJsonObject("match_bientity_condition"));
		}
		if (data.getBoolean("block")) {
			double distance = getBlockReach(data, entity);
			target = origin.add(direction.scale(distance));
			BlockHitResult blockHit = performBlockRaycast(entity, origin, target, data.getEnumValueOrDefault("shape_type", ClipContext.Block.class, ClipContext.Block.OUTLINE), data.getEnumValueOrDefault("fluid_handling", ClipContext.Fluid.class, ClipContext.Fluid.ANY));
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
				return ConditionExecutor.testBlock(data.getJsonObject("block_condition"), entity.level().getWorld().getBlockAt(CraftLocation.toBukkit(cbp.getPos())));
			}
			if (hitResult instanceof EntityHitResult ehr && data.isPresent("hit_bientity_condition")) {
				return ConditionExecutor.testBiEntity(data.getJsonObject("hit_bientity_condition"), entity.getBukkitEntity(), ehr.getEntity().getBukkitEntity());
			}
			return true;
		}
		return false;
	}

	private static double getEntityReach(FactoryJsonObject data, Entity entity) {

		if (!data.isPresent("entity_distance") && !data.isPresent("distance")) {
			return entity instanceof LivingEntity livingEntity && livingEntity.getAttributes().hasAttribute(Attributes.ENTITY_INTERACTION_RANGE)
				? livingEntity.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE)
				: 3;
		} else {
			return data.isPresent("entity_distance")
				? data.getNumber("entity_distance").getDouble()
				: data.getNumber("distance").getDouble();
		}

	}


	private static double getBlockReach(FactoryJsonObject data, Entity entity) {

		if (!data.isPresent("block_distance") && !data.isPresent("distance")) {
			return entity instanceof LivingEntity livingEntity && livingEntity.getAttributes().hasAttribute(Attributes.ENTITY_INTERACTION_RANGE)
				? livingEntity.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE)
				: 4.5;
		} else {
			return data.isPresent("block_distance")
				? data.getNumber("block_distance").getDouble()
				: data.getNumber("distance").getDouble();
		}

	}

	private static BlockHitResult performBlockRaycast(Entity source, Vec3 origin, Vec3 target, ClipContext.Block shapeType, ClipContext.Fluid fluidHandling) {
		ClipContext context = new ClipContext(origin, target, shapeType, fluidHandling, source);
		return source.level().clip(context);
	}

	private static EntityHitResult performEntityRaycast(Entity source, Vec3 origin, Vec3 target, FactoryJsonObject biEntityCondition) {
		Vec3 ray = target.subtract(origin);
		AABB box = source.getBoundingBox().expandTowards(ray).inflate(1.0D, 1.0D, 1.0D);
		EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(source, origin, target, box, (entityx) -> {
			return !entityx.isSpectator() && (biEntityCondition == null || biEntityCondition.isEmpty() || ConditionExecutor.testBiEntity(biEntityCondition, source.getBukkitEntity(), entityx.getBukkitEntity()));
		}, ray.lengthSqr());
		return entityHitResult;
	}

}
