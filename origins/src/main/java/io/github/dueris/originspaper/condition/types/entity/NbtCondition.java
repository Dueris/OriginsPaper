package io.github.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class NbtCondition {

	public static @NotNull ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("nbt"),
			SerializableData.serializableData()
				.add("nbt", SerializableDataTypes.NBT_COMPOUND),
			(data, entity) -> {
				CompoundTag nbt = new CompoundTag();
				entity.saveWithoutId(nbt);
				return NbtUtils.compareNbt(data.get("nbt"), nbt, true);
			}
		);
	}
}
