package io.github.dueris.originspaper.condition.factory;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.type.block.*;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.registry.ApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class BlockConditions {

	public static void register() {
		MetaConditions.register(ApoliDataTypes.BLOCK_CONDITION, BlockConditions::register);
		register(OffsetConditionType.getFactory());
		register(HeightConditionType.getFactory());
		DistanceFromCoordinatesConditionRegistry.registerBlockCondition(BlockConditions::register);
		register(BlockConditionType.getFactory());
		register(InTagConditionType.getFactory());
		register(AdjacentConditionType.getFactory());
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("replaceable"), (cachedBlock) -> {
			return cachedBlock.getState().canBeReplaced();
		}));
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("attachable"), AttachableConditionType::condition));
		register(FluidConditionType.getFactory());
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("movement_blocking"), MovementBlockingConditionType::condition));
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("light_blocking"), (cachedBlock) -> {
			return cachedBlock.getState().canOcclude();
		}));
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("water_loggable"), (cachedBlock) -> {
			return cachedBlock.getState().getBlock() instanceof LiquidBlockContainer;
		}));
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("exposed_to_sky"), (cachedBlock) -> {
			return cachedBlock.getLevel().canSeeSky(cachedBlock.getPos());
		}));
		register(LightLevelConditionType.getFactory());
		register(BlockStateConditionType.getFactory());
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("block_entity"), (cachedBlock) -> {
			return cachedBlock.getEntity() != null;
		}));
		register(NbtConditionType.getFactory());
		register(SlipperinessConditionType.getFactory());
		register(BlastResistanceConditionType.getFactory());
		register(HardnessConditionType.getFactory());
		register(CommandConditionType.getFactory());
	}

	public static @NotNull ConditionTypeFactory<BlockInWorld> createSimpleFactory(ResourceLocation id, Predicate<BlockInWorld> condition) {
		return new ConditionTypeFactory<>(id, new SerializableData(), (data, cachedBlock) -> {
			return condition.test(cachedBlock);
		});
	}

	public static @NotNull ConditionTypeFactory<BlockInWorld> register(ConditionTypeFactory<BlockInWorld> conditionFactory) {
		return Registry.register(ApoliRegistries.BLOCK_CONDITION, conditionFactory.getSerializerId(), conditionFactory);
	}
}
