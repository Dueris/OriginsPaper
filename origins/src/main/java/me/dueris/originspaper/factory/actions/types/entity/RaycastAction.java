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
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.HitResult.Type;
import org.bukkit.Location;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class RaycastAction {
	public static void action(@NotNull FactoryJsonObject data, @NotNull Entity entity) {
		Vec3 origin = new Vec3(entity.getX(), entity.getEyeY(), entity.getZ());
		Vec3 direction = entity.getViewVector(1.0F);
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
			BlockHitResult blockHit = performBlockRaycast(
				entity,
				origin,
				target,
				data.getEnumValueOrDefault("shape_type", Block.class, Block.OUTLINE),
				data.getEnumValueOrDefault("fluid_handling", Fluid.class, Fluid.ANY)
			);
			if (blockHit.getType() != Type.MISS) {
				if (hitResult == null || hitResult.getType() == Type.MISS) {
					hitResult = blockHit;
				} else if (hitResult.distanceTo(entity) > blockHit.distanceTo(entity)) {
					hitResult = blockHit;
				}
			}
		}

		if (hitResult != null && hitResult.getType() != Type.MISS) {
			if (data.isPresent("command_at_hit")) {
				Vec3 offsetDirection = direction;
				double offset = 0.0;
				Vec3 hitPos = hitResult.getLocation();
				if (data.isPresent("command_hit_offset")) {
					offset = data.getNumber("command_hit_offset").getDouble();
				} else {
					if (hitResult instanceof BlockHitResult bhr) {
						if (bhr.getDirection() == Direction.DOWN) {
							offset = entity.getBbHeight();
						} else if (bhr.getDirection() == Direction.UP) {
							offset = 0.0;
						} else {
							offset = entity.getBbWidth() / 2.0F;
							offsetDirection = new Vec3(
								bhr.getDirection().getStepX(), bhr.getDirection().getStepY(), bhr.getDirection().getStepZ()
							)
								.scale(-1.0);
						}
					}

					offset += 0.05;
				}

				Vec3 at = hitPos.subtract(offsetDirection.scale(offset));
				executeCommandAtHit(entity, at, data.getString("command_at_hit"));
			}

			if (data.isPresent("command_along_ray")) {
				executeStepCommands(
					entity, origin, hitResult.getLocation(), data.getString("command_along_ray"), data.getNumberOrDefault("command_step", 1.0).getDouble()
				);
			}

			if (data.isPresent("block_action") && hitResult instanceof BlockHitResult bhrx) {
				FactoryJsonObject blockAction = data.getJsonObject("block_action");
				Location location = CraftLocation.toBukkit(bhrx.getBlockPos());
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
				executeStepCommands(entity, origin, target, data.getString("command_along_ray"), data.getNumberOrDefault("command_step", 1.0).getDouble());
			}

			if (data.isPresent("miss_action")) {
				Actions.executeEntity(entity.getBukkitEntity(), data.getJsonObject("miss_action"));
			}
		}
	}

	private static double getEntityReach(@NotNull FactoryJsonObject data, Entity entity) {
		if (!data.isPresent("entity_distance") && !data.isPresent("distance")) {
			if (entity instanceof LivingEntity livingEntity && livingEntity.getAttributes().hasAttribute(Attributes.ENTITY_INTERACTION_RANGE)) {
				return livingEntity.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE);
			}

			return 3.0;
		} else {
			return data.isPresent("entity_distance")
				? data.getNumberOrDefault("entity_distance", null).getDouble()
				: data.getNumberOrDefault("distance", null).getDouble();
		}
	}

	private static double getBlockReach(@NotNull FactoryJsonObject data, Entity entity) {
		if (!data.isPresent("block_distance") && !data.isPresent("distance")) {
			if (entity instanceof LivingEntity livingEntity && livingEntity.getAttributes().hasAttribute(Attributes.BLOCK_INTERACTION_RANGE)) {
				return livingEntity.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
			}

			return 4.5;
		} else {
			return data.isPresent("block_distance")
				? data.getNumberOrDefault("block_distance", null).getDouble()
				: data.getNumberOrDefault("distance", null).getDouble();
		}
	}

	private static void executeStepCommands(@NotNull Entity entity, Vec3 origin, Vec3 target, String command, double step) {
		MinecraftServer server = entity.level().getServer();
		if (server != null) {
			Vec3 direction = target.subtract(origin).normalize();
			double length = origin.distanceTo(target);
			double current = 0.0;

			while (current < length) {
				if (entity instanceof ServerPlayer && ((ServerPlayer) entity).connection == null) {
					boolean var14 = false;
				} else {
					boolean var10000 = true;
				}

				CommandSourceStack source = new CommandSourceStack(
					CommandSource.NULL,
					origin.add(direction.scale(current)),
					entity.getRotationVector(),
					entity.level() instanceof ServerLevel ? (ServerLevel) entity.level() : null,
					4,
					entity.getName().getString(),
					entity.getDisplayName(),
					entity.level().getServer(),
					entity
				);
				server.getCommands().performPrefixedCommand(source, command);
				current += step;
			}
		}
	}

	private static void executeCommandAtHit(@NotNull Entity entity, Vec3 hitPosition, String command) {
		MinecraftServer server = entity.level().getServer();
		if (server != null) {
			if (entity instanceof ServerPlayer && ((ServerPlayer) entity).connection == null) {
				boolean var6 = false;
			} else {
				boolean var10000 = true;
			}

			CommandSourceStack source = new CommandSourceStack(
				CommandSource.NULL,
				hitPosition,
				entity.getRotationVector(),
				entity.level() instanceof ServerLevel ? (ServerLevel) entity.level() : null,
				4,
				entity.getName().getString(),
				entity.getDisplayName(),
				entity.level().getServer(),
				entity
			);
			server.getCommands().performPrefixedCommand(source, command);
		}
	}

	private static @NotNull BlockHitResult performBlockRaycast(Entity source, Vec3 origin, Vec3 target, Block shapeType, Fluid fluidHandling) {
		ClipContext context = new ClipContext(origin, target, shapeType, fluidHandling, source);
		return source.level().clip(context);
	}

	private static EntityHitResult performEntityRaycast(@NotNull Entity source, Vec3 origin, @NotNull Vec3 target, FactoryJsonObject biEntityCondition) {
		Vec3 ray = target.subtract(origin);
		AABB box = source.getBoundingBox().expandTowards(ray).inflate(1.0, 1.0, 1.0);
		return ProjectileUtil.getEntityHitResult(
			source,
			origin,
			target,
			box,
			entityx -> !entityx.isSpectator()
				&& (biEntityCondition == null || ConditionExecutor.testBiEntity(biEntityCondition, source.getBukkitEntity(), entityx.getBukkitEntity())),
			ray.lengthSqr()
		);
	}
}
