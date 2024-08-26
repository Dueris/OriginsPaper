package io.github.dueris.originspaper.condition.types.block;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;

public class NbtCondition {

	public static @NotNull ConditionTypeFactory<BlockInWorld> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("nbt"),
			SerializableData.serializableData()
				.add("nbt", SerializableDataTypes.NBT_COMPOUND),
			(data, block) -> {
				CompoundTag nbt = new CompoundTag();
				if (block.getEntity() != null) {
					nbt = block.getEntity().saveWithFullMetadata(block.getLevel().registryAccess());
				}
				return NbtUtils.compareNbt(data.get("nbt"), nbt, true);
			}
		);
	}
}
