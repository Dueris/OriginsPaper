package io.github.dueris.originspaper.condition.types;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import io.github.dueris.originspaper.condition.ConditionTypes;
import io.github.dueris.originspaper.condition.meta.MetaConditions;
import io.github.dueris.originspaper.condition.types.multi.DistanceFromCoordinatesConditionRegistry;
import io.github.dueris.originspaper.registry.Registries;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;


public class BlockConditions {

	public static void registerAll() {
		MetaConditions.register(Registries.BLOCK_CONDITION, BlockConditions::register);
		DistanceFromCoordinatesConditionRegistry.registerBlockCondition(BlockConditions::register);
		ConditionTypes.registerPackage(BlockConditions::register, "io.github.dueris.originspaper.condition.types.block");
	}

	public static void register(@NotNull ConditionTypeFactory<BlockInWorld> factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.BLOCK_CONDITION).register(factory, factory.getSerializerId());
	}

}
