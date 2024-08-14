package io.github.dueris.originspaper.condition.types.block;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;

public class NbtCondition {

	public static @NotNull ConditionFactory<BlockInWorld> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("nbt"),
			InstanceDefiner.instanceDefiner()
				.add("nbt", SerializableDataTypes.NBT),
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
