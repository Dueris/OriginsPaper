package io.github.dueris.originspaper.power;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import net.minecraft.ChatFormatting;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Tuple;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.function.TriFunction;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class ModifyPlayerSpawnPower extends PowerType {
	private final ResourceKey<Level> dimensionKey;

	private final ResourceKey<Structure> structureKey;
	private final TagKey<Structure> structureTag;

	private final ResourceKey<Biome> biomeKey;
	private final TagKey<Biome> biomeTag;

	private final SpawnStrategy spawnStrategy;
	private final SoundEvent respawnSound;

	private final float dimensionDistanceMultiplier;

	public ModifyPlayerSpawnPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority,
								  ResourceKey<Level> dimensionKey, ResourceKey<Structure> structureKey, TagKey<Structure> structureTag, ResourceKey<Biome> biomeKey, TagKey<Biome> biomeTag, SpawnStrategy spawnStrategy,
								  SoundEvent respawnSound, float dimensionDistanceMultiplier) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.dimensionKey = dimensionKey;
		this.structureKey = structureKey;
		this.structureTag = structureTag;
		this.biomeKey = biomeKey;
		this.biomeTag = biomeTag;
		this.spawnStrategy = spawnStrategy;
		this.respawnSound = respawnSound;
		this.dimensionDistanceMultiplier = dimensionDistanceMultiplier;
	}

	public static SerializableData buildFactory() {
		return PowerType.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("modify_player_spawn"))
			.add("dimension", SerializableDataTypes.DIMENSION)
			.add("structure", SerializableDataTypes.registryKey(Registries.STRUCTURE), null)
			.add("structure_tag", SerializableDataTypes.tag(Registries.STRUCTURE), null)
			.add("biome", SerializableDataTypes.registryKey(Registries.BIOME), null)
			.add("biome_tag", SerializableDataTypes.BIOME_TAG, null)
			.add("spawn_strategy", SerializableDataTypes.enumValue(SpawnStrategy.class), SpawnStrategy.DEFAULT)
			.add("respawn_sound", SerializableDataTypes.SOUND_EVENT, null)
			.add("dimension_distance_multiplier", SerializableDataTypes.FLOAT, 0F);
	}

	@EventHandler
	public void onRespawn(@NotNull PlayerPostRespawnEvent e) {
		Player player = ((CraftPlayer) e.getPlayer()).getHandle();

		if (getPlayers().contains(player) && isActive(player) && player.getBukkitEntity().getPotentialBedLocation() == null) {
			teleportToModifiedSpawn(player);
			if (respawnSound != null) {
				player.level().playSound(null, player.getX(), player.getY(), player.getZ(), respawnSound, player.getSoundSource(), 1.0F, 1.0F);
			}
		}
	}

	public void teleportToModifiedSpawn(Entity entity) {

		if (!(entity instanceof ServerPlayer serverPlayer)) {
			return;
		}

		Tuple<ServerLevel, BlockPos> spawnPoint = this.getSpawn(entity).orElse(null);
		if (spawnPoint == null) {
			OriginsPaper.getPlugin().getLog4JLogger().warn("Power \"{}\" couldnt build spawn point for player!", getTag());
			return;
		}

		ServerLevel spawnPointDimension = spawnPoint.getA();
		BlockPos spawnPointPosition = spawnPoint.getB();

		float pitch = serverPlayer.getXRot();
		float yaw = serverPlayer.getYRot();

		Vec3 placement = DismountHelper.findSafeDismountLocation(serverPlayer.getType(), spawnPointDimension, spawnPointPosition, true);
		if (placement == null) {
			OriginsPaper.getPlugin().getLog4JLogger().warn("Power \"{}\" could not find a suitable spawn point for player {}! Teleporting to the found location directly...", getTag(), entity.getName().getString());
			Location bukkitLocation = new Location(spawnPointDimension.getWorld(), spawnPointPosition.getX(), spawnPointPosition.getY(), spawnPointPosition.getZ(), pitch, yaw);
			serverPlayer.getBukkitEntity().teleport(bukkitLocation);
		} else {
			Location bukkitLocation = new Location(spawnPointDimension.getWorld(), placement.x, placement.y, placement.z, pitch, yaw);
			serverPlayer.getBukkitEntity().teleport(bukkitLocation);
		}

	}

	public Optional<Tuple<ServerLevel, BlockPos>> getSpawn(Entity entity) {

		if (!(entity instanceof ServerPlayer serverPlayer)) {
			return Optional.empty();
		}

		MinecraftServer server = serverPlayer.server;
		ServerLevel targetDimension = server.getLevel(dimensionKey);

		if (targetDimension == null) {
			return Optional.empty();
		}

		int center = targetDimension.getLogicalHeight() / 2;
		int range = 64;

		AtomicReference<Vec3> newSpawnPointVec = new AtomicReference<>();
		BlockPos dimensionSpawnPos = serverPlayer.serverLevel().getSharedSpawnPos();

		BlockPos.MutableBlockPos newSpawnPointPos = new BlockPos.MutableBlockPos();
		BlockPos.MutableBlockPos mutableDimensionSpawnPos = spawnStrategy.apply(dimensionSpawnPos, center, dimensionDistanceMultiplier).mutable();

		this.getBiomePos(entity, targetDimension, mutableDimensionSpawnPos).ifPresent(mutableDimensionSpawnPos::set);
		this.getSpawnPos(entity, targetDimension, mutableDimensionSpawnPos, range).ifPresent(newSpawnPointVec::set);

		if (newSpawnPointVec.get() == null) {
			return Optional.empty();
		}

		Vec3 msp = newSpawnPointVec.get();
		newSpawnPointPos.set(msp.x, msp.y, msp.z);

		targetDimension.getChunkSource().addRegionTicket(TicketType.START, new ChunkPos(newSpawnPointPos), 11, Unit.INSTANCE);
		return Optional.of(new Tuple<>(targetDimension, newSpawnPointPos));

	}

	private Optional<BlockPos> getBiomePos(Entity entity, ServerLevel targetDimension, BlockPos originPos) {

		if (biomeKey == null && biomeTag == null) {
			return Optional.empty();
		}

		int radius = 6400;
		int horizontalBlockCheckInterval = 64;
		int verticalBlockCheckInterval = 64;

		var targetBiomePos = targetDimension.findClosestBiome3d(
			biome -> (biomeKey == null || biome.is(this.biomeKey)) || (biomeTag == null || biome.is(biomeTag)),
			originPos,
			radius,
			horizontalBlockCheckInterval,
			verticalBlockCheckInterval
		);

		if (targetBiomePos != null) {
			return Optional.of(targetBiomePos.getFirst());
		} else {

			StringBuilder name = new StringBuilder();
			if (biomeKey != null) {
				name.append("biome \"").append(biomeKey.location()).append("\"");
			}

			if (biomeTag != null) {
				name.append(!name.isEmpty() ? " or " : "").append("any biomes from tag \"").append(biomeTag.location()).append("\"");
			}

			OriginsPaper.getPlugin().getLog4JLogger().warn("Power \"{}\" could not set player {}'s spawn point at {} as none can be found nearby in dimension \"{}\".", getTag(), entity.getName().getString(), name, dimensionKey.location());
			entity.sendSystemMessage(Component.literal("Power \"%s\" couldn't set spawn point at %s as none can be found nearby in dimension \"%s\"!".formatted(getTag(), name, dimensionKey.location())).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));

			return Optional.empty();

		}

	}

	@SuppressWarnings("unchecked")
	private Optional<Tuple<BlockPos, Structure>> getStructurePos(Entity entity, ServerLevel dimension) {

		if (structureKey == null && structureTag == null) {
			return Optional.empty();
		}

		Registry<Structure> structureRegistry = dimension.registryAccess().registryOrThrow(Registries.STRUCTURE);
		List<Holder<Structure>> structureEntries = new ArrayList<>();

		if (structureKey != null) {
			structureEntries.add(structureRegistry.getHolderOrThrow(structureKey));
		}

		if (structureTag != null) {
			structureRegistry.getTag(structureTag)
				.map(el -> (HolderSet.ListBacked<Structure>) el)
				.ifPresent(listBacked -> {
					try {
						Method contentsMethod = HolderSet.ListBacked.class.getDeclaredMethod("contents");
						contentsMethod.setAccessible(true);

						List<Holder<Structure>> contents = (List<Holder<Structure>>) contentsMethod.invoke(listBacked);

						structureEntries.addAll(contents);

					} catch (Exception e) {
						e.printStackTrace();
					}
				});
		}

		BlockPos center = new BlockPos(0, 70, 0);
		int radius = 6400;

		var result = Optional.ofNullable(dimension.getChunkSource().getGenerator().findNearestMapStructure(dimension, HolderSet.direct(structureEntries), center, radius, false))
			.map(pair -> pair.mapSecond(Holder::value))
			.map(pair -> new Tuple<>(pair.getFirst(), pair.getSecond()));

		if (result.isEmpty()) {

			StringBuilder name = new StringBuilder();
			if (structureKey != null) {
				name.append("structure \"").append(structureKey.location()).append("\"");
			}

			if (structureTag != null) {
				name.append(!name.isEmpty() ? " or " : "").append("any structures from tag \"").append(structureTag.location()).append("\"");
			}

			OriginsPaper.getPlugin().getLog4JLogger().warn("Power \"{}\" could not set player {}'s spawn point at {} as none can be found nearby in dimension \"{}\".", getTag(), entity.getName().getString(), name, dimensionKey.location());
			entity.sendSystemMessage(Component.literal("Power \"%s\" couldn't set spawn point at %s as none can be found nearby in dimension \"%s\"!".formatted(getTag(), name, dimensionKey.location())).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));

			return Optional.empty();

		}

		return result;

	}

	private Optional<Vec3> getSpawnPos(Entity entity, ServerLevel targetDimension, BlockPos originPos, int range) {

		if (structureKey == null && structureTag == null) {
			return this.getValidSpawn(entity, targetDimension, originPos, range);
		}

		Optional<Tuple<BlockPos, Structure>> targetStructure = getStructurePos(entity, targetDimension);
		if (targetStructure.isEmpty()) {
			return Optional.empty();
		}

		BlockPos targetStructurePos = targetStructure.get().getA();
		ChunkPos targetStructureChunkPos = new ChunkPos(targetStructurePos.getX() >> 4, targetStructurePos.getZ() >> 4);

		StructureStart targetStructureStart = targetDimension.structureManager().getStartForStructure(SectionPos.of(targetStructureChunkPos, 0), targetStructure.get().getB(), targetDimension.getChunk(targetStructurePos));
		if (targetStructureStart == null) {
			return Optional.empty();
		}

		BlockPos targetStructureCenter = new BlockPos(targetStructureStart.getBoundingBox().getCenter());
		return this.getValidSpawn(entity, targetDimension, targetStructureCenter, range);

	}

	private Optional<Vec3> getValidSpawn(Entity entity, ServerLevel targetDimension, BlockPos startPos, int range) {

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

				if (spawnPos != null) {
					return Optional.of(spawnPos);
				}

				mutableStartPos.setY(center + downOffset);
				spawnPos = DismountHelper.findSafeDismountLocation(entity.getType(), targetDimension, mutableStartPos, true);

				if (spawnPos != null) {
					return Optional.of(spawnPos);
				}

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
			if (upOffset < maxY) {
				upOffset++;
			}

			if (downOffset > minY) {
				downOffset--;
			}

		}

		return Optional.empty();

	}

	public enum SpawnStrategy {

		CENTER((blockPos, center, multiplier) -> new BlockPos(0, center, 0)),
		DEFAULT((blockPos, center, multiplier) -> {

			BlockPos.MutableBlockPos mut = new BlockPos.MutableBlockPos();

			if (multiplier != 0) {
				mut.set(blockPos.getX() * multiplier, blockPos.getY(), blockPos.getZ() * multiplier);
			} else {
				mut.set(blockPos);
			}

			return mut;

		});

		final TriFunction<BlockPos, Integer, Float, BlockPos> strategyApplier;

		SpawnStrategy(TriFunction<BlockPos, Integer, Float, BlockPos> strategyApplier) {
			this.strategyApplier = strategyApplier;
		}

		public BlockPos apply(BlockPos blockPos, int center, float multiplier) {
			return strategyApplier.apply(blockPos, center, multiplier);
		}

	}
}
