package me.dueris.genesismc.factory.powers.apoli;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import com.mojang.datafixers.util.Pair;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.annotations.Register;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.data.types.OptionalInstance;
import me.dueris.calio.data.types.RequiredInstance;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.PowerUpdateEvent;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.function.TriFunction;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class ModifyPlayerSpawnPower extends PowerType implements Listener {
	private final NamespacedKey dimension;
	private final @Nullable ResourceLocation biome;
	private final @Nullable ResourceLocation structure;
	private final SpawnStrategy spawnStrategy;
	private final float dimensionDistanceMultiplier;

	@Register
	public ModifyPlayerSpawnPower(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, NamespacedKey dimension, ResourceLocation biome, ResourceLocation structure, SpawnStrategy spawnStrategy, float dimensionDistanceMultiplier) {
		super(name, description, hidden, condition, loading_priority);
		this.dimension = dimension;
		this.biome = biome;
		this.structure = structure;
		this.spawnStrategy = spawnStrategy;
		this.dimensionDistanceMultiplier = dimensionDistanceMultiplier;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("modify_player_spawn"))
			.add("dimension", NamespacedKey.class, new RequiredInstance())
			.add("biome", ResourceLocation.class, new OptionalInstance())
			.add("structure", ResourceLocation.class, new OptionalInstance())
			.add("spawn_strategy", SpawnStrategy.class, SpawnStrategy.DEFAULT)
			.add("dimension_distance_multiplier", float.class, 0.125F);
	}

	@EventHandler
	public void powerRemove(PowerUpdateEvent e) {
		if (e.isRemoved() && e.getPower().getType().equals(getType())) {
			// Apoli remap start
			ServerPlayer ServerPlayer = ((CraftPlayer) e.getPlayer()).getHandle();
			if (ServerPlayer.hasDisconnected() || ServerPlayer.getRespawnPosition() == null || !ServerPlayer.isRespawnForced())
				return;

			ServerPlayer.setRespawnPosition(Level.OVERWORLD, null, 0F, false, false);
			// Apoli remap end
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void runD(PlayerPostRespawnEvent e) {
		if (!getPlayers().contains(e.getPlayer())) return;
		if (e.getPlayer().getBedSpawnLocation() != null) {
			e.getPlayer().teleport(e.getPlayer().getBedSpawnLocation());
		} else {
			this.teleportToModifiedSpawn(((CraftPlayer) e.getPlayer()).getHandle());
		}
	}

	private ResourceKey<Level> getDimension() {
		return ((CraftWorld) Bukkit.getWorld(dimension)).getHandle().dimension();
	}

	public void teleportToModifiedSpawn(Entity entity) {
		if (false || !(entity instanceof net.minecraft.world.entity.player.Player playerEntity)) return;

		ServerPlayer serverPlayer = (ServerPlayer) playerEntity;
		Pair<ServerLevel, BlockPos> newSpawn = getSpawn(entity);

		if (newSpawn == null) return;
		ServerLevel newSpawnDimension = newSpawn.getFirst();
		BlockPos newSpawnPos = newSpawn.getSecond();

		Vec3 tpPos = DismountHelper.findSafeDismountLocation(playerEntity.getType(), newSpawn.getFirst(), newSpawn.getSecond(), true);
		if (tpPos == null) {
			serverPlayer.teleportTo(newSpawnDimension, newSpawnPos.getX(), newSpawnPos.getY(), newSpawnPos.getZ(), entity.getXRot(), entity.getYRot());
		} else {
			serverPlayer.teleportTo(newSpawnDimension, tpPos.x, tpPos.y, tpPos.z, entity.getXRot(), entity.getYRot());
		}

	}

	public Pair<ServerLevel, BlockPos> getSpawn(Entity entity) {
		ResourceKey<Level> dimension = getDimension();
		if (false || !(entity instanceof net.minecraft.world.entity.player.Player playerEntity)) return null;

		ServerPlayer ServerPlayer = (ServerPlayer) playerEntity;
		MinecraftServer server = ServerPlayer.getServer();
		if (server == null) return null;

		ServerLevel overworldDimension = server.getLevel(Level.OVERWORLD);
		if (overworldDimension == null) {
			return null;
		}

		ServerLevel targetDimension = server.getLevel(dimension);
		if (targetDimension == null) {
			return null;
		}

		int center = targetDimension.getLogicalHeight() / 2;
		int range = 64;

		AtomicReference<Vec3> modifiedSpawnPos = new AtomicReference<>();

		BlockPos regularSpawnBlockPos = overworldDimension.getSharedSpawnPos();
		BlockPos.MutableBlockPos modifiedSpawnBlockPos = new BlockPos.MutableBlockPos();
		BlockPos.MutableBlockPos dimensionSpawnPos = spawnStrategy.apply(regularSpawnBlockPos, center, dimensionDistanceMultiplier).mutable();

		getBiomePos(targetDimension, dimensionSpawnPos).ifPresent(dimensionSpawnPos::set);
		getSpawnPos(targetDimension, dimensionSpawnPos, range, entity).ifPresent(modifiedSpawnPos::set);

		if (modifiedSpawnPos.get() == null) {
			return null;
		}

		Vec3 msp = modifiedSpawnPos.get();
		modifiedSpawnBlockPos.set(msp.x, msp.y, msp.z);
		targetDimension.getChunkSource().addRegionTicket(TicketType.START, new ChunkPos(modifiedSpawnBlockPos), 11, Unit.INSTANCE);

		return new Pair<>(targetDimension, modifiedSpawnBlockPos);

	}

	private Optional<BlockPos> getBiomePos(ServerLevel targetDimension, BlockPos originPos) {
		if (biome == null) return Optional.empty();

		Optional<Biome> targetBiome = CraftRegistry.getMinecraftRegistry().registry(Registries.BIOME).get().getOptional(biome);
		if (targetBiome.isEmpty()) {
			return Optional.empty();
		}
		int radius = 9400;
		int horizontalBlockCheckInterval = 64;
		int verticalBlockCheckInterval = 64;
		if (radius < 0) radius = 6400;
		if (horizontalBlockCheckInterval <= 0) horizontalBlockCheckInterval = 64;
		if (verticalBlockCheckInterval <= 0) verticalBlockCheckInterval = 64;
		Pair<BlockPos, Holder<Biome>> targetBiomePos = targetDimension.findClosestBiome3d((
				biome -> biome.value() == targetBiome.get()),
			originPos,
			radius,
			horizontalBlockCheckInterval,
			verticalBlockCheckInterval
		);
		if (targetBiomePos != null) return Optional.of(targetBiomePos.getFirst());
		else {
			return Optional.empty();
		}
	}

	private Optional<Pair<BlockPos, Structure>> getStructurePos(ServerLevel world) {
		Registry<Structure> structureRegistry = CraftRegistry.getMinecraftRegistry().registry(Registries.STRUCTURE).get();
		ResourceKey<Structure> stRk = GenesisMC.server.registryAccess().registry(Registries.STRUCTURE).get().getResourceKey(structureRegistry.get(structure)).get();
		HolderSet<Structure> structureHolderSet = null;

		if (stRk != null) {
			var entry = structureRegistry.getHolder(stRk);
			if (entry.isPresent()) {
				structureHolderSet = HolderSet.direct(entry.get());
			}
		}

		BlockPos center = new BlockPos(0, 70, 0);

		Pair<BlockPos, Holder<Structure>> struPos = world.getChunkSource().getGenerator().findNearestMapStructure(world, structureHolderSet, center, 100, false);

		BlockPos taStrPos = struPos.getFirst();
		ChunkPos taStrChPos = new ChunkPos(taStrPos.getX() >> 4, taStrPos.getZ() >> 4);
		StructureStart start = world.structureManager().getStartForStructure(SectionPos.of(taStrChPos, 0), struPos.getSecond().value(), world.getChunk(taStrPos));

		return Optional.of(new Pair<>(taStrPos, start.getStructure()));

	}

	private Optional<Vec3> getSpawnPos(ServerLevel targetDimension, BlockPos originPos, int range, Entity entity) {
		if (structure == null) return getValidSpawn(targetDimension, originPos, range, entity);

		Optional<Pair<BlockPos, Structure>> targetStructure = getStructurePos(targetDimension);
		if (targetStructure.isEmpty()) return Optional.empty();

		BlockPos targetStructurePos = targetStructure.get().getFirst();
		ChunkPos targetStructureChunkPos = new ChunkPos(targetStructurePos.getX() >> 4, targetStructurePos.getZ() >> 4);

		StructureStart targetStructureStart = targetDimension.structureManager().getStartForStructure(SectionPos.of(targetStructureChunkPos, 0), targetStructure.get().getSecond(), targetDimension.getChunk(targetStructurePos));
		if (targetStructureStart == null) return Optional.empty();

		BlockPos targetStructureCenter = new BlockPos(targetStructureStart.getBoundingBox().getCenter());
		return getValidSpawn(targetDimension, targetStructureCenter, range, entity);

	}

	private Optional<Vec3> getValidSpawn(ServerLevel targetDimension, BlockPos startPos, int range, Entity entity) {
		//  The 'direction' vector that determines the direction of the iteration
		int dx = 1;
		int dz = 0;

		//  The length of the current segment
		int segmentLength = 1;

		//  The center of the structure/dimension
		int center = startPos.getY();

		//  The valid spawn position and (mutable) starting position
		Vec3 spawnPos;
		BlockPos.MutableBlockPos mutableStartPos = startPos.mutable();

		//  The current position
		int x = startPos.getX();
		int z = startPos.getZ();

		//  Determines how much of the current segment has been passed
		int segmentPassed = 0;

		//  Vertical offsets
		int upOffset = 0;
		int downOffset = 0;

		//  The min and max Y values of the target dimension
		int maxY = targetDimension.getLogicalHeight();
		int minY = targetDimension.dimensionTypeRegistration().value().minY();

		while (upOffset < maxY || downOffset > minY) {
			for (int steps = 0; steps < range; ++steps) {
				//  Make a step by adding the 'direction' vector to the current position
				x += dx;
				z += dz;
				mutableStartPos.setX(x);
				mutableStartPos.setZ(z);

				//  Increment how much of the current segment has been passed
				++segmentPassed;

				//  Offset the Y axis (up and down) of the current position to check for valid spawn positions
				mutableStartPos.setY(center + upOffset);
				spawnPos = DismountHelper.findSafeDismountLocation(entity.getType(), targetDimension, mutableStartPos, true);
				if (spawnPos != null) return Optional.of(spawnPos);

				mutableStartPos.setY(center + downOffset);
				spawnPos = DismountHelper.findSafeDismountLocation(entity.getType(), targetDimension, mutableStartPos, true);
				if (spawnPos != null) return Optional.of(spawnPos);

				//  If the current segment has not been passed, continue the loop
				if (segmentPassed != segmentLength) continue;

				//  Otherwise, reset the value of how much of the current segment has been passed
				segmentPassed = 0;

				//  'Rotate' the 'direction' vector
				int bdx = dx;
				dx = -dz;
				dz = bdx;

				//  Increment the length of the current segment if necessary
				if (dz == 0) ++segmentLength;

			}

			//  Increment/decrement the up/down offsets until it's no longer less/greater than the max/min Y
			if (upOffset < maxY) upOffset++;
			if (downOffset > minY) downOffset--;

		}

		return Optional.empty();

	}

	public enum SpawnStrategy {
		CENTER((blockPos, center, multiplier) -> new BlockPos(0, center, 0)),
		DEFAULT(
			(blockPos, center, multiplier) -> {
				BlockPos.MutableBlockPos mut = new BlockPos.MutableBlockPos();

				if (multiplier != 0)
					mut.set(blockPos.getX() * multiplier, blockPos.getY(), blockPos.getZ() * multiplier);
				else mut.set(blockPos);

				return mut;

			}
		);

		final TriFunction<BlockPos, Integer, Float, BlockPos> strategyApplier;

		SpawnStrategy(TriFunction<BlockPos, Integer, Float, BlockPos> strategyApplier) {
			this.strategyApplier = strategyApplier;
		}

		public BlockPos apply(BlockPos blockPos, int center, float multiplier) {
			return strategyApplier.apply(blockPos, center, multiplier);
		}

	}
}
