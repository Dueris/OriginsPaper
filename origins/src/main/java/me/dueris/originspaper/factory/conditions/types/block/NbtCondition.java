package me.dueris.originspaper.factory.conditions.types.block;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
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
