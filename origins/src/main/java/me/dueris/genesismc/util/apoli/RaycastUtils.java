package me.dueris.genesismc.util.apoli;

import me.dueris.calio.util.ClipContextUtils;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.data.types.RotationType;
import me.dueris.genesismc.factory.data.types.Space;
import me.dueris.genesismc.util.console.OriginConsoleSender;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftLocation;
import org.joml.Vector3f;
import org.json.simple.JSONObject;

import java.util.Optional;

public class RaycastUtils {

    public static void action(JSONObject data, Entity entity) {
        Vec3 origin = new Vec3(entity.getX(), entity.getEyeY(), entity.getZ());
        Vec3 direction = entity.getViewVector(1);
        if (data.containsKey("direction")) {
            direction = RotationType.parseDirection((JSONObject) data.get("direction"));
            Space space = Space.getSpace(data.get("space").toString());
            Vector3f vector3f = new Vector3f((float) direction.x, (float) direction.y, (float) direction.z).normalize();
            space.toGlobal(vector3f, entity);
            direction = new Vec3(vector3f);
        }
        Vec3 target = origin.add(direction.scale(getBlockReach(data, entity)));

        Location location = CraftLocation.toBukkit(target);
        location.setWorld(entity.getBukkitEntity().getWorld());

        Actions.executeEntity(entity.getBukkitEntity(), (JSONObject) data.getOrDefault("before_action", null));

        float step = Math.round((Double) data.getOrDefault("command_step", 1d));
        if (data.containsKey("command_along_ray")) {
            executeStepCommands(entity, origin, target, data.getOrDefault("command_along_ray", null).toString(), step);
        }
        MinecraftServer server = entity.getServer();
        if (server != null) {
            Vec3 dir = target.subtract(origin).normalize();
            double length = origin.distanceTo(target);
            for (double current = 0; current < length; current += step) {
                Location curLoc = CraftLocation.toBukkit(origin.add(dir.scale(current)));
                curLoc.setWorld(entity.getBukkitEntity().getWorld());
                boolean hit = false;
                if (!curLoc.getNearbyEntities(0.3, 0.3, 0.3).isEmpty()) { // entity hit
                    hit = true;
                    Actions.executeBiEntity(entity.getBukkitEntity(), (org.bukkit.entity.Entity) curLoc.getNearbyEntities(0.3, 0.3, 0.3).toArray()[0], (JSONObject) data.getOrDefault("bientity_action", new JSONObject()));
                }
                if (curLoc.getBlock().isCollidable()) {
                    hit = true;
                    Actions.executeBlock(curLoc, (JSONObject) data.getOrDefault("hit_action", new JSONObject()));
                }

                if (curLoc.getBlock().isCollidable()) {
                    if (hit) {
                        Actions.executeEntity(entity.getBukkitEntity(), (JSONObject) data.getOrDefault("hit_action", new JSONObject()));
                    }
                    if (data.containsKey("command_at_hit")) {
                        executeCommandAtHit(entity, CraftLocation.toVec3D(curLoc), data.getOrDefault("command_at_hit", null).toString());
                    }
                    break;
                }
            }
        }
    }

    private static long getEntityReach(JSONObject data, Entity entity) {
        if (!data.containsKey("entity_distance") && !data.containsKey("distance")) {
            long base = (entity instanceof Player player && player.getAbilities().instabuild) ? 6 : 3;
            LivingEntity living = (LivingEntity) entity;
            return base;
        }
        return data.containsKey("entity_distance") ? (long) data.get("entity_distance") : (long) data.get("distance");
    }


    private static long getBlockReach(JSONObject data, Entity entity) {
        if (!data.containsKey("block_distance") && !data.containsKey("distance")) {
            long base = (entity instanceof Player player && player.getAbilities().instabuild) ? 5 : 4;
            LivingEntity living = (LivingEntity) entity;
            return base;
        }
        return data.containsKey("block_distance") ? (long) data.get("block_distance") : (long) data.get("distance");
    }

    private static void executeStepCommands(Entity entity, Vec3 origin, Vec3 target, String command, double step) {
        if (command == null) return;
        MinecraftServer server = entity.getServer();
        if (server != null) {
            Vec3 direction = target.subtract(origin).normalize();
            double length = origin.distanceTo(target);
            for (double current = 0; current < length; current += step) {
                boolean validOutput = !(entity instanceof ServerPlayer) || ((ServerPlayer) entity).connection != null;
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

    public static void executeCommandAtHit(Entity entity, Vec3 hitPosition, String command) { // GenesisMC - private -> public
        if (command == null) return;
        MinecraftServer server = entity.getServer();
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
                entity.getServer(),
                entity);
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
        EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(source, origin, target, box, (entityx) -> {
            return !entityx.isSpectator() && (!biEntityCondition.isPresent() || (biEntityCondition.isPresent() && biEntityCondition.get()));
        }, ray.lengthSqr());
        return entityHitResult;
    }

    public static boolean condition(JSONObject data, Entity entity) {
        Vec3 origin = new Vec3(entity.getX(), entity.getEyeY(), entity.getZ());
        Vec3 direction = entity.getViewVector(1);
        if (data.containsKey("direction")) {
            direction = RotationType.parseDirection((JSONObject) data.get("direction"));
            Space space = Space.getSpace(data.get("space").toString());
            Vector3f vector3f = new Vector3f((float) direction.x, (float) direction.y, (float) direction.z).normalize();
            space.toGlobal(vector3f, entity);
            direction = new Vec3(vector3f);
        }
        Vec3 target = origin.add(direction.scale(getBlockReach(data, entity)));

        Location location = CraftLocation.toBukkit(target);
        location.setWorld(entity.getBukkitEntity().getWorld());

        HitResult hitResult = null;
        // Apoli start
        if (data.get("entity") != null && (boolean) data.get("entity")) {
            double distance = getEntityReach(data, entity);
            target = origin.add(direction.scale(distance));
            hitResult = performEntityRaycast(entity, origin, target, Optional.empty());
        }
        if (data.get("block") != null && (boolean) data.get("block")) {
            double distance = getBlockReach(data, entity);
            target = origin.add(direction.scale(distance));
            BlockHitResult blockHit = performBlockRaycast(entity, origin, target, ClipContextUtils.getShapeType(data.get("shape_type").toString()), ClipContextUtils.getFluidHandling(data.get("fluid_handling").toString()));
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
        float step = Math.round((Double) data.getOrDefault("command_step", 1d));
        MinecraftServer server = entity.getServer();
        if (server != null) {
            Vec3 dir = target.subtract(origin).normalize();
            double length = origin.distanceTo(target);
            boolean hasHit = false;
            for (double current = 0; current < length; current += step) {
                Location curLoc = CraftLocation.toBukkit(origin.add(dir.scale(current)));
                curLoc.setWorld(entity.getBukkitEntity().getWorld());
                if (curLoc.getBlock().isCollidable() || !curLoc.getNearbyEntities(0.3, 0.3, 0.3).isEmpty()) {
                    hasHit = true;
                    break;
                }
            }
            return hasHit;
        }
        return false;
    }
}
