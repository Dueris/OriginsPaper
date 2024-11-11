package io.github.dueris.originspaper.condition.type.block.meta;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.BlockConditionType;
import io.github.dueris.originspaper.condition.type.BlockConditionTypes;
import io.github.dueris.originspaper.condition.type.meta.ConstantMetaConditionType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ConstantBlockConditionType extends BlockConditionType implements ConstantMetaConditionType {

	private final boolean value;

	public ConstantBlockConditionType(boolean value) {
		this.value = value;
	}

	@Override
	public boolean test(Level world, BlockPos pos, BlockState blockState, Optional<BlockEntity> blockEntity) {
		return value();
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return BlockConditionTypes.CONSTANT;
	}

	@Override
	public boolean value() {
		return value;
	}

}
