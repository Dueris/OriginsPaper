package io.github.dueris.originspaper.condition.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.util.IdentifierAlias;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.condition.factory.DistanceFromCoordinatesConditionRegistry;
import io.github.dueris.originspaper.condition.type.block.*;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.registry.ApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

import java.util.function.Predicate;

public class BlockConditionTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();

	public static void register() {
		MetaConditionTypes.register(ApoliDataTypes.BLOCK_CONDITION, BlockConditionTypes::register);
		register(OffsetConditionType.getFactory());

		register(HeightConditionType.getFactory());
		DistanceFromCoordinatesConditionRegistry.registerBlockCondition(BlockConditionTypes::register);
		register(BlockConditionType.getFactory());
		register(InTagConditionType.getFactory());
		register(AdjacentConditionType.getFactory());
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("replaceable"), cachedBlock -> cachedBlock.getState().canBeReplaced()));
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("attachable"), AttachableConditionType::condition));
		register(FluidConditionType.getFactory());
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("movement_blocking"), MovementBlockingConditionType::condition));
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("light_blocking"), cachedBlock -> cachedBlock.getState().canOcclude()));
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("water_loggable"), cachedBlock -> cachedBlock.getState().getBlock() instanceof LiquidBlockContainer));
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("exposed_to_sky"), cachedBlock -> cachedBlock.getLevel().canSeeSky(cachedBlock.getPos())));
		register(LightLevelConditionType.getFactory());
		register(BlockStateConditionType.getFactory());
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("block_entity"), cachedBlock -> cachedBlock.getEntity() != null));
		register(NbtConditionType.getFactory());
		register(SlipperinessConditionType.getFactory());
		register(BlastResistanceConditionType.getFactory());
		register(HardnessConditionType.getFactory());
		register(CommandConditionType.getFactory());
	}

	public static ConditionTypeFactory<BlockInWorld> createSimpleFactory(ResourceLocation id, Predicate<BlockInWorld> condition) {
		return new ConditionTypeFactory<>(id, new SerializableData(), (data, cachedBlock) -> condition.test(cachedBlock));
	}

	public static <F extends ConditionTypeFactory<BlockInWorld>> F register(F conditionFactory) {
		return Registry.register(ApoliRegistries.BLOCK_CONDITION, conditionFactory.getSerializerId(), conditionFactory);
	}

}
