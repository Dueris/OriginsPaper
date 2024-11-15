package io.github.dueris.originspaper.condition.type;

import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.util.IdentifierAlias;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.BlockCondition;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.block.*;
import io.github.dueris.originspaper.condition.type.block.meta.*;
import io.github.dueris.originspaper.condition.type.meta.*;
import io.github.dueris.originspaper.registry.ApoliRegistries;
import net.minecraft.core.Registry;

public class BlockConditionTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();
	public static final SerializableDataType<ConditionConfiguration<BlockConditionType>> DATA_TYPE = SerializableDataType.registry(ApoliRegistries.BLOCK_CONDITION_TYPE, "apoli", ALIASES, (configurations, id) -> "Block condition type \"" + id + "\" is undefined!");

	public static final ConditionConfiguration<AllOfBlockConditionType> ALL_OF = register(AllOfMetaConditionType.createConfiguration(BlockCondition.DATA_TYPE, AllOfBlockConditionType::new));
	public static final ConditionConfiguration<AnyOfBlockConditionType> ANY_OF = register(AnyOfMetaConditionType.createConfiguration(BlockCondition.DATA_TYPE, AnyOfBlockConditionType::new));
	public static final ConditionConfiguration<ConstantBlockConditionType> CONSTANT = register(ConstantMetaConditionType.createConfiguration(ConstantBlockConditionType::new));
	public static final ConditionConfiguration<RandomChanceBlockConditionType> RANDOM_CHANCE = register(RandomChanceMetaConditionType.createConfiguration(RandomChanceBlockConditionType::new));

	public static final ConditionConfiguration<DistanceFromCoordinatesBlockConditionType> DISTANCE_FROM_COORDINATES = register(DistanceFromCoordinatesMetaConditionType.createConfiguration(DistanceFromCoordinatesBlockConditionType::new));
	public static final ConditionConfiguration<OffsetBlockConditionType> OFFSET = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("offset"), OffsetBlockConditionType.DATA_FACTORY));

	public static final ConditionConfiguration<AdjacentBlockConditionType> ADJACENT = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("adjacent"), AdjacentBlockConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<AttachableBlockConditionType> ATTACHABLE = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("attachable"), AttachableBlockConditionType::new));
	public static final ConditionConfiguration<BlastResistanceBlockConditionType> BLAST_RESISTANCE = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("blast_resistance"), BlastResistanceBlockConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<BlockBlockConditionType> BLOCK = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("block"), BlockBlockConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<BlockEntityBlockConditionType> BLOCK_ENTITY = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("block_entity"), BlockEntityBlockConditionType::new));
	public static final ConditionConfiguration<BlockStateBlockConditionType> BLOCK_STATE = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("block_state"), BlockStateBlockConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<CommandBlockConditionType> COMMAND = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("command"), CommandBlockConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<ExposedToSkyBlockConditionType> EXPOSED_TO_SKY = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("exposed_to_sky"), ExposedToSkyBlockConditionType::new));
	public static final ConditionConfiguration<FluidBlockConditionType> FLUID = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("fluid"), FluidBlockConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<HardnessBlockConditionType> HARDNESS = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("hardness"), HardnessBlockConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<HeightBlockConditionType> HEIGHT = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("height"), HeightBlockConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<InTagBlockConditionType> IN_TAG = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("in_tag"), InTagBlockConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<LightBlockingBlockConditionType> LIGHT_BLOCKING = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("light_blocking"), LightBlockingBlockConditionType::new));
	public static final ConditionConfiguration<LightLevelBlockConditionType> LIGHT_LEVEL = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("light_level"), LightLevelBlockConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<MovementBlockingBlockConditionType> MOVEMENT_BLOCKING = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("movement_blocking"), MovementBlockingBlockConditionType::new));
	public static final ConditionConfiguration<NbtBlockConditionType> NBT = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("nbt"), NbtBlockConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<ReplaceableBlockConditionType> REPLACEABLE = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("replaceable"), ReplaceableBlockConditionType::new));
	public static final ConditionConfiguration<SlipperinessBlockConditionType> SLIPPERINESS = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("slipperiness"), SlipperinessBlockConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<WaterLoggableBlockConditionType> WATER_LOGGABLE = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("water_loggable"), WaterLoggableBlockConditionType::new));

	public static void register() {

	}

	@SuppressWarnings("unchecked")
	public static <T extends BlockConditionType> ConditionConfiguration<T> register(ConditionConfiguration<T> configuration) {

		ConditionConfiguration<BlockConditionType> casted = (ConditionConfiguration<BlockConditionType>) configuration;
		Registry.register(ApoliRegistries.BLOCK_CONDITION_TYPE, casted.id(), casted);

		return configuration;

	}

}
