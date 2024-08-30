package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class PowerTypeConditionType {

	public static boolean condition(@NotNull Entity entity, ResourceLocation powerTypeFactory) {
		return PowerHolderComponent.hasPowerType(entity.getBukkitEntity(), powerTypeFactory);
	}

	public static @NotNull ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("power_type"),
			new SerializableData()
				.add("power_type", SerializableDataTypes.IDENTIFIER),
			(data, entity) -> condition(entity,
				data.get("power_type")
			)
		);
	}

}
