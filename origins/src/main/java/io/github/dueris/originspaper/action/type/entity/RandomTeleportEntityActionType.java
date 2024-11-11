package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.EntityAction;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.condition.BlockCondition;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class RandomTeleportEntityActionType extends EntityActionType {

    public static final TypedDataObjectFactory<RandomTeleportEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
        new SerializableData()
            .add("success_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
            .add("fail_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
            .add("landing_condition", EntityCondition.DATA_TYPE.optional(), Optional.empty())
            .add("landing_block_condition", BlockCondition.DATA_TYPE.optional(), Optional.empty())
            .add("heightmap", SerializableDataType.enumValue(Heightmap.Types.class).optional(), Optional.empty())
            .add("landing_offset", SerializableDataTypes.VECTOR, Vec3.ZERO)
            .add("area_width", SerializableDataTypes.POSITIVE_DOUBLE, 8.0D)
            .add("area_height", SerializableDataTypes.POSITIVE_DOUBLE, 8.0D)
            .add("loaded_chunks_only", SerializableDataTypes.BOOLEAN, true)
            .addFunctionedDefault("attempts", SerializableDataTypes.POSITIVE_INT, data -> (int) ((data.getDouble("area_width") * 2) + (data.getDouble("area_height") * 2))),
        data -> new RandomTeleportEntityActionType(
            data.get("success_action"),
            data.get("fail_action"),
            data.get("landing_condition"),
            data.get("landing_block_condition"),
            data.get("heightmap"),
            data.get("landing_offset"),
            data.getDouble("area_width") * 2,
            data.getDouble("area_height") * 2,
            data.get("loaded_chunks_only"),
            data.get("attempts")
        ),
        (actionType, serializableData) -> serializableData.instance()
            .set("success_action", actionType.successAction)
            .set("fail_action", actionType.failAction)
            .set("landing_condition", actionType.landingCondition)
            .set("landing_block_condition", actionType.landingBlockCondition)
            .set("heightmap", actionType.heightmapType)
            .set("landing_offset", actionType.landingOffset)
            .set("area_width", actionType.areaWidth)
            .set("area_height", actionType.areaHeight)
            .set("loaded_chunks_only", actionType.loadedChunksOnly)
            .set("attempts", actionType.attempts)
    );

    private final Optional<EntityAction> successAction;
    private final Optional<EntityAction> failAction;

    private final Optional<EntityCondition> landingCondition;
    private final Optional<BlockCondition> landingBlockCondition;

    private final Optional<Heightmap.Types> heightmapType;
    private final Vec3 landingOffset;

    private final double areaWidth;
    private final double areaHeight;

    private final boolean loadedChunksOnly;
    private final int attempts;

    public RandomTeleportEntityActionType(Optional<EntityAction> successAction, Optional<EntityAction> failAction, Optional<EntityCondition> landingCondition, Optional<BlockCondition> landingBlockCondition, Optional<Heightmap.Types> heightmapType, Vec3 landingOffset, double areaWidth, double areaHeight, boolean loadedChunksOnly, int attempts) {
        this.successAction = successAction;
        this.failAction = failAction;
        this.landingCondition = landingCondition;
        this.landingBlockCondition = landingBlockCondition;
        this.heightmapType = heightmapType;
        this.landingOffset = landingOffset;
        this.loadedChunksOnly = loadedChunksOnly;
        this.attempts = attempts;
        this.areaWidth = areaWidth;
        this.areaHeight = areaHeight;
    }

    @Override
    protected void execute(Entity entity) {

        if (!(entity.level() instanceof ServerLevel serverWorld)) {
            return;
        }

        RandomSource random = RandomSource.create();
        boolean succeeded = false;

        double x, y, z;

        for (int i = 0; i < attempts; i++) {

            x = entity.getX() + (random.nextDouble() - 0.5) * areaWidth;
            y = Mth.clamp(entity.getY() + (random.nextInt(Math.max((int) areaHeight, 1)) - (areaHeight / 2)), serverWorld.getMinBuildHeight(), serverWorld.getMinBuildHeight() + (serverWorld.getLogicalHeight() - 1));
            z = entity.getZ() + (random.nextDouble() - 0.5) * areaWidth;

            if (attemptToTeleport(entity, serverWorld, x, y, z)) {

                successAction.ifPresent(action -> action.execute(entity));
                entity.resetFallDistance();

                succeeded = true;
                break;

            }

        }

        if (!succeeded) {
            failAction.ifPresent(action -> action.execute(entity));
        }

    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return EntityActionTypes.RANDOM_TELEPORT;
    }

    private boolean attemptToTeleport(Entity entity, ServerLevel serverWorld, double destX, double destY, double destZ) {

        BlockPos.MutableBlockPos blockPos = BlockPos.containing(destX, destY, destZ).mutable();
        boolean foundSurface = false;

        if (heightmapType.isPresent()) {

            blockPos.set(serverWorld.getHeightmapPos(heightmapType.get(), blockPos).below());
            foundSurface |= shouldLandOnBlock(serverWorld, blockPos);

            if (foundSurface) {
                blockPos.set(blockPos.above());
            }

        }

        else {

            for (double decrements = 0; decrements < areaHeight / 2 && !foundSurface; ++decrements) {

                blockPos.set(blockPos.below());
                foundSurface = shouldLandOnBlock(serverWorld, blockPos);

                if (foundSurface) {
                    blockPos.set(blockPos.above());
                }

            }

        }

        destX = landingOffset.x() == 0 ? destX : Mth.floor(destX) + landingOffset.x();
        destY = blockPos.getY() + landingOffset.y();
        destZ = landingOffset.z() == 0 ? destZ : Mth.floor(destZ) + landingOffset.z();

        blockPos.set(destX, destY, destZ);

        if (!foundSurface) {
            return false;
        }

        double prevX = entity.getX();
        double prevY = entity.getY();
        double prevZ = entity.getZ();

        ChunkPos chunkPos = new ChunkPos(blockPos);
        if (!loadedChunksOnly && !serverWorld.hasChunk(chunkPos.x, chunkPos.z)) {
            serverWorld.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkPos, 0, entity.getId());
            serverWorld.getChunk(chunkPos.x, chunkPos.z);
        }

        entity.teleportTo(destX, destY, destZ);

        if (shouldLand(entity)) {
            entity.teleportTo(prevX, prevY, prevZ);
            return false;
        }

        if (entity instanceof PathfinderMob pathAwareEntity) {
            pathAwareEntity.getNavigation().stop();
        }

        return true;

    }

    private boolean shouldLandOnBlock(Level world, BlockPos pos) {
        return landingBlockCondition
            .map(condition -> condition.test(world, pos))
            .orElseGet(() -> world.getBlockState(pos).blocksMotion());
    }

    private boolean shouldLand(Entity entity) {
        return landingCondition
            .map(condition -> condition.test(entity))
            .orElseGet(() -> entity.level().noCollision(entity) && !entity.level().containsAnyLiquid(entity.getBoundingBox()));
    }

}
