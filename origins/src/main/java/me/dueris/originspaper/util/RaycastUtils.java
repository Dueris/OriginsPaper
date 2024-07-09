package me.dueris.originspaper.util;

import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.util.ClipContextUtils;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.actions.Actions;
import me.dueris.originspaper.factory.conditions.ConditionExecutor;
import me.dueris.originspaper.factory.data.types.RotationType;
import me.dueris.originspaper.factory.data.types.Space;
import me.dueris.originspaper.util.console.OriginConsoleSender;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Optional;

public class RaycastUtils {

	public static void action(FactoryJsonObject data, Entity entity) {
		Vec3 origin = new Vec3(entity.getX(), entity.getEyeY(), entity.getZ());
		Vec3 direction = entity.getViewVector(1);
		if (data.isPresent("direction")) {
			direction = RotationType.parseDirection(data.getJsonObject("direction"));
			Space space = data.getEnumValue("shape", Space.class);
			Vector3f vector3f = new Vector3f((float) direction.x, (float) direction.y, (float) direction.z).normalize();
			space.toGlobal(vector3f, entity);
			direction = new Vec3(vector3f);
		}
		Vec3 target = origin.add(direction.scale(getBlockReach(data, entity)));

		Location location = CraftLocation.toBukkit(target);
		location.setWorld(entity.getBukkitEntity().getWorld());

		Actions.executeEntity(entity.getBukkitEntity(), data.getJsonObject("before_action"));

		float step = Math.round(data.getNumberOrDefault("command_step", 1d).getDouble());
		MinecraftServer server = entity.getServer();
		if (server != null) {
			Vec3 dir = target.subtract(origin).normalize();
			double length = origin.distanceTo(target);
			boolean hit = false;
			Vec3 vecLoc = null;
			for (double current = 0; current < length; current += step) {
				Location curLoc = CraftLocation.toBukkit(origin.add(dir.scale(current)));
				vecLoc = CraftLocation.toVec3D(curLoc);
				curLoc.setWorld(entity.getBukkitEntity().getWorld());
				if (!curLoc.getNearbyEntities(0.4, 0.4, 0.4)
					.stream().filter(e -> e != entity.getBukkitEntity()).toList().isEmpty() && data.getBooleanOrDefault("entity", true) &&
					ConditionExecutor.testBiEntity(data.getJsonObject("bientity_condition"), entity.getBukkitEntity(), curLoc.getNearbyEntities(0.4, 0.4, 0.4)
						.stream().filter(entity1 -> !entity1.equals(entity.getBukkitEntity())).findFirst().get())) { // entity hit
					hit = true;
					curLoc.getNearbyEntities(0.4, 0.4, 0.4)
						.stream().filter(e -> e != entity.getBukkitEntity()).toList()
						.forEach(entity1 -> Actions.executeBiEntity(entity.getBukkitEntity(), entity1, data.getJsonObject("bientity_action")));
				}
				if (curLoc.getBlock().isCollidable() && data.getBooleanOrDefault("block", true)) {
					hit = true;
					Actions.executeBlock(curLoc, data.getJsonObject("block_action"));
				}

				if (data.isPresent("command_along_ray")) {
					executeNMSCommand(entity, vecLoc, data.getString("command_along_ray"));
				}

				if (hit) break;
			}

			if (!hit) {
				Actions.executeEntity(entity.getBukkitEntity(), data.getJsonObject("miss_action"));
			} else {
				Actions.executeEntity(entity.getBukkitEntity(), data.getJsonObject("hit_action"));
				if (data.isPresent("command_at_hit")) {
					executeNMSCommand(entity, vecLoc, data.getString("command_at_hit"));
				}
			}
		}
	}

	private static long getEntityReach(FactoryJsonObject data, Entity entity) {
		if (!data.isPresent("entity_distance") && !data.isPresent("distance")) {
			return (entity instanceof Player player && player.getAbilities().instabuild) ? 6 : 3;
		}
		return data.isPresent("entity_distance") ? data.getNumber("entity_distance").getLong() : data.getNumber("distance").getLong();
	}


	private static long getBlockReach(FactoryJsonObject data, Entity entity) {
		if (!data.isPresent("block_distance") && !data.isPresent("distance")) {
			return (entity instanceof Player player && player.getAbilities().instabuild) ? 5 : 4;
		}
		return data.isPresent("block_distance") ? data.getNumber("block_distance").getLong() : data.getNumber("distance").getLong();
	}

	private static void executeStepCommands(Entity entity, Vec3 origin, Vec3 target, String command, double step) {
		if (command == null) return;
		MinecraftServer server = OriginsPaper.server;
		if (server != null) {
			Vec3 direction = target.subtract(origin).normalize();
			double length = origin.distanceTo(target);
			for (double current = 0; current < length; current += step) {
				CommandSourceStack source = new CommandSourceStack(
					CommandSource.NULL,
					origin.add(direction.scale(current)),
					entity.getRotationVector(),
					(ServerLevel) entity.level(),
					4,
					entity.getName().getString(),
					entity.getDisplayName(),
					entity.getServer(),
					entity);
				server.getCommands().performPrefixedCommand(source, command);
			}
		}
	}

	public static void executeNMSCommand(@Nullable Entity entity, Vec3 hitPosition, String command) { // OriginsPaper - private -> public
		if (command == null) return;
		MinecraftServer server = entity.getServer();
		if (server != null) {
			CommandSourceStack source = new CommandSourceStack(
				entity == null ? CommandSource.NULL : entity,
				hitPosition,
				entity.getRotationVector(),
				entity.level() instanceof ServerLevel ? (ServerLevel) entity.level() : null,
				4,
				entity.getName().getString(),
				entity.getName(),
				entity.getServer(),
				entity).withSuppressedOutput();
			try {
				server.getCommands().performPrefixedCommand(source, command);
			} catch (Exception e) {
				try {
					OriginConsoleSender serverCommandSender = new OriginConsoleSender();
					Bukkit.dispatchCommand(serverCommandSender, command);
				} catch (Exception ee) {
					ee.printStackTrace();
				}
			}
		}
	}

	private static BlockHitResult performBlockRaycast(Entity source, Vec3 origin, Vec3 target, ClipContext.Block shapeType, ClipContext.Fluid fluidHandling) {
		ClipContext context = new ClipContext(origin, target, shapeType, fluidHandling, source);
		return source.level().clip(context);
	}

	private static EntityHitResult performEntityRaycast(Entity source, Vec3 origin, Vec3 target, Optional<Boolean> biEntityCondition) {
		Vec3 ray = target.subtract(origin);
		AABB box = source.getBoundingBox().expandTowards(ray).inflate(1.0D, 1.0D, 1.0D);
		EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(source, origin, target, box, (entityx) -> !entityx.isSpectator() && (!biEntityCondition.isPresent() || (biEntityCondition.isPresent() && biEntityCondition.get())), ray.lengthSqr());
		return entityHitResult;
	}

	public static boolean condition(FactoryJsonObject data, Entity entity) {
		Vec3 origin = new Vec3(entity.getX(), entity.getEyeY(), entity.getZ());
		Vec3 direction = entity.getViewVector(1);
		if (data.isPresent("direction")) {
			direction = RotationType.parseDirection(data.getJsonObject("direction"));
			Space space = data.getEnumValue("space", Space.class);
			Vector3f vector3f = new Vector3f((float) direction.x, (float) direction.y, (float) direction.z).normalize();
			space.toGlobal(vector3f, entity);
			direction = new Vec3(vector3f);
		}
		Vec3 target = origin.add(direction.scale(getBlockReach(data, entity)));

		Location location = CraftLocation.toBukkit(target);
		location.setWorld(entity.getBukkitEntity().getWorld());

		HitResult hitResult = null;
		// Apoli start
		if (data.isPresent("entity") && data.getBoolean("entity")) {
			double distance = getEntityReach(data, entity);
			target = origin.add(direction.scale(distance));
			hitResult = performEntityRaycast(entity, origin, target, Optional.empty());
		}
		if (data.isPresent("block") && data.getBoolean("block")) {
			double distance = getBlockReach(data, entity);
			target = origin.add(direction.scale(distance));
			BlockHitResult blockHit = performBlockRaycast(entity, origin, target, ClipContextUtils.getShapeType(data.getString("shape_type")), ClipContextUtils.getFluidHandling(data.getString("fluid_handling")));
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
		// Apoli end
		float step = Math.round(data.getNumberOrDefault("command_step", 1d).getDouble());
		MinecraftServer server = entity.getServer();
		if (server != null) {
			Vec3 dir = target.subtract(origin).normalize();
			double length = origin.distanceTo(target);
			boolean hasHit = false;
			for (double current = 0; current < length; current += step) {
				Location curLoc = CraftLocation.toBukkit(origin.add(dir.scale(current)));
				curLoc.setWorld(entity.getBukkitEntity().getWorld());
				if (curLoc.getBlock().isCollidable()) {
					hasHit = true;
				}
				if (!curLoc.getNearbyEntities(0.3, 0.3, 0.3).stream().filter(entity1 -> !entity1.equals(entity.getBukkitEntity())).toList().isEmpty()) {
					if (ConditionExecutor.testBiEntity(data.getJsonObject("bientity_condition"), entity.getBukkitEntity(), curLoc.getNearbyEntities(0.3, 0.3, 0.3).stream().filter(entity1 -> !entity1.equals(entity.getBukkitEntity())).findFirst().get()) && ConditionExecutor.testBiEntity(data.getJsonObject("match_bientity_condition"), entity.getBukkitEntity(), curLoc.getNearbyEntities(0.3, 0.3, 0.3).stream().filter(entity1 -> !entity1.equals(entity.getBukkitEntity())).findFirst().orElseThrow())) {
						hasHit = true;
						if (ConditionExecutor.testBiEntity(data.getJsonObject("hit_bientity_condition"), entity.getBukkitEntity(), curLoc.getNearbyEntities(0.3, 0.3, 0.3).stream().filter(entity1 -> !entity1.equals(entity.getBukkitEntity())).findFirst().orElseThrow())) {
							break;
						}
					}
				}
			}
			return hasHit;
		}
		return false;
	}
}