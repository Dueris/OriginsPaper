package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerConfiguration;
import net.minecraft.ChatFormatting;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Tuple;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class ModifyPlayerSpawnPowerType extends PowerType implements Prioritized<ModifyPlayerSpawnPowerType> {

	public static final TypedDataObjectFactory<ModifyPlayerSpawnPowerType> DATA_FACTORY = createConditionedDataFactory(
		new SerializableData()
			.add("dimension", SerializableDataTypes.DIMENSION)
			.add("structure", SerializableDataType.registryKey(Registries.STRUCTURE).optional(), Optional.empty())
			.add("structure_tag", SerializableDataType.tagKey(Registries.STRUCTURE).optional(), Optional.empty())
			.add("biome", SerializableDataType.registryKey(Registries.BIOME).optional(), Optional.empty())
			.add("biome_tag", SerializableDataType.tagKey(Registries.BIOME).optional(), Optional.empty())
			.add("spawn_strategy", SerializableDataType.enumValue(SpawnStrategy.class), SpawnStrategy.DEFAULT)
			.add("respawn_sound", SerializableDataTypes.SOUND_EVENT.optional(), Optional.empty())
			.add("dimension_distance_multiplier", SerializableDataTypes.NON_NEGATIVE_FLOAT, 1.0F)
			.add("priority", SerializableDataTypes.INT, 0),
		(data, condition) -> new ModifyPlayerSpawnPowerType(
			data.get("dimension"),
			data.get("structure"),
			data.get("structure_tag"),
			data.get("biome"),
			data.get("biome_tag"),
			data.get("spawn_strategy"),
			data.get("respawn_sound"),
			data.get("dimension_distance_multiplier"),
			data.get("priority"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("dimension", powerType.dimensionKey)
			.set("structure", powerType.structureKey)
			.set("structure_tag", powerType.structureTag)
			.set("biome", powerType.biomeKey)
			.set("biome_tag", powerType.biomeTag)
			.set("spawn_strategy", powerType.spawnStrategy)
			.set("respawn_sound", powerType.respawnSound)
			.set("dimension_distance_multiplier", powerType.dimensionDistanceMultiplier)
			.set("priority", powerType.getPriority())
	);

	private final ResourceKey<Level> dimensionKey;

	private final Optional<ResourceKey<Structure>> structureKey;
	private final Optional<TagKey<Structure>> structureTag;

	private final Optional<ResourceKey<Biome>> biomeKey;
	private final Optional<TagKey<Biome>> biomeTag;

	private final SpawnStrategy spawnStrategy;
	private final Optional<SoundEvent> respawnSound;

	private final float dimensionDistanceMultiplier;
	private final int priority;

	public ModifyPlayerSpawnPowerType(ResourceKey<Level> dimensionKey, Optional<ResourceKey<Structure>> structureKey, Optional<TagKey<Structure>> structureTag, Optional<ResourceKey<Biome>> biomeKey, Optional<TagKey<Biome>> biomeTag, SpawnStrategy spawnStrategy, Optional<SoundEvent> respawnSound, float dimensionDistanceMultiplier, int priority, Optional<EntityCondition> condition) {
		super(condition);
		this.dimensionKey = dimensionKey;
		this.structureKey = structureKey;
		this.structureTag = structureTag;
		this.biomeKey = biomeKey;
		this.biomeTag = biomeTag;
		this.spawnStrategy = spawnStrategy;
		this.respawnSound = respawnSound;
		this.dimensionDistanceMultiplier = dimensionDistanceMultiplier;
		this.priority = priority;
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.MODIFY_PLAYER_SPAWN;
	}

	@Override
	public void onRespawn() {
		LivingEntity entity = getHolder();
		respawnSound.ifPresent(soundEvent -> entity.level().playSound(null, entity.getX(), entity.getY(), entity.getX(), soundEvent, entity.getSoundSource(), 1.0F, 1.0F));
	}

	@Override
	public void onLost() {

		if (!(getHolder() instanceof ServerPlayer serverPlayer)) {
			return;
		}

		if (!serverPlayer.hasDisconnected() && serverPlayer.getRespawnPosition() != null && !serverPlayer.isRespawnForced()) {
			serverPlayer.setRespawnPosition(Level.OVERWORLD, null, 0F, false, false);
		}

	}

	@Override
	public int getPriority() {
		return priority;
	}

	public ResourceKey<Level> getDimensionKey() {
		return dimensionKey;
	}

	public void teleportToModifiedSpawn() {

		if (!(getHolder() instanceof ServerPlayer serverPlayer)) {
			return;
		}

		Tuple<ServerLevel, BlockPos> spawnPoint = this.getSpawn().orElse(null);
		if (spawnPoint == null) {
			return;
		}

		ServerLevel spawnPointDimension = spawnPoint.getA();
		BlockPos spawnPointPosition = spawnPoint.getB();

		float pitch = serverPlayer.getXRot();
		float yaw = serverPlayer.getYRot();

		Vec3 placement = DismountHelper.findSafeDismountLocation(serverPlayer.getType(), spawnPointDimension, spawnPointPosition, true);
		if (placement == null) {
			OriginsPaper.LOGGER.warn("Power \"{}\" could not find a suitable spawn point for player {}! Teleporting to the found location directly...", this.getPower().getId(), serverPlayer.getName().getString());
			serverPlayer.teleportTo(spawnPointDimension, spawnPointPosition.getX(), spawnPointPosition.getY(), spawnPointPosition.getZ(), pitch, yaw);
		} else {
			serverPlayer.teleportTo(spawnPointDimension, placement.x(), placement.y(), placement.z(), pitch, yaw);
		}

	}

	public Optional<Tuple<ServerLevel, BlockPos>> getSpawn() {

		if (!(getHolder() instanceof ServerPlayer serverPlayer)) {
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

		this.getBiomePos(targetDimension, mutableDimensionSpawnPos).ifPresent(mutableDimensionSpawnPos::set);
		this.getSpawnPos(targetDimension, mutableDimensionSpawnPos, range).ifPresent(newSpawnPointVec::set);

		if (newSpawnPointVec.get() == null) {
			return Optional.empty();
		}

		Vec3 msp = newSpawnPointVec.get();
		newSpawnPointPos.set(msp.x, msp.y, msp.z);

		targetDimension.getChunkSource().addRegionTicket(TicketType.START, new ChunkPos(newSpawnPointPos), 11, Unit.INSTANCE);
		return Optional.of(new Tuple<>(targetDimension, newSpawnPointPos));

	}

	private Optional<BlockPos> getBiomePos(ServerLevel targetDimension, BlockPos originPos) {

		if (biomeKey.isEmpty() && biomeTag.isEmpty()) {
			return Optional.empty();
		}

		int radius = OriginsPaper.config.modifyPlayerSpawnPower.radius;
		int horizontalBlockCheckInterval = OriginsPaper.config.modifyPlayerSpawnPower.horizontalBlockCheckInterval;
		int verticalBlockCheckInterval = OriginsPaper.config.modifyPlayerSpawnPower.verticalBlockCheckInterval;

		if (radius < 0) {
			radius = 6400;
		}

		if (horizontalBlockCheckInterval <= 0) {
			horizontalBlockCheckInterval = 64;
		}

		if (verticalBlockCheckInterval <= 0) {
			verticalBlockCheckInterval = 64;
		}

		var targetBiomePos = targetDimension.findClosestBiome3d(
			biome -> biomeKey.map(biome::is).orElse(false) || biomeTag.map(biome::is).orElse(false),
			originPos,
			radius,
			horizontalBlockCheckInterval,
			verticalBlockCheckInterval
		);

		if (targetBiomePos != null) {
			return Optional.of(targetBiomePos.getFirst());
		} else {

			LivingEntity holder = getHolder();
			StringBuilder name = new StringBuilder();

			biomeKey
				.map(ResourceKey::location)
				.ifPresent(id -> name.append("biome \"").append(id).append("\""));
			biomeTag
				.map(TagKey::location)
				.ifPresent(id -> name.append(!name.isEmpty() ? " or " : "").append("any biomes from tag \"").append(id).append("\""));

			OriginsPaper.LOGGER.warn("Power \"{}\" could not set player {}'s spawn point at {} as none can be found nearby in dimension \"{}\".", this.getPower().getId(), holder.getName().getString(), name, dimensionKey.location());
			holder.sendSystemMessage(Component.literal("Power \"%s\" couldn't set spawn point at %s as none can be found nearby in dimension \"%s\"!".formatted(this.getPower().getId(), name, dimensionKey.location()).formatted(ChatFormatting.ITALIC, ChatFormatting.GRAY)));

			return Optional.empty();

		}

	}

	private Optional<Tuple<BlockPos, Structure>> getStructurePos(ServerLevel dimension) {

		if (structureKey.isEmpty() && structureTag.isEmpty()) {
			return Optional.empty();
		}

		Registry<Structure> structureRegistry = dimension.registryAccess().registryOrThrow(Registries.STRUCTURE);
		List<Holder<Structure>> structureEntries = new ArrayList<>();

		structureKey
			.map(structureRegistry::getHolderOrThrow)
			.ifPresent(structureEntries::add);

		structureTag
			.flatMap(structureRegistry::getTag)
			.map(registryEntries -> (HolderSet.ListBacked<Structure>) registryEntries)
			.map(HolderSet.ListBacked::contents)
			.ifPresent(structureEntries::addAll);

		BlockPos center = new BlockPos(0, 70, 0);
		int radius = OriginsPaper.config.modifyPlayerSpawnPower.radius;

		if (radius < 0) {
			radius = 6400;
		}

		var result = Optional.ofNullable(dimension.getChunkSource().getGenerator().findNearestMapStructure(dimension, HolderSet.direct(structureEntries), center, radius, false))
			.map(pair -> pair.mapSecond(Holder::value))
			.map(pair -> new Tuple<>(pair.getFirst(), pair.getSecond()));

		if (result.isEmpty()) {

			LivingEntity holder = getHolder();
			StringBuilder name = new StringBuilder();

			structureKey
				.map(ResourceKey::location)
				.ifPresent(id -> name.append("structure \"").append(id).append("\""));
			structureTag
				.map(TagKey::location)
				.ifPresent(id -> name.append(!name.isEmpty() ? " or " : "").append("any structures from tag \"").append(id).append("\""));

			OriginsPaper.LOGGER.warn("Power \"{}\" could not set player {}'s spawn point at {} as none can be found nearby in dimension \"{}\".", this.getPower().getId(), holder.getName().getString(), name, dimensionKey.location());
			holder.sendSystemMessage(Component.literal("Power \"%s\" couldn't set spawn point at %s as none can be found nearby in dimension \"%s\"!".formatted(this.getPower().getId(), name, dimensionKey.location()).formatted(ChatFormatting.ITALIC, ChatFormatting.GRAY)));

			return Optional.empty();

		}

		return result;

	}

	private Optional<Vec3> getSpawnPos(ServerLevel targetDimension, BlockPos originPos, int range) {

		if (structureKey.isEmpty() && structureTag.isEmpty()) {
			return this.getValidSpawn(targetDimension, originPos, range);
		}

		Optional<Tuple<BlockPos, Structure>> targetStructure = getStructurePos(targetDimension);
		if (targetStructure.isEmpty()) {
			return Optional.empty();
		}

		BlockPos structurePos = targetStructure.get().getA();
		Structure structure = targetStructure.get().getB();

		ChunkPos chunkPos = new ChunkPos(structurePos.getX() >> 4, structurePos.getZ() >> 4);
		SectionPos chunkSectionPos = SectionPos.of(chunkPos, 0);

		return Optional.ofNullable(targetDimension.structureManager().getStartForStructure(chunkSectionPos, structure, targetDimension.getChunk(structurePos)))
			.map(structureStart -> structureStart.getBoundingBox().getCenter())
			.flatMap(pos -> this.getValidSpawn(targetDimension, pos, range));

	}

	private Optional<Vec3> getValidSpawn(ServerLevel targetDimension, BlockPos startPos, int range) {

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
				spawnPos = DismountHelper.findSafeDismountLocation(getHolder().getType(), targetDimension, mutableStartPos, true);

				if (spawnPos != null) {
					return Optional.of(spawnPos);
				}

				mutableStartPos.setY(center + downOffset);
				spawnPos = DismountHelper.findSafeDismountLocation(getHolder().getType(), targetDimension, mutableStartPos, true);

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
			multiplier = Math.max(multiplier, 1F);

			return mut.set(blockPos.getX() * multiplier, blockPos.getY(), blockPos.getZ() * multiplier);

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
