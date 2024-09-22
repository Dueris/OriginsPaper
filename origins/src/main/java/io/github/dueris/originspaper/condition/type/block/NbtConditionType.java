package io.github.dueris.originspaper.condition.type.block;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;

public class NbtConditionType {

	public static boolean condition(@NotNull BlockInWorld cachedBlock, CompoundTag nbt) {

		RegistryAccess registryManager = cachedBlock.getLevel().registryAccess();
		BlockEntity blockEntity = cachedBlock.getEntity();

		return blockEntity != null
			&& NbtUtils.compareNbt(nbt, blockEntity.saveWithFullMetadata(registryManager), true);

	}

	public static @NotNull ConditionTypeFactory<BlockInWorld> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("nbt"),
			new SerializableData()
				.add("nbt", SerializableDataTypes.NBT_COMPOUND),
			(data, cachedBlock) -> condition(cachedBlock,
				data.get("nbt")
			)
		);
	}

}
