package io.github.dueris.originspaper.condition.type.block;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.BlockConditionType;
import io.github.dueris.originspaper.condition.type.BlockConditionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class InTagBlockConditionType extends BlockConditionType {

	public static final TypedDataObjectFactory<InTagBlockConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("tag", SerializableDataTypes.BLOCK_TAG),
		data -> new InTagBlockConditionType(
			data.get("tag")
		),
		(conditionType, serializableData) -> serializableData.instance()
			.set("tag", conditionType.tag)
	);

	private final TagKey<Block> tag;

	public InTagBlockConditionType(TagKey<Block> tag) {
		this.tag = tag;
	}

	@Override
	public boolean test(Level world, BlockPos pos, BlockState blockState, Optional<BlockEntity> blockEntity) {
		return blockState.is(tag);
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return BlockConditionTypes.IN_TAG;
	}

}
