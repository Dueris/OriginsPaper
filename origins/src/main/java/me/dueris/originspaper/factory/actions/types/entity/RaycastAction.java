package me.dueris.originspaper.factory.actions.types.entity;

import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.originspaper.factory.actions.Actions;
import me.dueris.originspaper.factory.conditions.ConditionExecutor;
import me.dueris.originspaper.factory.data.types.Space;
import me.dueris.originspaper.factory.data.types.VectorGetter;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.joml.Vector3f;

public class RaycastAction {
	public static void action(FactoryJsonObject data, Entity entity) {

		Vec3 origin = new Vec3(entity.getX(), entity.getEyeY(), entity.getZ());
		Vec3 direction = entity.getViewVector(1);
		if (data.isPresent("direction")) {
			direction = VectorGetter.getNMSVector(data.getJsonObject("direction"));
			Space space = data.getEnumValueOrDefault("space", Space.class, Space.WORLD);
			Vector3f vector3f = new Vector3f((float) direction.x(), (float) direction.y(), (float) direction.z()).normalize();
			space.toGlobal(vector3f, entity);
			direction = new Vec3(vector3f);
		}
		Vec3 target = origin.add(direction.scale(getBlockReach(data, entity)));

		if (data.isPresent("before_action")) {
			Actions.executeEntity(entity.getBukkitEntity(), data.getJsonObject("before_action"));
		}

		HitResult hitResult = null;
		if (data.getBoolean("entity")) {
			double distance = getEntityReach(data, entity);
			target = origin.add(direction.scale(distance));
			hitResult = performEntityRaycast(entity, origin, target, data.getJsonObject("bientity_condition"));
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
			if (data.isPresent("command_at_hit")) {
				Vec3 offsetDirection = direction;
				double offset = 0;
				Vec3 hitPos = hitResult.getLocation();
				if (data.isPresent("command_hit_offset")) {
					offset = data.getNumber("command_hit_offset").getDouble();
				} else {
					if (hitResult instanceof BlockHitResult bhr) {
						if (bhr.getDirection() == Direction.DOWN) {
							offset = entity.getBbHeight();
						} else if (bhr.getDirection() == Direction.UP) {
							offset = 0;
						} else {
							offset = entity.getBbWidth() / 2;
							offsetDirection = new Vec3(
								bhr.getDirection().getStepX(),
								bhr.getDirection().getStepY(),
								bhr.getDirection().getStepZ()
							).scale(-1);
						}
					}
					offset += 0.05;
				}
				Vec3 at = hitPos.subtract(offsetDirection.scale(offset));
				executeCommandAtHit(entity, at, data.getString("command_at_hit"));
			}
			if (data.isPresent("command_along_ray")) {
				executeStepCommands(entity, origin, hitResult.getLocation(), data.getString("command_along_ray"), data.getNumberOrDefault("command_step", 1.0D).getDouble());
			}
			if (data.isPresent("block_action") && hitResult instanceof BlockHitResult bhr) {
				FactoryJsonObject blockAction = data.getJsonObject("block_action");
				Location location = CraftLocation.toBukkit(bhr.getBlockPos());
				location.setWorld(entity.getBukkitEntity().getWorld());
				Actions.executeBlock(location, blockAction);
			}
			if (data.isPresent("bientity_action") && hitResult instanceof EntityHitResult ehr) {
				FactoryJsonObject bientityAction = data.getJsonObject("bientity_action");
				Actions.executeBiEntity(entity.getBukkitEntity(), ehr.getEntity().getBukkitEntity(), bientityAction);
			}
			if (data.isPresent("hit_action")) {
				Actions.executeEntity(entity.getBukkitEntity(), data.getJsonObject("hit_action"));
			}
		} else {
			if (data.isPresent("command_along_ray") && !data.getBooleanOrDefault("command_along_ray_only_on_hit", false)) {
				executeStepCommands(entity, origin, target, data.getString("command_along_ray"), data.getNumberOrDefault("command_step", 1.0D).getDouble());
			}
			if (data.isPresent("miss_action")) {
				Actions.executeEntity(entity.getBukkitEntity(), data.getJsonObject("miss_action"));
			}
		}
	}

	private static double getEntityReach(FactoryJsonObject data, Entity entity) {

		if (!data.isPresent("entity_distance") && !data.isPresent("distance")) {
			return entity instanceof LivingEntity livingEntity && livingEntity.getAttributes().hasAttribute(Attributes.ENTITY_INTERACTION_RANGE)
				? livingEntity.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE)
				: 3;
		} else {
			return data.isPresent("entity_distance")
				? data.getNumberOrDefault("entity_distance", null).getDouble()
				: data.getNumberOrDefault("distance", null).getDouble();
		}

	}

	private static double getBlockReach(FactoryJsonObject data, Entity entity) {

		if (!data.isPresent("block_distance") && !data.isPresent("distance")) {
			return entity instanceof LivingEntity livingEntity && livingEntity.getAttributes().hasAttribute(Attributes.BLOCK_INTERACTION_RANGE)
				? livingEntity.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE)
				: 4.5;
		} else {
			return data.isPresent("block_distance")
				? data.getNumberOrDefault("block_distance", null).getDouble()
				: data.getNumberOrDefault("distance", null).getDouble();
		}

	}

	private static void executeStepCommands(Entity entity, Vec3 origin, Vec3 target, String command, double step) {
		MinecraftServer server = entity.level().getServer();
		if (server != null) {
			Vec3 direction = target.subtract(origin).normalize();
			double length = origin.distanceTo(target);
			for (double current = 0; current < length; current += step) {
				boolean validOutput = !(entity instanceof ServerPlayer) || ((ServerPlayer) entity).connection != null;
				CommandSourceStack source = new CommandSourceStack(
					CommandSource.NULL,
					origin.add(direction.scale(current)),
					entity.getRotationVector(),
					entity.level() instanceof ServerLevel ? (ServerLevel) entity.level() : null,
					4,
					entity.getName().getString(),
					entity.getDisplayName(),
					entity.level().getServer(),
					entity);
				server.getCommands().performPrefixedCommand(source, command);
			}
		}
	}

	private static void executeCommandAtHit(Entity entity, Vec3 hitPosition, String command) {
		MinecraftServer server = entity.level().getServer();
		if (server != null) {
			boolean validOutput = !(entity instanceof ServerPlayer) || ((ServerPlayer) entity).connection != null;
			CommandSourceStack source = new CommandSourceStack(
				CommandSource.NULL,
				hitPosition,
				entity.getRotationVector(),
				entity.level() instanceof ServerLevel ? (ServerLevel) entity.level() : null,
				4,
				entity.getName().getString(),
				entity.getDisplayName(),
				entity.level().getServer(),
				entity);
			server.getCommands().performPrefixedCommand(source, command);
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
			return !entityx.isSpectator() && (biEntityCondition == null || ConditionExecutor.testBiEntity(biEntityCondition, source.getBukkitEntity(), entityx.getBukkitEntity()));
		}, ray.lengthSqr());
		return entityHitResult;
	}
}
