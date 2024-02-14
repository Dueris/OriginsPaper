package me.dueris.genesismc.util.apoli;

import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.util.Utils;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.*;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftLocation;
import org.joml.Vector3f;
import org.json.simple.JSONObject;

import java.util.Optional;

public class RaycastApoli {

    public static void action(JSONObject data, Entity entity) {

        Vec3 origin = new Vec3(entity.getX(), entity.getEyeY(), entity.getZ());
        Vec3 direction = entity.getViewVector(1);
        if (data.containsKey("direction")) {
            direction = Utils.createDirection((JSONObject) data.get("direction"));
            Space space = Utils.getSpaceFromString(data.get("space").toString());
            Vector3f vector3f = new Vector3f((float)direction.x, (float) direction.y, (float) direction.z).normalize();
            space.toGlobal(vector3f, entity);
            direction = new Vec3(vector3f);
        }
        Vec3 target = origin.add(direction.scale(getBlockReach(data, entity)));

        Actions.EntityActionType(entity.getBukkitEntity(), (JSONObject) data.getOrDefault("before_action", null));

        HitResult hitResult = null;
        if((boolean)data.get("entity")) {
            double distance = getEntityReach(data, entity);
            target = origin.add(direction.scale(distance));
            hitResult = performEntityRaycast(entity, origin, target, Optional.empty());
        }
        if((boolean)data.get("block")) {
            double distance = getBlockReach(data, entity);
            target = origin.add(direction.scale(distance));
            BlockHitResult blockHit = performBlockRaycast(entity, origin, target, Utils.getShapeType(data.get("shape_type").toString()), Utils.getFluidHandling(data.get("fluid_handling").toString()));
            if(blockHit.getType() != HitResult.Type.MISS) {
                if(hitResult == null || hitResult.getType() == HitResult.Type.MISS) {
                    hitResult = blockHit;
                } else {
                    if(hitResult.distanceTo(entity) > blockHit.distanceTo(entity)) {
                        hitResult = blockHit;
                    }
                }
            }
        }
        if(hitResult != null && hitResult.getType() != HitResult.Type.MISS) {
            if(data.containsKey("command_at_hit")) {
                Vec3 offsetDirection = direction;
                double offset = 0;
                Vec3 hitPos = hitResult.getLocation();
                if(data.containsKey("command_hit_offset")) {
                    offset = (double)data.get("command_hit_offset");
                } else {
                    if(hitResult instanceof BlockHitResult bhr) {
                        if(bhr.getDirection() == Direction.DOWN) {
                            offset = entity.getBbHeight();
                        } else if(bhr.getDirection() == Direction.UP) {
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
                executeCommandAtHit(entity, at, data.get("command_at_hit").toString());
            }
            if(data.containsKey("command_along_ray")) {
                executeStepCommands(entity, origin, hitResult.getLocation(), data.get("command_along_ray").toString(), (double)data.get("command_step"));
            }
            if(data.containsKey("block_action") && hitResult instanceof BlockHitResult bhr) {
                Actions.BlockActionType(CraftLocation.toBukkit(bhr.getLocation()), (JSONObject) data.get("block_action"));
            }
            if(data.containsKey("bientity_action") && hitResult instanceof EntityHitResult ehr) {
                Actions.BiEntityActionType(entity.getBukkitEntity(), ehr.getEntity().getBukkitEntity(), (JSONObject) data.get("bientity_action"));
            }
            if(data.containsKey("hit_action")){
                Actions.EntityActionType(entity.getBukkitEntity(), (JSONObject) data.get("hit_action"));
            }
        } else {
            if(data.containsKey("command_along_ray") && !(boolean)data.get("command_along_ray_only_on_hit")) {
                executeStepCommands(entity, origin, target, data.get("command_along_ray").toString(), (double)data.get("command_step"));
            }
            Actions.EntityActionType(entity.getBukkitEntity(), (JSONObject) data.get("miss_action"));
        }
    }

    private static long getEntityReach(JSONObject data, Entity entity) {
        if (!data.containsKey("entity_distance") && !data.containsKey("distance")) {
            long base = (entity instanceof Player player && player.getAbilities().instabuild) ? 6 : 3;
            return (entity instanceof LivingEntity living && false) ?
                    null : base;
        }
        return data.containsKey("entity_distance") ? (long)data.get("entity_distance") : (long)data.get("distance");
    }


    private static long getBlockReach(JSONObject data, Entity entity) {
        if (!data.containsKey("block_distance") && !data.containsKey("distance")) {
            long base = (entity instanceof Player player && player.getAbilities().instabuild) ? 5 : 4;
            return (entity instanceof LivingEntity living && false) ?
                    null : base;
        }
        return data.containsKey("block_distance") ? (long)data.get("block_distance") : (long)data.get("distance");
    }

    private static void executeStepCommands(Entity entity, Vec3 origin, Vec3 target, String command, double step) {
        MinecraftServer server = entity.getServer();
        if(server != null) {
            Vec3 direction = target.subtract(origin).normalize();
            double length = origin.distanceTo(target);
            for(double current = 0; current < length; current += step) {
                boolean validOutput = !(entity instanceof ServerPlayer) || ((ServerPlayer)entity).connection != null;
                CommandSourceStack source = new CommandSourceStack(
                        false && validOutput ? entity : CommandSource.NULL,
                        origin.add(direction.scale(current)),
                        entity.getRotationVector(),
                        true ? (ServerLevel) entity.level() : null,
                        4,
                        entity.getName().getString(),
                        entity.getDisplayName(),
                        entity.getServer(),
                        entity);
                server.getCommands().performPrefixedCommand(source, command);
            }
        }
    }

    private static void executeCommandAtHit(Entity entity, Vec3 hitPosition, String command) {
        MinecraftServer server = entity.getServer();
        if(server != null) {
            boolean validOutput = !(entity instanceof ServerPlayer) || ((ServerPlayer)entity).connection != null;
            CommandSourceStack source = new CommandSourceStack(
                    false && validOutput ? entity : CommandSource.NULL,
                    hitPosition,
                    entity.getRotationVector(),
                    entity.level() instanceof ServerLevel ? (ServerLevel) entity.level() : null,
                    4,
                    entity.getName().getString(),
                    entity.getDisplayName(),
                    entity.getServer(),
                    entity);
            server.getCommands().performPrefixedCommand(source, command);
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
            direction = Utils.createDirection((JSONObject) data.get("direction"));
            Space space = Utils.getSpaceFromString(data.get("space").toString());
            Vector3f vector3f = new Vector3f((float)direction.x, (float) direction.y, (float) direction.z).normalize();
            space.toGlobal(vector3f, entity);
            direction = new Vec3(vector3f);
        }
        Vec3 target;

        HitResult hitResult = null;
        if((boolean)data.get("entity")) {
            double distance = getEntityReach(data, entity);
            target = origin.add(direction.scale(distance));
            hitResult = performEntityRaycast(entity, origin, target, Optional.empty());
        }
        if((boolean)data.get("block")) {
            double distance = getBlockReach(data, entity);
            target = origin.add(direction.scale(distance));
            BlockHitResult blockHit = performBlockRaycast(entity, origin, target, Utils.getShapeType(data.get("shape_type").toString()), Utils.getFluidHandling(data.get("fluid_handling").toString()));
            if(blockHit.getType() != HitResult.Type.MISS) {
                if(hitResult == null || hitResult.getType() == HitResult.Type.MISS) {
                    hitResult = blockHit;
                } else {
                    if(hitResult.distanceTo(entity) > blockHit.distanceTo(entity)) {
                        hitResult = blockHit;
                    }
                }
            }
        }
        if(hitResult != null && hitResult.getType() != HitResult.Type.MISS) {
            return true;
        }
        return false;
    }
}
