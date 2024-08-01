package me.dueris.originspaper.factory.condition.types;

import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.condition.ConditionFactory;
import me.dueris.originspaper.factory.condition.Conditions;
import me.dueris.originspaper.factory.condition.meta.MetaConditions;
import me.dueris.originspaper.factory.condition.types.multi.DistanceFromCoordinatesConditionRegistry;
import me.dueris.originspaper.registry.Registries;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;


public class BlockConditions {

	public static void registerAll() {
		MetaConditions.register(Registries.BLOCK_CONDITION, BlockConditions::register);
		DistanceFromCoordinatesConditionRegistry.registerBlockCondition(BlockConditions::register);
		Conditions.registerPackage(BlockConditions::register, "me.dueris.originspaper.factory.condition.types.block");
	}

	public static void register(@NotNull ConditionFactory<BlockInWorld> factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.BLOCK_CONDITION).register(factory, factory.getSerializerId());
	}

}
