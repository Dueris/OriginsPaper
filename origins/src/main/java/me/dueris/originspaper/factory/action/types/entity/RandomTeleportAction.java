package me.dueris.originspaper.factory.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.data.ApoliDataTypes;
import me.dueris.originspaper.factory.action.ActionFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class RandomTeleportAction {

	public static void action(DeserializedFactoryJson data, @NotNull Entity entity) {

		if (!(entity.level() instanceof ServerLevel serverWorld)) {
			return;
		}

		Predicate<BlockInWorld> landingBlockCondition = data.isPresent("landing_block_condition") ? data.get("landing_block_condition")
			: cachedBlockPosition -> cachedBlockPosition.getState().blocksMotion();
		Predicate<Entity> landingCondition = data.isPresent("landing_condition") ? data.get("landing_condition")
			: _entity -> serverWorld.noCollision(_entity) && !serverWorld.containsAnyLiquid(_entity.getBoundingBox());

		Heightmap.Types heightmap = data.get("heightmap");
		RandomSource random = RandomSource.create();
		Vec3 landingOffset = data.get("landing_offset");

		boolean loadedChunksOnly = data.getBoolean("loaded_chunks_only");
		boolean succeeded = false;

		int attempts = data.isPresent("attempts") ? (int) ((data.getDouble("area_width") * 2) + (data.getDouble("area_height") * 2)) : 1;

		double areaWidth = data.getDouble("area_width") * 2;
		double areaHeight = data.getDouble("area_height") * 2;
		double x, y, z;

		for (int i = 0; i < attempts; i++) {

			x = entity.getX() + (random.nextDouble() - 0.5) * areaWidth;
			y = Mth.clamp(entity.getY() + (random.nextInt(Math.max((int) areaHeight, 1)) - (areaHeight / 2)), serverWorld.getMinBuildHeight(), serverWorld.getMinBuildHeight() + (serverWorld.getLogicalHeight() - 1));
			z = entity.getZ() + (random.nextDouble() - 0.5) * areaWidth;

			if (attemptToTeleport(entity, serverWorld, x, y, z, landingOffset.x(), landingOffset.y(), landingOffset.z(), areaHeight, loadedChunksOnly, heightmap, landingBlockCondition, landingCondition)) {

				data.<Consumer<Entity>>ifPresent("success_action", successAction -> successAction.accept(entity));
				entity.resetFallDistance();

				succeeded = true;
				break;

			}

		}

		if (!succeeded) {
			data.<Consumer<Entity>>ifPresent("fail_action", failAction -> failAction.accept(entity));
		}

	}

	private static boolean attemptToTeleport(Entity entity, ServerLevel serverWorld, double destX, double destY, double destZ, double offsetX, double offsetY, double offsetZ, double areaHeight, boolean loadedChunksOnly, Heightmap.Types heightmap, Predicate<BlockInWorld> landingBlockCondition, Predicate<Entity> landingCondition) {

		BlockPos.MutableBlockPos blockPos = BlockPos.containing(destX, destY, destZ).mutable();
		boolean foundSurface = false;

		if (heightmap != null) {

			blockPos.set(serverWorld.getHeightmapPos(heightmap, blockPos).below());

			if (landingBlockCondition.test(new BlockInWorld(serverWorld, blockPos, true))) {
				blockPos.set(blockPos.above());
				foundSurface = true;
			}

		} else {

			for (double decrements = 0; decrements < areaHeight / 2; ++decrements) {

				blockPos.set(blockPos.below());

				if (landingBlockCondition.test(new BlockInWorld(serverWorld, blockPos, true))) {

					blockPos.set(blockPos.above());
					foundSurface = true;

					break;

				}

			}

		}

		destX = offsetX == 0 ? destX : Mth.floor(destX) + offsetX;
		destY = blockPos.getY() + offsetY;
		destZ = offsetZ == 0 ? destZ : Mth.floor(destZ) + offsetZ;

		blockPos.set(destX, destY, destZ);

		if (!foundSurface) {
			return false;
		}

		double prevX = entity.getX();
		double prevY = entity.getY();
		double prevZ = entity.getZ();

		ChunkPos chunkPos = new ChunkPos(blockPos);
		if (!serverWorld.hasChunk(chunkPos.x, chunkPos.z)) {

			if (loadedChunksOnly) {
				return false;
			}

			serverWorld.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkPos, 0, entity.getId());
			serverWorld.getChunk(chunkPos.x, chunkPos.z);

		}

		entity.teleportTo(destX, destY, destZ);

		if (!landingCondition.test(entity)) {
			entity.teleportTo(prevX, prevY, prevZ);
			return false;
		}

		if (entity instanceof PathfinderMob pathAwareEntity) {
			pathAwareEntity.getNavigation().stop();
		}

		return true;

	}

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(
			OriginsPaper.apoliIdentifier("random_teleport"),
			InstanceDefiner.instanceDefiner()
				.add("area_width", SerializableDataTypes.DOUBLE, 8.0)
				.add("area_height", SerializableDataTypes.DOUBLE, 8.0)
				.add("heightmap", SerializableDataTypes.enumValue(Heightmap.Types.class), null)
				.add("attempts", SerializableDataTypes.INT, 0)
				.add("landing_block_condition", ApoliDataTypes.BLOCK_CONDITION, null)
				.add("landing_condition", ApoliDataTypes.ENTITY_CONDITION, null)
				.add("landing_offset", SerializableDataTypes.VECTOR, Vec3.ZERO)
				.add("loaded_chunks_only", SerializableDataTypes.BOOLEAN, true)
				.add("success_action", ApoliDataTypes.ENTITY_ACTION, null)
				.add("fail_action", ApoliDataTypes.ENTITY_ACTION, null),
			RandomTeleportAction::action
		);
	}
}
