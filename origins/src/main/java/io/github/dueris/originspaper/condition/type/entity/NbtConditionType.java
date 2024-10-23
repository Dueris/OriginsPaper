package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.Entity;

public class NbtConditionType {

	public static boolean condition(Entity entity, CompoundTag nbt) {
		return NbtUtils.compareNbt(nbt, entity.saveWithoutId(new CompoundTag()), true);
	}

	public static ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("nbt"),
			new SerializableData()
				.add("nbt", SerializableDataTypes.NBT_COMPOUND),
			(data, entity) -> condition(entity,
				data.get("nbt")
			)
		);
	}

}
